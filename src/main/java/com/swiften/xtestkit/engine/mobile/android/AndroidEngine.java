package com.swiften.xtestkit.engine.mobile.android;

import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.*;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.MobileEngine;
import com.swiften.xtestkit.engine.base.Platform;
import com.swiften.xtestkit.engine.mobile.android.param.DeviceSettingParam;
import com.swiften.xtestkit.engine.mobile.android.protocol.AndroidDelayProtocol;
import com.swiften.xtestkit.engine.mobile.android.protocol.AndroidErrorProtocol;
import com.swiften.xtestkit.system.NetworkHandler;
import com.swiften.xtestkit.system.ProcessRunner;
import com.swiften.xtestkit.util.Log;
import com.swiften.xtestkit.util.StringUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.exceptions.Exceptions;
import org.apache.bcel.generic.RET;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haipham on 3/22/17.
 */
public class AndroidEngine extends MobileEngine<
    AndroidElement,
    AndroidDriver<AndroidElement>>
    implements
    AndroidDelayProtocol,
    AndroidErrorProtocol {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull String appActivity;

    AndroidEngine() {
        super();
        appActivity = "";
    }

    //region TestListener
    @NotNull
    @Override
    public Flowable<Boolean> rxOnFreshStart() {
        /* We restart adb server at the start of all tests to avoid problems
         * with inactive adb instances */
        return super.rxOnFreshStart().flatMap(a -> rxRestartAdb());
    }
    //endregion

    //region Getters
    /**
     * Return {@link #appActivity}. This can be stubbed out for custom
     * implementation.
     * @return A {@link String} value.
     */
    @NotNull
    public String appActivity() {
        return appActivity;
    }
    //endregion

    //region Test Setup
    /**
     * @param param A {@link BeforeClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeClass(BeforeClassParam)
     * @see #rxStartEmulator()
     * @see #rxDisableEmulatorAnimations()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
        switch (testMode()) {
            case EMULATOR:
                return rxStartEmulator(param)
                    /* Disable animations to avoid erratic behaviors */
                    .flatMap(a -> rxDisableEmulatorAnimations());

            default:
                return Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
        }
    }

    /**
     * @param param A {@link AfterClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterClass(AfterClassParam)
     * @see #rxStopEmulator(RetryProtocol)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        switch (testMode()) {
            case EMULATOR:
                return rxStopEmulator(param);

            default:
                return Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
        }
    }

    /**
     * @param param A {@link BeforeParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeMethod(BeforeParam)
     * @see #rxStartDriver()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxBeforeMethod(@NotNull BeforeParam param) {
        return rxStartDriver();
    }

    /**
     * @param param A {@link AfterParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterMethod(AfterParam)
     * @see #rxStopDriver()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxAfterMethod(@NotNull AfterParam param) {
        return rxStopDriver();
    }
    //endregion

    //region Appium Setup
    /**
     * @return A {@link Map} of capabilities.
     * @see MobileEngine#capabilities()
     * @see #appPackage()
     * @see #appActivity()
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        capabilities.put(AndroidMobileCapabilityType.APP_PACKAGE, appPackage());
        capabilities.put(AndroidMobileCapabilityType.APP_ACTIVITY, appActivity());
        return capabilities;
    }

    /**
     * @return An {@link AndroidDriver} instance.
     * @see PlatformEngine#createDriverInstance()
     */
    @NotNull
    @Override
    protected AndroidDriver<AndroidElement> createDriverInstance() {
        try {
            URL url = new URL(serverUri());
            DesiredCapabilities capabilities = desiredCapabilities();
            return new AndroidDriver<>(url, capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region Adb setup
    /**
     * Restart adb server in order to avoid problem with adb not acknowledging
     * the first command at the start of a test batch.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxRestartAdb() {
        final ProcessRunner RUNNER = processRunner();
        NetworkHandler handler = networkHandler();

        return handler
            .rxKillProcessWithName("adb")
            .onErrorResumeNext(Flowable.just(true))
            .map(a -> cmLaunchAdb())
            .flatMap(RUNNER::rxExecute)
            .map(a -> true);
    }
    //endregion

    //region CLI commands
    /**
     * Get ${ANDROID_HOME} from Environment variables.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmAndroidHome() {
        String androidHome = System.getenv("ANDROID_HOME");

        if (StringUtil.isNotNullOrEmpty(androidHome)) {
            return androidHome;
        }

        throw new RuntimeException(ANDROID_HOME_NOT_SET);
    }

    /**
     * Get path to adb.
     * @return A {@link String} value.
     * @see #cmAndroidHome()
     */
    @NotNull
    public String cmAdb() {
        return String.format("%s/platform-tools/adb", cmAndroidHome());
    }

    /**
     * Command to launch adb.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmLaunchAdb() {
        return String.format("%s start-server", cmAdb());
    }

    /**
     * Get path to adb shell CLI.
     * @return A {@link String} value.
     * @see #cmAdb()
     */
    @NotNull
    public String cmAdbShell() {
        return String.format("%s shell", cmAdb());
    }

    /**
     * Get path to Android emulator CLI.
     * @return A {@link String} value.
     * @see #cmAndroidHome()
     */
    @NotNull
    public String cmEmulator() {
        return String.format("%s/tools/emulator", cmAndroidHome());
    }

    /**
     * Command to get a list of attached devices.
     * @return A {@link String} value.
     * @see #cmAdb()
     */
    @NotNull
    public String cmAdbDevices() {
        return String.format("%s devices -l", cmAdb());
    }

    /**
     * Command to start an emulator whose name is {@link #deviceName}.
     * @return A {@link String} value.
     * @see #cmEmulator()
     * @see #deviceName()
     */
    @NotNull
    public String cmStartEmulator() {
        return String.format("%1$s -avd %2$s", cmEmulator(), deviceName());
    }

    /**
     * Command to check bootanim status from adb shell.
     * We can check whether the emulator is fully started by checking its
     * bootanim. If this value is 'stopped', the emulator has booted up
     * completely.
     * @return A {@link String} value.
     * @see #cmAdbShell()
     */
    @NotNull
    public String cmBootAnim() {
        return String.format("%s getprop init.svc.bootanim", cmAdbShell());
    }

    /**
     * Command to shut down the emulator. Should not be used for actual
     * devices because this command will send a shutdown signal.
     * @return A {@link String} value.
     * @see #cmAdbShell()
     */
    @NotNull
    public String cmStopEmulator() {
        return String.format("%s reboot -p", cmAdbShell());
    }

    /**
     * The command to enable/disable internet connection.
     * @param param A {@link ConnectionParam} instance.
     * @return A {@link String} value.
     * @see #cmAdbShell()
     */
    @NotNull
    public String cmToggleConnection(@NotNull ConnectionParam param) {
        String append = param.enable() ? "enable" : "disable";
        return String.format("%1$s svc data %2$s", cmAdbShell(), append);
    }

    /**
     * Command to check whether keyboard is open.
     * @return A {@link String} value.
     * @see #cmAdbShell()
     */
    @NotNull
    public String cmCheckKeyboardOpen() {
        return String.format("%s dumpsys window InputMethod", cmAdbShell());
    }

    /**
     * Command to change device settings.
     * @param param a {@link DeviceSettingParam} instance.
     * @return A {@link String} value.
     * @see #cmAdbShell()
     */
    @NotNull
    public String cmPutSettings(@NotNull DeviceSettingParam param) {
        return String.format("%1$s settings %2$s", cmAdbShell(), param.putCommand());
    }

    /**
     * Command to get device settings.
     * @param param a {@link DeviceSettingParam} instance.
     * @return A {@link String} value.
     * @see #cmAdbShell()
     */
    @NotNull
    public String cmGetSettings(@NotNull DeviceSettingParam param) {
        return String.format("%1$s settings %2$s", cmAdbShell(), param.getCommand());
    }
    //endregion

    //region Device Methods
    /**
     * Since {@link WebDriver.TargetLocator#alert()} is not yet implemented
     * on {@link Platform#ANDROID}, we need a custom solution by using
     * {@link AndroidDriver#findElementById(String)}.
     * @param param An {@link AlertParam} instance.
     * @return A {@link Flowable} instance.
     * @see #driver()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxDismissAlert(@NotNull AlertParam param) {
        return Flowable.just(param.shouldAccept())
            .map(a -> a ? "permission_allow_button" : "permission_deny_button")
            .map(id -> String.format("com.android.packageinstaller:id/%s", id))
            .map(id -> driver().findElement(By.id(id)))
            .filter(Objects::nonNull)
            .switchIfEmpty(Flowable.error(new Exception(NO_SUCH_ELEMENT)))
            .flatMapCompletable(a -> Completable.fromAction(a::click))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    //region Check Emulator Open
    /**
     * Check if the specified emulator is open. This is a crude workaround
     * that assumes only once device is attached at any moment. Suitable only
     * for isolated tests.
     * @return A {@link Flowable} instance.
     * @see #cmAdbDevices()
     */
    @NotNull
    public Flowable<Boolean> rxCheckEmulatorOpen() {
        String command = cmAdbDevices();

        return processRunner()
            .rxExecute(command)
            .map(a -> a.split("\n"))
            .filter(a -> a.length >= 2)
            .map(a -> true)
            .defaultIfEmpty(false);
    }
    //endregion

    //region Start Emulator
    /**
     * Start the emulator with the specified settings, mainly
     * {@link #deviceName} and {@link #testMode}. Detect when bootanim is
     * 'closed' and then emit value.
     * @return A {@link Flowable} instance.
     * @see #cmStartEmulator()
     * @see #cmBootAnim()
     */
    @NotNull
    public Flowable<Boolean> rxStartEmulator(@NotNull RetryProtocol param) {
        final ProcessRunner PROCESS_RUNNER = processRunner();
        final int RETRIES = param.maxRetries();

        @SuppressWarnings("WeakerAccess")
        final long DELAY = emulatorBootRetryDelay();

        @SuppressWarnings("WeakerAccess")
        final long TIMEOUT = emulatorBoothTimeout();

        /* Append any error when starting up the emulator to this list, and
         * have retryWhen read it to determine whether to continue retrying */
        final List<Exception> ERRORS = new ArrayList<>();

        /* We need to start the emulator on a new Thread, or else it will
         * block the rest of the operations */
        new Thread(() -> {
            try {
                PROCESS_RUNNER.execute(cmStartEmulator());
            } catch (Exception e) {
                ERRORS.add(e);
            }
        }).start();

        return PROCESS_RUNNER
            .rxExecute(cmBootAnim())
            .filter(Objects::nonNull)
            .map(String::trim)

            /* There are two errors that can be thrown here:
             * When the emulator is first started: no devices/emulators found
             * When the emulator is booting up: device unauthorized
             * Afterwards, when the emulator has booted up fully, a value
             * 'stopped' will be emitted */
            .filter(a -> a.equals("stopped"))
            .switchIfEmpty(Flowable.error(new Exception()))
            .retryWhen(a -> a
                /* We add one to the retry count, or else we won't be able
                 * to catch when the count is exceeded */
                .zipWith(Flowable.range(1, RETRIES + 1), (e, i) -> {
                    if (i > RETRIES) {
                        throw Exceptions.propagate(e);
                    } else {
                        return e;
                    }
                })
                .flatMap(e -> {
                    if (ERRORS.isEmpty()) {
                        return Flowable.timer(DELAY, TimeUnit.MILLISECONDS);
                    } else {
                        return Flowable.<Long>error(e);
                    }
                }))
            .map(a -> true)

            /* timeout is used here in case processRunner() fails to poll
             * for bootanim. The timeout will be a reasonable value so that,
             * at the end of the interval, the emulator would have been
             * started up anyway */
            .timeout(TIMEOUT, TimeUnit.MILLISECONDS, Flowable.just(true));
    }

    /**
     * Same as above, but uses a default {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #rxStartEmulator(RetryProtocol)
     */
    @NotNull
    public Flowable<Boolean> rxStartEmulator() {
        return rxStartEmulator(new RetryProtocol() {
            @Override
            public int minRetries() {
                /* We have to use a large retry count because starting an
                 * emulator may take quite a while. A low retry count will
                 * fail the bootanim test rather quickly */
                return 100;
            }
        });
    }
    //endregion

    //region Stop Emulator
    /**
     * Shut down the emulator with {@link #cmStopEmulator()}.
     * @param param A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #cmStopEmulator()
     */
    @NotNull
    public Flowable<Boolean> rxStopEmulator(@NotNull RetryProtocol param) {
        String command = cmStopEmulator();

        return processRunner()
            .rxExecute(command)
            .retry(param.minRetries())
            .map(a -> true);
    }

    /**
     * Same as above, but uses a default {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #rxStopEmulator(RetryProtocol)
     */
    @NotNull
    public Flowable<Boolean> rxStopEmulator() {
        return rxStopEmulator(RetryProtocol.DEFAULT);
    }
    //endregion

    //region Toggle Internet Connection
    /**
     * Disable internet connection for a rooted device. We should only use
     * this method for emulators since they are rooted by default.
     * @param param A {@link ConnectionParam} instance.
     * @return A {@link Flowable} instance.
     * @see #cmToggleConnection(ConnectionParam)
     */
    @NotNull
    public Flowable<Boolean> rxToggleInternetConnection(@NotNull ConnectionParam param) {
        String command = cmToggleConnection(param);
        return processRunner().rxExecute(command)
            /* If successful, there should be no output */
            .filter(String::isEmpty)
            .map(a -> true)
            .switchIfEmpty(Flowable.error(new Exception(NO_OUTPUT_EXPECTED)));
    }

    /**
     * Same as above, but uses a default {@link ConnectionParam}.
     * @return A {@link Flowable} instance.
     * @see #rxToggleInternetConnection(ConnectionParam)
     */
    @NotNull
    public Flowable<Boolean> rxEnableInternetConnection() {
        ConnectionParam param = ConnectionParam
            .builder()
            .shouldEnable(true)
            .build();

        return rxToggleInternetConnection(param);
    }

    /**
     * Same as above, but uses a default {@link ConnectionParam}.
     * @return A {@link Flowable} instance.
     * @see #rxToggleInternetConnection(ConnectionParam)
     */
    @NotNull
    public Flowable<Boolean> rxDisableInternetConnection() {
        ConnectionParam param = ConnectionParam
            .builder()
            .shouldEnable(false)
            .build();

        return rxToggleInternetConnection(param);
    }
    //endregion

    //region Dismiss Keyboard
    /**
     * Check whether the keyboard is open.
     * @return A {@link Flowable} instance.
     * @see #cmCheckKeyboardOpen()
     */
    @NotNull
    public Flowable<Boolean> rxCheckKeyboardOpen() {
        String command = cmCheckKeyboardOpen();

        return processRunner().rxExecute(command)
            .filter(StringUtil::isNotNullOrEmpty)
            .map(output -> {
                String regex = "mHasSurface=(\\w+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(output);

                if (matcher.find()) {
                    return matcher.group(1);
                } else {
                    return "false";
                }
            })
            .map(Boolean::valueOf)

            /* If the output is empty, the keyboard is not open */
            .defaultIfEmpty(false);
    }

    /**
     * Dismiss the keyboard if it is open. We first need to check whether the
     * keyboard is present with {@link #rxCheckKeyboardOpen()}, and then call
     * {@link #rxNavigateBack(NavigateBack)}.
     * @return A {@link Flowable} instance.
     * @see #rxCheckKeyboardOpen()
     * @see #rxNavigateBack(NavigateBack)
     */
    @NotNull
    public Flowable<Boolean> rxDismissKeyboard() {
        return rxCheckKeyboardOpen()
            .filter(isOpen -> isOpen)
            .flatMap(a -> rxNavigateBack())
            .defaultIfEmpty(true);
    }
    //endregion

    //region Change Device Settings
    /**
     * Change emulator/device settings with
     * {@link #cmPutSettings(DeviceSettingParam)}, and then check that the
     * value is set with {@link #cmGetSettings(DeviceSettingParam)}.
     * @param PARAM A {@link DeviceSettingParam} instance.
     * @return A {@link Flowable} instance.
     * @see #cmPutSettings(DeviceSettingParam)
     * @see #cmGetSettings(DeviceSettingParam)
     */
    @NotNull
    public Flowable<Boolean> rxChangeSettings(@NotNull final DeviceSettingParam PARAM) {
        final ProcessRunner RUNNER = processRunner();

        return RUNNER.rxExecute(cmPutSettings(PARAM))
            .flatMap(a -> RUNNER.rxExecute(cmGetSettings(PARAM)))
            .filter(a -> a.contains(PARAM.value()))
            .map(a -> true)
            /* Throw error if the returned value does not match the new
             * setting value */
            .switchIfEmpty(Flowable.error(new Exception(changeSettingsFailed(PARAM.key()))));
    }
    //endregion

    //region Disable Window Animation Scale
    /**
     * Construct a {@link DeviceSettingParam} to disable window animation
     * scale.
     * @return A {@link DeviceSettingParam} instance.
     */
    @NotNull
    public DeviceSettingParam disableWindowAnimationScaleParam() {
        return DeviceSettingParam.builder()
            .withGlobalNameSpace()
            .withKey("window_animation_scale")
            .withValue("0")
            .build();
    }

    /**
     * Command to disable window animation scale.
     * @return A {@link String} value.
     * @see #disableWindowAnimationScaleParam()
     */
    @NotNull
    public String disableWindowAnimationScale() {
        DeviceSettingParam param = disableWindowAnimationScaleParam();
        return cmPutSettings(param);
    }

    /**
     * Command to get window animation scale.
     * @return A {@link String} value.
     * @see #disableWindowAnimationScaleParam()
     */
    @NotNull
    public String getWindowAnimationScale() {
        DeviceSettingParam param = disableWindowAnimationScaleParam();
        return cmGetSettings(param);
    }

    /**
     * Disable window animation scale.
     * @return A {@link Flowable} instance.
     * @see #disableWindowAnimationScaleParam()
     */
    @NotNull
    public Flowable<Boolean> rxDisableWindowAnimationScale() {
        DeviceSettingParam param = disableWindowAnimationScaleParam();
        return rxChangeSettings(param);
    }
    //endregion

    //region Disable Transition Animation Scale
    /**
     * Construct a {@link DeviceSettingParam} to disable transition animation
     * scale.
     * @return A {@link DeviceSettingParam} instance.
     */
    @NotNull
    public DeviceSettingParam disableTransitionAnimationScaleParam() {
        return DeviceSettingParam.builder()
            .withGlobalNameSpace()
            .withKey("transition_animation_scale")
            .withValue("0")
            .build();
    }

    /**
     * Command to disable transition animation scale.
     * @return A {@link String} value.
     * @see #disableTransitionAnimationScaleParam()
     */
    @NotNull
    public String disableTransitionAnimationScale() {
        DeviceSettingParam param = disableTransitionAnimationScaleParam();
        return cmPutSettings(param);
    }

    /**
     * Command to get transition animation scale.
     * @return A {@link String} value.
     * @see #disableTransitionAnimationScaleParam()
     */
    @NotNull
    public String getTransitionAnimationScale() {
        DeviceSettingParam param = disableTransitionAnimationScaleParam();
        return cmGetSettings(param);
    }

    /**
     * Disable transition animation scale.
     * @return A {@link Flowable} instance.
     * @see #disableTransitionAnimationScaleParam()
     */
    @NotNull
    public Flowable<Boolean> rxDisableTransitionAnimationScale() {
        DeviceSettingParam param = disableTransitionAnimationScaleParam();
        return rxChangeSettings(param);
    }
    //endregion

    //region Disable Animator Duration Scale
    /**
     * Construct a {@link DeviceSettingParam} to disable animator duration
     * scale.
     * @return A {@link DeviceSettingParam} instance.
     */
    @NotNull
    public DeviceSettingParam disableAnimatorDurationScaleParam() {
        return DeviceSettingParam.builder()
            .withGlobalNameSpace()
            .withKey("animator_duration_scale")
            .withValue("0")
            .build();
    }

    /**
     * Command to disable animator duration scale.
     * @return A {@link String} value.
     * @see #disableAnimatorDurationScaleParam()
     */
    @NotNull
    public String disableAnimatorDurationScale() {
        DeviceSettingParam param = disableAnimatorDurationScaleParam();
        return cmPutSettings(param);
    }

    /**
     * Command to get animator duration scale.
     * @return A {@link String} value.
     * @see #disableAnimatorDurationScaleParam()
     */
    @NotNull
    public String getAnimatorDurationScale() {
        DeviceSettingParam param = disableAnimatorDurationScaleParam();
        return cmGetSettings(param);
    }

    /**
     * Disable animator duration scale.
     * @return A {@link Flowable} instance.
     * @see #disableAnimatorDurationScaleParam()
     */
    @NotNull
    public Flowable<Boolean> rxDisableAnimatorDurationScale() {
        DeviceSettingParam param = disableAnimatorDurationScaleParam();
        return rxChangeSettings(param);
    }
    //endregion

    //region Disable Emulator Animations
    /**
     * Return an Array of {@link String} commands to disable emulator
     * animations.
     * @return An Array of {@link String}.
     * @see #disableWindowAnimationScale()
     * @see #disableTransitionAnimationScale()
     * @see #disableAnimatorDurationScale()
     */
    @NotNull
    public String[] disableAnimationCommands() {
        return new String[] {
            disableWindowAnimationScale(),
            disableTransitionAnimationScale(),
            disableAnimatorDurationScale()
        };
    }

    /**
     * Return an Array of {@link String} commands to get emulator animations.
     * @return An Array of {@link String}.
     * @see #getWindowAnimationScale()
     * @see #getTransitionAnimationScale()
     * @see #getAnimatorDurationScale()
     */
    @NotNull
    public String[] getAnimationValuesCommands() {
        return new String[] {
            getWindowAnimationScale(),
            getTransitionAnimationScale(),
            getAnimatorDurationScale()
        };
    }

    /**
     * Disable emulator animations for UI tests to prevent unexpected wait
     * times. Note that this is only applicable for rooted devices, and
     * emulators are rooted by default.
     * @return A {@link Flowable} instance.
     * @see #rxDisableWindowAnimationScale()
     * @see #rxDisableTransitionAnimationScale()
     * @see #rxDisableAnimatorDurationScale()
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxDisableEmulatorAnimations() {
        return Flowable
            .mergeArray(
                rxDisableWindowAnimationScale(),
                rxDisableTransitionAnimationScale(),
                rxDisableAnimatorDurationScale()
            )
            .toList()
            .toFlowable()
            .map(a -> true);
    }
    //endregion
    //endregion

    //region Builder
    public static final class Builder extends MobileEngine.Builder<AndroidEngine> {
        @NotNull
        @Override
        protected AndroidEngine createEngineInstance() {
            return new AndroidEngine();
        }

        /**
         * Set the {@link #ENGINE#appActivity} value. This value is used to
         * determine which Activity is started first.
         * @param appActivity A {@link String} value.
         * @return A {@link Builder} instance.
         */
        @NotNull
        public Builder withAppActivity(@NotNull String appActivity) {
            ENGINE.appActivity = appActivity;
            return this;
        }

        @NotNull
        @Override
        public AndroidEngine build() {
            withPlatform(Platform.ANDROID);
            withPlatformView(new AndroidView());
            return super.build();
        }
    }
    //endregion
}
