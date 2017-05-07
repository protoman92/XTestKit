package org.swiften.xtestkit.engine.mobile.android;

import org.swiften.xtestkit.engine.base.AppPackageProtocol;
import org.swiften.xtestkit.engine.base.RetryProtocol;
import org.swiften.xtestkit.engine.mobile.android.param.ConnectionParam;
import org.swiften.xtestkit.engine.mobile.android.param.DeviceSettingParam;
import org.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import org.swiften.xtestkit.engine.mobile.android.param.StopEmulatorParam;
import org.swiften.xtestkit.system.NetworkHandler;
import org.swiften.xtestkit.system.ProcessRunner;
import org.swiften.xtestkit.system.PortProtocol;
import org.swiften.xtestkit.system.ProcessRunnerProtocol;
import io.reactivex.Flowable;
import io.reactivex.exceptions.Exceptions;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.number.NumberUtil;
import org.swiften.javautilities.string.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by haipham on 4/8/17.
 */
public class ADBHandler implements ADBErrorProtocol, ADBDelayProtocol {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Recommended port range is 5585 - 5586. However, adb will increment the
     * port number by one, so we need to decrement by 1.
     */
    public static final int MIN_PORT = 5554;
    public static final int MAX_PORT = 5585;

    @NotNull private static final Collection<Integer> AVAILABLE_PORTS;

    static {
        AVAILABLE_PORTS = IntStream
            .range(MIN_PORT, MAX_PORT + 1)
            .filter(NumberUtil::isEven)
            .boxed()
            .collect(Collectors.toList());;
    }

    /**
     * Get a count of available ports between {@link #MIN_PORT} and
     * {@link #MAX_PORT}. Only even ports are counted.
     * @return An {@link Integer} value.
     */
    public static int availablePortsCount() {
        int total = MAX_PORT - MIN_PORT + 1;

        if (NumberUtil.isEven(MIN_PORT) && NumberUtil.isEven(MAX_PORT)) {
            return (total - 1) / 2 + 1;
        } else if (NumberUtil.isOdd(MIN_PORT) && NumberUtil.isOdd(MAX_PORT)) {
            return (total - 1) / 2;
        } else {
            return total / 2;
        }
    }

    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;

    ADBHandler() {
        PROCESS_RUNNER = ProcessRunner.builder().build();
        NETWORK_HANDLER = NetworkHandler.builder().build();
    }

    //region Getters
    /**
     * Return {@link #PROCESS_RUNNER}.
     * @return A {@link ProcessRunner} instance.
     */
    @NotNull
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    /**
     * Get all available ports.
     * @return A {@link Collection} of {@link Integer}.
     */
    @NotNull
    public Collection<Integer> availablePorts() {
        return AVAILABLE_PORTS;
    }

    /**
     * Return {@link #NETWORK_HANDLER}.
     * @return A {@link NetworkHandler} instance.
     */
    @NotNull
    public NetworkHandler networkHandler() {
        return NETWORK_HANDLER;
    }

    /**
     * Check if a port is acceptable; i.e. lies within {@link #MIN_PORT} and
     * {@link #MAX_PORT} and is an even number.
     * @param port An {@link Integer} value.
     * @return A {@link Boolean} value.
     */
    public boolean isAcceptablePort(int port) {
        return port >= MIN_PORT && port <= MAX_PORT && NumberUtil.isEven(port);
    }
    //endregion

    //region ADB setup
    /**
     * Restart adb server in order to avoid problem with adb not acknowledging
     * the first command at the start of a test batch.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxRestartAdb() {
        final ProcessRunnerProtocol RUNNER = processRunner();
        NetworkHandler handler = networkHandler();

        return handler
            .rxKillProcessWithName("adb")
            .onErrorResumeNext(Flowable.just(true))
            .map(a -> cmLaunchAdb())
            .flatMap(RUNNER::rxExecute)
            .map(a -> true);
    }
    //endregion

    //region Start Emulator
    /**
     * Same as {@link #isAcceptablePort(int)}, but returns a {@link Flowable}
     * instance.
     * @param port An {@link Integer} value.
     * @return A {@link Flowable} instance.
     * @see #isAcceptablePort(int)
     */
    @NotNull
    public Flowable<Boolean> rxIsAcceptablePort(int port) {
        return Flowable.just(isAcceptablePort(port));
    }

