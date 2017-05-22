package org.swiften.xtestkit.mobile.android.adb;

import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.type.AppPackageType;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.mobile.android.param.ConnectionParam;
import org.swiften.xtestkit.mobile.android.param.DeviceSettingParam;
import org.swiften.xtestkit.mobile.android.param.StartEmulatorParam;
import org.swiften.xtestkit.mobile.android.param.StopEmulatorParam;
import org.swiften.xtestkit.mobile.android.type.DeviceUIDType;
import org.swiften.xtestkit.system.network.NetworkHandler;
import org.swiften.xtestkit.system.network.param.PortCheckParam;
import org.swiften.xtestkit.system.network.type.PortType;
import org.swiften.xtestkit.system.process.ProcessRunner;
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
public class ADBHandler implements ADBErrorType, ADBDelayType {
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
     * @return {@link Integer} value.
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
     * @return {@link ProcessRunner} instance.
     */
    @NotNull
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    /**
     * Get all available ports.
     * @return {@link Collection} of {@link Integer}.
     */
    @NotNull
    public Collection<Integer> availablePorts() {
        return AVAILABLE_PORTS;
    }

    /**
     * Return {@link #NETWORK_HANDLER}.
     * @return {@link NetworkHandler} instance.
     */
    @NotNull
    public NetworkHandler networkHandler() {
        return NETWORK_HANDLER;
    }

    /**
     * Check if a port is acceptable; i.e. lies within {@link #MIN_PORT} and
     * {@link #MAX_PORT} and is an even number.
     * @param port {@link Integer} value.
     * @return {@link Boolean} value.
     */
    public boolean isAcceptablePort(int port) {
        return port >= MIN_PORT && port <= MAX_PORT && NumberUtil.isEven(port);
    }
    //endregion

    //region ADB setup
    /**
     * Restart adb server in order to avoid problem with adb not acknowledging
     * the first command at the start of a test batch.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rx_restartAdb() {
        final ProcessRunner RUNNER = processRunner();
        NetworkHandler handler = networkHandler();

        return handler
            .rxKillWithName("adb")
            .onErrorResumeNext(Flowable.just(true))
            .map(a -> cm_launchAdb())
            .flatMap(RUNNER::rxExecute)
            .map(BooleanUtil::toTrue);
    }
    //endregion

    //region Start Emulator
    /**
     * Recursively find an available port and emit and error if none is found.
     * @param PARAM {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * * @see #rxIsAcceptablePort(int)
     * @see NetworkHandler#checkPortsMarkedAsUsed(Collection)
     * @see NetworkHandler#rxCheckPortAvailable(PortType)
     */
    @NotNull
    public Flowable<Integer> rx_availablePort(@NotNull final RetryType PARAM) {
        NetworkHandler networkHandler = networkHandler();
        Collection<Integer> availablePorts = availablePorts();

        if (networkHandler.checkPortsMarkedAsUsed(availablePorts)) {
            return RxUtil.error(NO_PORT_AVAILABLE);
        } else {
            PortCheckParam param = PortCheckParam.builder()
                .withPort(MIN_PORT)
                .withMaxPort(MAX_PORT)
                .withPortStep(2)
                .withRetryType(PARAM)
                .build();

            return networkHandler.rxCheckUntilPortAvailable(param);
        }
    }

