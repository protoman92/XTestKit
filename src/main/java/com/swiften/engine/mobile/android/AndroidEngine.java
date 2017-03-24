package com.swiften.engine.mobile.android;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.engine.base.param.ConnectionParam;
import com.swiften.engine.base.param.NavigateBack;
import com.swiften.engine.base.param.StartEnvParam;
import com.swiften.engine.base.param.StopEnvParam;
import com.swiften.engine.mobile.MobileEngine;
import com.swiften.engine.mobile.Platform;
import com.swiften.engine.mobile.android.param.DeviceSettingParam;
import com.swiften.engine.mobile.android.protocol.AndroidDelay;
import com.swiften.engine.mobile.android.protocol.AndroidEngineError;
import com.swiften.util.ProcessRunner;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.reactivex.Flowable;
import io.reactivex.exceptions.Exceptions;
import org.jetbrains.annotations.NotNull;
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
    AndroidDriver<AndroidElement>
    >
    implements
    AndroidDelay,
    AndroidEngineError {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull String appActivity;

    AndroidEngine() {
        super();
        appActivity = "";
    }

    /**
     * @return A {@link Map} of capabilities.
     * @see MobileEngine#capabilities()
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        capabilities.put(AndroidMobileCapabilityType.APP_PACKAGE, appPackage);
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
            URL url = new URL(serverUrl);
            DesiredCapabilities capabilities = desiredCapabilities();
            return new AndroidDriver<>(url, capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get ${ANDROID_HOME} from Environment variables.
     * @return A {@link String} value.
     */
    @NotNull
    public String androidHome() {
        String androidHome = System.getenv("ANDROID_HOME");

        if (Objects.isNull(androidHome) || androidHome.isEmpty()) {
            throw new RuntimeException(ANDROID_HOME_NOT_SET);
        }

        return androidHome;
    }

    /**
     * Get path to adb.
     * @return A {@link String} value.
     */
    @NotNull
    public String adb() {
        return String.format("%s/platform-tools/adb", androidHome());
    }

    /**
     * Get path to adb shell CLI.
     * @return A {@link String} value.
     */
    @NotNull
    public String adbShell() {
        return String.format("%s shell", adb());
    }

    /**
     * Get path to Android emulator CLI.
     * @return A {@link String} value.
     */
    @NotNull
    public String emulator() {
        return String.format("%s/tools/emulator", androidHome());
    }

    //region Device Methods
    @NotNull
    public Flowable<Boolean> rxStartApp() {
        String start = String.format("%1$s am start -m %2$s/%3$s",
            androidHome(),
            appPackage,
            appActivity);

        return processRunner().rxExecute(start).map(a -> true);
    }

    //region Check Emulator Open
    /**
     * Command to get a list of attached devices.
     * @return A {@link String} value.
     */
    @NotNull
    public String adbDevices() {
        return String.format("%s devices -l", adb());
    }

    /**
     * Check if the specified emulator is open. This is a crude workaround
     * that assumes only once device is attached at any moment. Suitable only
     * for isolated tests.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxCheckEmulatorOpen() {
        String command = adbDevices();

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
     * Command to start an emulator whose name is {@link #deviceName}.
     * @return A {@link String} value.
     */
    @NotNull
    public String startEmulator() {
        return String.format("%1$s -avd %2$s", emulator(), deviceName);
    }

    /**
     * Command to check bootanim status from adb shell.
     * We can check whether the emulator is fully started by checking its
     * bootanim. If this value is 'stopped', the emulator has booted up
     * completely.
     * @return A {@link String} value.
     */
    @NotNull
    public String bootAnim() {
        return String.format("%s getprop init.svc.bootanim", adbShell());
    }

    /**
     * Start the emulator with the specified settings, mainly
     * {@link #deviceName} and {@link #testMode}. Detect when bootanim is
     * 'closed' and then emit value.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStartEmulator(@NotNull StartEnvParam param) {
        final ProcessRunner PROCESS_RUNNER = processRunner();
        final int RETRIES = param.retries();

        @SuppressWarnings("WeakerAccess")
        final long DELAY = emulatorBootRetryDelay();

        /* Append any error when starting up the emulator to this list, and
         * have retryWhen read it to determine whether to continue retrying */
        final List<Exception> ERRORS = new ArrayList<>();

        /* We need to start the emulator on a new Thread, or else it will
         * block the rest of the operations */
        new Thread(() -> {
            try {
                PROCESS_RUNNER.execute(startEmulator());
            } catch (Exception e) {
                ERRORS.add(e);
            }
        }).start();

        return PROCESS_RUNNER
            .rxExecute(bootAnim())
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
            .map(a -> true);
    }

    /**
     * Same as above, but uses a default {@link StartEnvParam} instance.
     * @return A {@link Flowable} instance.
     * @see #rxStartEmulator(StartEnvParam)
     */
    @NotNull
    public Flowable<Boolean> rxStartEmulator() {
        StartEnvParam param = StartEnvParam.newBuilder().build();
        return rxStartEmulator(param);
    }
    //endregion

    //region Stop Emulator
    /**
     * Command to shut down the emulator. Should not be used for actual
     * devices because this command will send a shutdown signal.
     * @return A {@link String} value.
     */
    @NotNull
    public String stopEmulator() {
        return String.format("%s reboot -p", adbShell());
    }

    /**
     * Shut down the emulator with {@link #stopEmulator()}.
     * @param param A {@link StopEnvParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStopEmulator(@NotNull StopEnvParam param) {
        String command = stopEmulator();

        return processRunner()
            .rxExecute(command)
            .retry(param.retries())
            .map(a -> true);
    }

    /**
     * Same as above, but uses a default {@link StopEnvParam} instance.
     * @return A {@link Flowable} instance.
     * @see #rxStopEmulator(StopEnvParam)
     */
    @NotNull
    public Flowable<Boolean> rxStopEmulator() {
        StopEnvParam param = StopEnvParam.newBuilder().build();
        return rxStopEmulator(param);
    }
    //endregion

    //region Toggle Internet Connection
    /**
     * The command to enable/disable internet connection.
     * @param param A {@link ConnectionParam} instance.
     * @return A {@link String} value.
     */
    @NotNull
    public String toggleConnectionCommand(@NotNull ConnectionParam param) {
        String append = param.enable() ? "enable" : "disable";
        return String.format("%1$s svc data %2$s", adbShell(), append);
    }

    /**
     * Disable internet connection for a rooted device. We should only use
     * this method for emulators since they are rooted by default.
     * @param param A {@link ConnectionParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxToggleInternetConnection(@NotNull ConnectionParam param) {
        String command = toggleConnectionCommand(param);
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
            .newBuilder()
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
            .newBuilder()
            .shouldEnable(false)
            .build();

        return rxToggleInternetConnection(param);
    }
    //endregion

    //region Start and Stop Test Environment
    /**
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxStartTestEnvironment(StartEnvParam)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxStartTestEnvironment(@NotNull StartEnvParam param) {
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
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxStopTestEnvironment(StopEnvParam)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxStopTestEnvironment(@NotNull StopEnvParam param) {
        switch (testMode()) {
            case EMULATOR:
                return rxStopEmulator(param);

            default:
                return Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
        }
    }
    //endregion

    //region Dismiss Keyboard
    /**
     * Command to check whether keyboard is open.
     * @return A {@link String} value.
     */
    @NotNull
    public String checkKeyboardOpen() {
        return String.format(
            "%s dumpsys window InputMethod | grep 'mHasSurface'",
            adbShell());
    }

    /**
     * Check whether the keyboard is open.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxCheckKeyboardOpen() {
        String command = checkKeyboardOpen();

        return processRunner().rxExecute(command)
            .filter(a -> a != null && !a.isEmpty())
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
     * Command to change device settings.
     * @param param a {@link DeviceSettingParam} instance.
     * @return A {@link String} value.
     */
    @NotNull
    public String putDeviceSettings(@NotNull DeviceSettingParam param) {
        return String.format("%1$s settings %2$s", adbShell(), param.putCommand());
    }

    /**
     * Command to get device settings.
     * @param param a {@link DeviceSettingParam} instance.
     * @return A {@link String} value.
     */
    @NotNull
    public String getDeviceSettings(@NotNull DeviceSettingParam param) {
        return String.format("%1$s settings %2$s", adbShell(), param.getCommand());
    }

    /**
     * Change emulator/device settings with
     * {@link #putDeviceSettings(DeviceSettingParam)}, and then check that the
     * value is set with {@link #getDeviceSettings(DeviceSettingParam)}.
     * @param PARAM A {@link DeviceSettingParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxChangeDeviceSettings(@NotNull final DeviceSettingParam PARAM) {
        final ProcessRunner RUNNER = processRunner();

        return RUNNER.rxExecute(putDeviceSettings(PARAM))
            .flatMap(a -> RUNNER.rxExecute(getDeviceSettings(PARAM)))
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
        return DeviceSettingParam.newBuilder()
            .withGlobalNameSpace()
            .withKey("window_animation_scale")
            .withValue("0")
            .build();
    }

    /**
     * Command to disable window animation scale.
     * @return A {@link String} value.
     */
    @NotNull
    public String disableWindowAnimationScale() {
        DeviceSettingParam param = disableWindowAnimationScaleParam();
        return putDeviceSettings(param);
    }

    /**
     * Command to get window animation scale.
     * @return A {@link String} value.
     */
    @NotNull
    public String getWindowAnimationScale() {
        DeviceSettingParam param = disableWindowAnimationScaleParam();
        return getDeviceSettings(param);
    }

    /**
     * Disable window animation scale.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxDisableWindowAnimationScale() {
        DeviceSettingParam param = disableWindowAnimationScaleParam();
        return rxChangeDeviceSettings(param);
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
        return DeviceSettingParam.newBuilder()
            .withGlobalNameSpace()
            .withKey("transition_animation_scale")
            .withValue("0")
            .build();
    }

    /**
     * Command to disable transition animation scale.
     * @return A {@link String} value.
     */
    @NotNull
    public String disableTransitionAnimationScale() {
        DeviceSettingParam param = disableTransitionAnimationScaleParam();
        return putDeviceSettings(param);
    }

    /**
     * Command to get transition animation scale.
     * @return A {@link String} value.
     */
    @NotNull
    public String getTransitionAnimationScale() {
        DeviceSettingParam param = disableTransitionAnimationScaleParam();
        return getDeviceSettings(param);
    }

    /**
     * Disable transition animation scale.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxDisableTransitionAnimationScale() {
        DeviceSettingParam param = disableTransitionAnimationScaleParam();
        return rxChangeDeviceSettings(param);
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
        return DeviceSettingParam.newBuilder()
            .withGlobalNameSpace()
            .withKey("animator_duration_scale")
            .withValue("0")
            .build();
    }

    /**
     * Command to disable animator duration scale.
     * @return A {@link String} value.
     */
    @NotNull
    public String disableAnimatorDurationScale() {
        DeviceSettingParam param = disableAnimatorDurationScaleParam();
        return putDeviceSettings(param);
    }

    /**
     * Command to get animator duration scale.
     * @return A {@link String} value.
     */
    @NotNull
    public String getAnimatorDurationScale() {
        DeviceSettingParam param = disableAnimatorDurationScaleParam();
        return getDeviceSettings(param);
    }

    /**
     * Disable animator duration scale.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxDisableAnimatorDurationScale() {
        DeviceSettingParam param = disableAnimatorDurationScaleParam();
        return rxChangeDeviceSettings(param);
    }
    //endregion

    //region Disable Emulator Animations
    /**
     * Return an Array of {@link String} commands to disable emulator
     * animations.
     * @return An Array of {@link String}.
     */
    @NotNull
    public String[] disableAnimationCommands() {
        return new String[] {
            disableWindowAnimationScale(),
            disableTransitionAnimationScale(),
            disableAnimatorDurationScale()
        };
    }

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
            return super.build();
        }
    }
}