    /**
     * Recursively find an available port and emit and error if none is found.
     * @param PARAM A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * * @see #rxIsAcceptablePort(int)
     * @see NetworkHandler#checkPortsMarkedAsUsed(Collection)
     * @see NetworkHandler#rxCheckPortAvailable(PortProtocol)
     */
    @NotNull
    public Flowable<Integer> rxFindAvailablePort(@NotNull final RetryProtocol PARAM) {
        final NetworkHandler NETWORK_HANDLER = networkHandler();
        Collection<Integer> availablePorts = availablePorts();

        if (NETWORK_HANDLER.checkPortsMarkedAsUsed(availablePorts)) {
            return Flowable.error(new Exception(NO_PORT_AVAILABLE));
        }

        class CheckPort {
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Flowable<Integer> check(final int PORT) {
                if (PORT >= MIN_PORT && PORT <= MAX_PORT) {
                    class Param implements PortProtocol, RetryProtocol {
                        @Override
                        public int port() {
                            return PORT;
                        }

                        @Override
                        public int retries() {
                            return PARAM.retries();
                        }
                    }

                    return Flowable
                        .concat(
                            rxIsAcceptablePort(PORT),
                            NETWORK_HANDLER.rxCheckPortAvailable(new Param())
                        )
                        .all(BooleanUtil::isTrue)
                        .filter(BooleanUtil::isTrue)
                        .toFlowable()
                        .map(a -> PORT)
                        .doOnNext(NETWORK_HANDLER::markPortAsUsed)
                        .switchIfEmpty(new CheckPort().check(PORT + 1));
                }

                return Flowable.error(new Exception(NO_PORT_AVAILABLE));
            }
        }

        return new CheckPort().check(MIN_PORT);
    }