    /**
     * Start the emulator with the specified settings. Detect when bootanim is
     * 'closed' and then emit value.
     * @param PARAM {@link StartEmulatorParam} instance.
     * @return {@link Flowable} instance.
     * @see #cm_startEmulator(StartEmulatorParam)
     * @see #cm_bootAnim(DeviceUIDType)
     */
    @NotNull
    public Flowable<Boolean> rx_startEmulator(@NotNull final StartEmulatorParam PARAM) {
        if (!isAcceptablePort(PARAM.port())) {
            String error = unacceptablePort(PARAM.port());
            return RxUtil.error(error);
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
                PROCESS_RUNNER.execute(cm_startEmulator(PARAM));
            } catch (Exception e) {
                ERRORS.add(e);
            }
        }).start();

        return PROCESS_RUNNER
            .rxExecute(cm_bootAnim(PARAM))
            .filter(Objects::nonNull)
            .map(String::trim)

            /* There are two errors that can be thrown here:
             * When the emulator is first started: no devices/emulators found
             * When the emulator is booting up: device unauthorized
             * Afterwards, when the emulator has booted up fully, a value
             * 'stopped' will be emitted */
            .filter(a -> a.equals("stopped"))
            .switchIfEmpty(RxUtil.error())
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
            .map(BooleanUtil::toTrue)

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
     * @param param {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see #cm_stopAllEmulators()
     */
    @NotNull
    public Flowable<Boolean> rx_stopAllEmulators(@NotNull RetryType param) {
        String command = cm_stopAllEmulators();

        return processRunner()
            .rxExecute(command)
            .map(BooleanUtil::toTrue)
            .retry(param.retries());
    }

    /**
     * Kill a specific emulator instance, based on its port number.
     * @param param {@link StopEmulatorParam} instance.
     * @return {@link Flowable} instance.
     * @see NetworkHandler#rxKillWithPort(RetryType, Predicate)
     */
    @NotNull
    public Flowable<Boolean> rx_stopEmulator(@NotNull StopEmulatorParam param) {
        return networkHandler().rxKillWithPort(param, a -> true);
    }
    //endregion

    //region Clear Cached Data
    /**
     * Clear cached data from a device/emulator.
     * @param param {@link T} instance.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see T#appPackage()
     * @see #cm_clearCache(AppPackageType)
     * @see #unableToClearCache(String)
     */
    @NotNull
    public <T extends AppPackageType & DeviceUIDType & RetryType>
    Flowable<Boolean> rx_clearCache(@NotNull T param) {
        ProcessRunner runner = processRunner();
        final String APP = param.appPackage();
        String command = cm_clearCache(param);

        return runner
            .rxExecute(command)

            /* Output from the above command may either be 'Success' or
             * 'Failed'. Failures may be due to the app's package name not
             * present in the device/emulator. */
            .filter(a -> a.contains("Success"))
            .switchIfEmpty(RxUtil.error(unableToClearCache(APP)))
            .retry(param.retries())
            .map(BooleanUtil::toTrue);
    }
    //endregion

    //region Check App Installation
    /**
     * Check whether an app is installed on the currently active
     * device/emulator.
     * @param PARAM {@link AppPackageType} instance.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #cm_listPackages(DeviceUIDType)
     */
    @NotNull
    public <T extends AppPackageType & DeviceUIDType & RetryType>
    Flowable<Boolean> rx_checkAppInstalled(@NotNull final T PARAM) {
        ProcessRunner processRunner = processRunner();
        String listCommand = cm_listPackages(PARAM);
        final String PKG = PARAM.appPackage();

        return processRunner
            .rxExecute(listCommand)
            .filter(a -> a.contains(PKG))
            .retry(PARAM.retries())
            .map(BooleanUtil::toTrue)
            .switchIfEmpty(RxUtil.error(appNotInstalled(PKG)));
    }
    //endregion

    //region Toggle Internet Connection
    /**
     * Disable internet connection for a rooted device. We should only use
     * this method for emulators since they are rooted by default.
     * @param param {@link ConnectionParam} instance.
     * @return {@link Flowable} instance.
     * @see #cm_toggleInternet(ConnectionParam)
     */
    @NotNull
    public Flowable<Boolean> rx_toggleInternet(@NotNull ConnectionParam param) {
        String command = cm_toggleInternet(param);

        return processRunner().rxExecute(command)
            /* If successful, there should be no output */
            .filter(String::isEmpty)
            .map(BooleanUtil::toTrue)
            .switchIfEmpty(RxUtil.error(NO_OUTPUT_EXPECTED));
    }

    /**
     * Same as above, but uses a default {@link ConnectionParam}.
     * @param param {@link DeviceUIDType} instance.
     * @return {@link Flowable} instance.
     * @see #rx_toggleInternet(ConnectionParam)
     */
    @NotNull
    public Flowable<Boolean> rx_enableInternet(@NotNull DeviceUIDType param) {
        ConnectionParam conn = ConnectionParam
            .builder()
            .shouldEnable(true)
            .withDeviceUIDProtocol(param)
            .build();

        return rx_toggleInternet(conn);
    }

    /**
     * Same as above, but uses a default {@link ConnectionParam}.
     * @param param {@link DeviceUIDType} instance.
     * @return {@link Flowable} instance.
     * @see #rx_toggleInternet(ConnectionParam)
     */
    @NotNull
    public Flowable<Boolean> rx_disableInternet(@NotNull DeviceUIDType param) {
        ConnectionParam conn = ConnectionParam
            .builder()
            .shouldEnable(false)
            .withDeviceUIDProtocol(param)
            .build();

        return rx_toggleInternet(conn);
    }
    //endregion

    //region Check Keyboard
    /**
     * Check whether the keyboard is open.
     * @param param {@link DeviceUIDType} instance.
     * @return {@link Flowable} instance.
     * @see #cm_checkKeyboardOpen(DeviceUIDType)
     */
    @NotNull
    public Flowable<Boolean> rx_checkKeyboardOpen(@NotNull DeviceUIDType param) {
        String command = cm_checkKeyboardOpen(param);

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
     * {@link #cm_putSettings(DeviceSettingParam)}, and then check that the
     * value is set with {@link #cm_getSettings(DeviceSettingParam)}.
     * @param PARAM {@link DeviceSettingParam} instance.
     * @return {@link Flowable} instance.
     * @see #cm_putSettings(DeviceSettingParam)
     * @see #cm_getSettings(DeviceSettingParam)
     */
    @NotNull
    public Flowable<Boolean> rx_changeSettings(@NotNull final DeviceSettingParam PARAM) {
        final ProcessRunner RUNNER = processRunner();

        return RUNNER.rxExecute(cm_putSettings(PARAM))
            .flatMap(a -> RUNNER.rxExecute(cm_getSettings(PARAM)))
            .filter(a -> a.contains(PARAM.value()))
            .map(BooleanUtil::toTrue)
            .onErrorResumeNext(Flowable.empty())

            /* Throw error if the returned value does not match the new
             * setting value */
            .switchIfEmpty(RxUtil.error(changeSettingsFailed(PARAM.key())))

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
     * @param param {@link DeviceUIDType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rx_disableWindowAnimationScale(@NotNull DeviceUIDType param) {
        DeviceSettingParam setting = DeviceSettingParam.builder()
            .withGlobalNameSpace()
            .withKey("window_animation_scale")
            .withValue("0")
            .withDeviceUIDProtocol(param)
            .build();

        return rx_changeSettings(setting);
    }
    //endregion

    //region Disable Transition Animation Scale
    /**
     * Disable transition animation scale.
     * @param param {@link DeviceUIDType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rx_disableTransitionAnimationScale(@NotNull DeviceUIDType param) {
        DeviceSettingParam setting = DeviceSettingParam.builder()
            .withGlobalNameSpace()
            .withKey("transition_animation_scale")
            .withValue("0")
            .withDeviceUIDProtocol(param)
            .build();

        return rx_changeSettings(setting);
    }
    //endregion

    //region Disable Animator Duration Scale
    /**
     * Disable animator duration scale.
     * @param param {@link DeviceUIDType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rx_disableAnimatorDurationScale(@NotNull DeviceUIDType param) {
        DeviceSettingParam setting = DeviceSettingParam.builder()
            .withGlobalNameSpace()
            .withKey("animator_duration_scale")
            .withValue("0")
            .withDeviceUIDProtocol(param)
            .build();

        return rx_changeSettings(setting);
    }
    //endregion

    //region Disable Emulator Animations
    /**
     * Disable emulator animations for UI test to prevent unexpected wait
     * times. Note that this is only applicable for rooted devices, and
     * emulators are rooted by default.
     * @param param {@link DeviceUIDType} instance.
     * @return {@link Flowable} instance.
     * @see #rx_disableWindowAnimationScale(DeviceUIDType)
     * @see #rx_disableTransitionAnimationScale(DeviceUIDType)
     * @see #rx_disableAnimatorDurationScale(DeviceUIDType)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rx_disableEmulatorAnimations(@NotNull DeviceUIDType param) {
        return Flowable
            .mergeArray(
                rx_disableWindowAnimationScale(param),
                rx_disableTransitionAnimationScale(param),
                rx_disableAnimatorDurationScale(param)
            )
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }
    //endregion

    //region CLI commands
    /**
     * Get ${ANDROID_HOME} from Environment variables.
     * @return {@link String} value.
     */
    @NotNull
    public String cm_AndroidHome() {
        String androidHome = System.getenv("ANDROID_HOME");

        if (StringUtil.isNotNullOrEmpty(androidHome)) {
            return androidHome;
        }

        throw new RuntimeException(ANDROID_HOME_NOT_SET);
    }

    /**
     * Get path to adb.
     * @return {@link String} value.
     * @see #cm_AndroidHome()
     */
    @NotNull
    public String cm_adb() {
        return String.format("%s/platform-tools/adb", cm_AndroidHome());
    }

    /**
     * Command to launch adb.
     * @return {@link String} value.
     * @see #cm_adb()
     */
    @NotNull
    public String cm_launchAdb() {
        return String.format("%s start-server", cm_adb());
    }

    /**
     * Get path to adb shell CLI.
     * @return {@link String} value.
     * @see #cm_adb()
     */
    @NotNull
    public String cm_adbShell() {
        return String.format("%1$s shell", cm_adb());
    }

    /**
     * Get path to adb shell CLI, with device UID.
     * @param param {@link DeviceUIDType} instance.
     * @return {@link String} value.
     * @see #cm_adb()
     */
    @NotNull
    public String cm_adbShell(@NotNull DeviceUIDType param) {
        String deviceUID = param.deviceUID();
        return String.format("%1$s -s %2$s shell", cm_adb(), deviceUID);
    }

    /**
     * Get path to Android emulator CLI.
     * @return {@link String} value.
     * @see #cm_AndroidHome()
     */
    @NotNull
    public String cm_emulator() {
        return String.format("%s/tools/emulator", cm_AndroidHome());
    }

    /**
     * Command to get a list of attached devices.
     * @return {@link String} value.
     * @see #cm_adb()
     */
    @NotNull
    public String cm_adbDevices() {
        return String.format("%s devices -l", cm_adb());
    }

    /**
     * Command to start an emulator whose name is specified in the parameters.
     * @param param {@link String} value.
     * @return {@link String} value.
     * @see #cm_emulator()
     */
    @NotNull
    public String cm_startEmulator(@NotNull StartEmulatorParam param) {
        String name = param.deviceName();
        int port = param.port();
        return String.format("%1$s -port %2$d -avd %3$s", cm_emulator(), port, name);
    }

    /**
     * Command to check bootanim status from adb shell.
     * We can check whether the emulator is fully started by checking its
     * bootanim. If this value is 'stopped', the emulator has booted up
     * completely.
     * @param param {@link String} value.
     * @return {@link String} value.
     * @see #cm_adbShell(DeviceUIDType)
     */
    @NotNull
    public String cm_bootAnim(@NotNull DeviceUIDType param) {
        return String.format("%s getprop init.svc.bootanim", cm_adbShell(param));
    }

    /**
     * Command to shut down the emulator. Should not be used for actual
     * devices because this command will send a shutdown signal.
     * @return {@link String} value.
     * @see #cm_adbShell()
     */
    @NotNull
    public String cm_stopAllEmulators() {
        return String.format("%s reboot -p", cm_adbShell());
    }

    /**
     * Command to list all installed packages on an active device/emulator.
     * @param param {@link String} value.
     * @return {@link String} value.
     */
    @NotNull
    public String cm_listPackages(@NotNull DeviceUIDType param) {
        return String.format("%1$s pm list packages", cm_adbShell(param));
    }

    /**
     * The command to enable/disable internet connection.
     * @param param {@link ConnectionParam} instance.
     * @return {@link String} value.
     * @see #cm_adbShell(DeviceUIDType)
     */
    @NotNull
    public String cm_toggleInternet(@NotNull ConnectionParam param) {
        String append = param.enable() ? "enable" : "disable";
        return String.format("%1$s svc data %2$s", cm_adbShell(param), append);
    }

    /**
     * Command to check whether keyboard is open.
     * @return {@link String} value.
     * @see #cm_adbShell(DeviceUIDType)
     */
    @NotNull
    public String cm_checkKeyboardOpen(@NotNull DeviceUIDType param) {
        return String.format("%s dumpsys window InputMethod", cm_adbShell(param));
    }

    /**
     * Command to change device settings.
     * @param param {@link DeviceSettingParam} instance.
     * @return {@link String} value.
     * @see DeviceSettingParam#cm_put()
     * @see #cm_adbShell(DeviceUIDType)
     */
    @NotNull
    public String cm_putSettings(@NotNull DeviceSettingParam param) {
        return String.format("%1$s settings %2$s", cm_adbShell(param), param.cm_put());
    }

    /**
     * Command to clear cached data from a device/emulator.
     * @param param {@link T} instance.
     * @param <T> Generics parameter.
     * @return {@link String} value.
     * @see #cm_adbShell(DeviceUIDType)
     * @see T#appPackage()
     */
    @NotNull
    public <T extends AppPackageType & DeviceUIDType> String cm_clearCache(@NotNull T param) {
        String shell = cm_adbShell(param);
        String appPackage = param.appPackage();
        return String.format("%1$s pm clear %2$s", shell, appPackage);
    }

    /**
     * Command to get device settings.
     * @param param {@link DeviceSettingParam} instance.
     * @return {@link String} value.
     * @see DeviceSettingParam#cm_get()
     * @see #cm_adbShell(DeviceUIDType)
     */
    @NotNull
    public String cm_getSettings(@NotNull DeviceSettingParam param) {
        return String.format("%1$s settings %2$s", cm_adbShell(param), param.cm_get());
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