    /**
     * Start the emulator with the specified settings. Detect when bootanim is
     * 'closed' and then emit value.
     * @param PARAM A {@link StartEmulatorParam} instance.
     * @return A {@link Flowable} instance.
     * @see #cmStartEmulator(StartEmulatorParam)
     * @see #cmBootAnim(DeviceUIDProtocol)
     */
    @NotNull
    public Flowable<Boolean> rxStartEmulator(@NotNull final StartEmulatorParam PARAM) {
        if (!isAcceptablePort(PARAM.port())) {
            String error = unacceptablePort(PARAM.port());
            return Flowable.error(new Exception(error));
        }

        final ProcessRunner PROCESS_RUNNER = processRunner();
        final int RETRIES = PARAM.retries();

        @SuppressWarnings("WeakerAccess")
        final long DELAY = emulatorBootRetryDelay();

        @SuppressWarnings("WeakerAccess")
        final long TIMEOUT = emulatorBootTimeout();

        /* Append any error when starting up the emulator to this list, and
         * have retryWhen read it to determine whether to continue retrying */
        final List<Exception> ERRORS = new ArrayList<>();

        /* We need to start the emulator on a new Thread, or else it will
         * block the rest of the operations */
        new Thread(() -> {
            try {
                PROCESS_RUNNER.execute(cmStartEmulator(PARAM));
            } catch (Exception e) {
                ERRORS.add(e);
            }
        }).start();

        return PROCESS_RUNNER
            .rxExecute(cmBootAnim(PARAM))
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
    //endregion

    //region Stop Emulator
    /**
     * Shut down all emulators.
     * @param param A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #cmStopAllEmulators()
     */
    @NotNull
    public Flowable<Boolean> rxStopAllEmulators(@NotNull RetryProtocol param) {
        String command = cmStopAllEmulators();

        return processRunner()
            .rxExecute(command)
            .map(a -> true)
            .retry(param.retries());
    }

    /**
     * Kill a specific emulator instance, based on its port number.
     * @param param A {@link StopEmulatorParam} instance.
     * @return A {@link Flowable} instance.
     * @see NetworkHandler#rxKillProcessWithPort(RetryProtocol, Predicate)
     */
    @NotNull
    public Flowable<Boolean> rxStopEmulator(@NotNull StopEmulatorParam param) {
        return networkHandler().rxKillProcessWithPort(param, a -> true);
    }
    //endregion

    //region Clear Cached Data
    /**
     * Clear cached data from a device/emulator.
     * @param param A {@link T} instance.
     * @param <T> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #cmClearCachedData(AppPackageProtocol)
     */
    @NotNull
    public <T extends AppPackageProtocol & DeviceUIDProtocol & RetryProtocol>
    Flowable<Boolean> rxClearCachedData(@NotNull T param) {
        ProcessRunner runner = processRunner();
        final String APP = param.appPackage();
        String command = cmClearCachedData(param);

        return runner
            .rxExecute(command)

            /* Output from the above command may either be 'Success' or
             * 'Failed'. Failures may be due to the app's package name not
             * present in the device/emulator. */
            .filter(a -> a.contains("Success"))
            .switchIfEmpty(Flowable.error(new Exception(unableToClearCache(APP))))
            .retry(param.retries())
            .map(a -> true);
    }
    //endregion

    //region Check App Installation
    /**
     * Check whether an app is installed on the currently active
     * device/emulator.
     * @param PARAM An {@link AppPackageProtocol} instance.
     * @param <T> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #cmListPackages(DeviceUIDProtocol)
     */
    @NotNull
    public <T extends AppPackageProtocol & DeviceUIDProtocol & RetryProtocol>
    Flowable<Boolean> rxCheckAppInstalled(@NotNull final T PARAM) {
        ProcessRunner processRunner = processRunner();
        String listCommand = cmListPackages(PARAM);
        final String PKG = PARAM.appPackage();

        return processRunner
            .rxExecute(listCommand)
            .filter(a -> a.contains(PKG))
            .retry(PARAM.retries())
            .map(a -> true)
            .switchIfEmpty(Flowable.error(new Exception(appNotInstalled(PKG))));
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
     * @param param A {@link DeviceUIDProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #rxToggleInternetConnection(ConnectionParam)
     */
    @NotNull
    public Flowable<Boolean>
    rxEnableInternetConnection(@NotNull DeviceUIDProtocol param) {
        ConnectionParam conn = ConnectionParam
            .builder()
            .shouldEnable(true)
            .withDeviceUIDProtocol(param)
            .build();

        return rxToggleInternetConnection(conn);
    }

    /**
     * Same as above, but uses a default {@link ConnectionParam}.
     * @param param A {@link DeviceUIDProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #rxToggleInternetConnection(ConnectionParam)
     */
    @NotNull
    public Flowable<Boolean>
    rxDisableInternetConnection(@NotNull DeviceUIDProtocol param) {
        ConnectionParam conn = ConnectionParam
            .builder()
            .shouldEnable(false)
            .withDeviceUIDProtocol(param)
            .build();

        return rxToggleInternetConnection(conn);
    }
    //endregion

    //region Check Keyboard
    /**
     * Check whether the keyboard is open.
     * @param param A {@link DeviceUIDProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #cmCheckKeyboardOpen(DeviceUIDProtocol)
     */
    @NotNull
    public Flowable<Boolean> rxCheckKeyboardOpen(@NotNull DeviceUIDProtocol param) {
        String command = cmCheckKeyboardOpen(param);

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
            .onErrorResumeNext(Flowable.empty())

            /* Throw error if the returned value does not match the new
             * setting value */
            .switchIfEmpty(Flowable.error(new Exception(changeSettingsFailed(PARAM.key()))))

            /* Sometimes an adb error may be thrown if the currently active
             * adb instance does not acknowledge the request */
            .retry(PARAM.retries())

            /* If it takes too long to change the device/emulator settings,
             * proceed anyway */
            .timeout(emulatorSettingTimeout(), TimeUnit.MILLISECONDS);
    }
    //endregion

    //region Disable Window Animation Scale
    /**
     * Disable window animation scale.
     * @param param A {@link DeviceUIDProtocol} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean>
    rxDisableWindowAnimationScale(@NotNull DeviceUIDProtocol param) {
        DeviceSettingParam setting = DeviceSettingParam.builder()
            .withGlobalNameSpace()
            .withKey("window_animation_scale")
            .withValue("0")
            .withDeviceUIDProtocol(param)
            .build();

        return rxChangeSettings(setting);
    }
    //endregion

    //region Disable Transition Animation Scale
    /**
     * Disable transition animation scale.
     * @param param A {@link DeviceUIDProtocol} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean>
    rxDisableTransitionAnimationScale(@NotNull DeviceUIDProtocol param) {
        DeviceSettingParam setting = DeviceSettingParam.builder()
            .withGlobalNameSpace()
            .withKey("transition_animation_scale")
            .withValue("0")
            .withDeviceUIDProtocol(param)
            .build();

        return rxChangeSettings(setting);
    }
    //endregion

    //region Disable Animator Duration Scale
    /**
     * Disable animator duration scale.
     * @param param A {@link DeviceUIDProtocol} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean>
    rxDisableAnimatorDurationScale(@NotNull DeviceUIDProtocol param) {
        DeviceSettingParam setting = DeviceSettingParam.builder()
            .withGlobalNameSpace()
            .withKey("animator_duration_scale")
            .withValue("0")
            .withDeviceUIDProtocol(param)
            .build();

        return rxChangeSettings(setting);
    }
    //endregion

    //region Disable Emulator Animations
    /**
     * Disable emulator animations for UI test to prevent unexpected wait
     * times. Note that this is only applicable for rooted devices, and
     * emulators are rooted by default.
     * @param param A {@link DeviceUIDProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #rxDisableWindowAnimationScale(DeviceUIDProtocol)
     * @see #rxDisableTransitionAnimationScale(DeviceUIDProtocol)
     * @see #rxDisableAnimatorDurationScale(DeviceUIDProtocol)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean>
    rxDisableEmulatorAnimations(@NotNull DeviceUIDProtocol param) {
        return Flowable
            .mergeArray(
                rxDisableWindowAnimationScale(param),
                rxDisableTransitionAnimationScale(param),
                rxDisableAnimatorDurationScale(param)
            )
            .all(BooleanUtil::isTrue)
            .toFlowable();
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
        return String.format("%1$s shell", cmAdb());
    }

    /**
     * Get path to adb shell CLI, with device UID.
     * @param param A {@link DeviceUIDProtocol} instance.
     * @return A {@link String} value.
     * @see #cmAdb()
     */
    @NotNull
    public String cmAdbShell(@NotNull DeviceUIDProtocol param) {
        String deviceUID = param.deviceUID();
        return String.format("%1$s -s %2$s shell", cmAdb(), deviceUID);
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
     * Command to start an emulator whose name is specified in the parameters.
     * @param param A {@link String} value.
     * @return A {@link String} value.
     * @see #cmEmulator()
     */
    @NotNull
    public String cmStartEmulator(@NotNull StartEmulatorParam param) {
        String name = param.deviceName();
        int port = param.port();
        return String.format("%1$s -port %2$d -avd %3$s", cmEmulator(), port, name);
    }

    /**
     * Command to check bootanim status from adb shell.
     * We can check whether the emulator is fully started by checking its
     * bootanim. If this value is 'stopped', the emulator has booted up
     * completely.
     * @param param A {@link String} value.
     * @return A {@link String} value.
     * @see #cmAdbShell(DeviceUIDProtocol)
     */
    @NotNull
    public String cmBootAnim(@NotNull DeviceUIDProtocol param) {
        return String.format("%s getprop init.svc.bootanim", cmAdbShell(param));
    }

    /**
     * Command to shut down the emulator. Should not be used for actual
     * devices because this command will send a shutdown signal.
     * @return A {@link String} value.
     * @see #cmAdbShell()
     */
    @NotNull
    public String cmStopAllEmulators() {
        return String.format("%s reboot -p", cmAdbShell());
    }

    /**
     * Command to list all installed packages on an active device/emulator.
     * @param param A {@link String} value.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmListPackages(@NotNull DeviceUIDProtocol param) {
        return String.format("%1$s pm list packages", cmAdbShell(param));
    }

    /**
     * The command to enable/disable internet connection.
     * @param param A {@link ConnectionParam} instance.
     * @return A {@link String} value.
     * @see #cmAdbShell(DeviceUIDProtocol)
     */
    @NotNull
    public String cmToggleConnection(@NotNull ConnectionParam param) {
        String append = param.enable() ? "enable" : "disable";
        return String.format("%1$s svc data %2$s", cmAdbShell(param), append);
    }

    /**
     * Command to check whether keyboard is open.
     * @return A {@link String} value.
     * @see #cmAdbShell(DeviceUIDProtocol)
     */
    @NotNull
    public String cmCheckKeyboardOpen(@NotNull DeviceUIDProtocol param) {
        return String.format("%s dumpsys window InputMethod", cmAdbShell(param));
    }

    /**
     * Command to change device settings.
     * @param param a {@link DeviceSettingParam} instance.
     * @return A {@link String} value.
     * @see #cmAdbShell(DeviceUIDProtocol)
     */
    @NotNull
    public String cmPutSettings(@NotNull DeviceSettingParam param) {
        return String.format(
            "%1$s settings %2$s",
            cmAdbShell(param), param.putCommand());
    }

    /**
     * Command to clear cached data from a device/emulator.
     * @param param A {@link T} instance.
     * @param <T> Generics parameter.
     * @return A {@link String} value.
     */
    @NotNull
    public <T extends AppPackageProtocol & DeviceUIDProtocol>
    String cmClearCachedData(@NotNull T param) {
        String shell = cmAdbShell(param);
        String appPackage = param.appPackage();
        return String.format("%1$s pm clear %2$s", shell, appPackage);
    }

    /**
     * Command to get device settings.
     * @param param a {@link DeviceSettingParam} instance.
     * @return A {@link String} value.
     * @see #cmAdbShell(DeviceUIDProtocol)
     */
    @NotNull
    public String cmGetSettings(@NotNull DeviceSettingParam param) {
        return String.format("%1$s settings %2$s", cmAdbShell(param), param.getCommand());
    }
    //endregion

    /**
     * Builder class for {@link ADBHandler}.
     */
    public static final class Builder {
        @NotNull private final ADBHandler HANDLER;

        Builder() {
            HANDLER = new ADBHandler();
        }

        public ADBHandler build() {
            return HANDLER;
        }
    }
}
