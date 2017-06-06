package org.swiften.xtestkit.ios;

import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.swiften.xtestkit.ios.param.StartSimulatorParam;
import org.swiften.xtestkit.ios.type.XCRunDelayType;
import org.swiften.xtestkitcomponents.system.process.ProcessRunner;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by haipham on 4/8/17.
 */
public class XCRunHandler implements XCRunDelayType {
    @NotNull private final ProcessRunner PROCESS_RUNNER;

    XCRunHandler() {
        PROCESS_RUNNER = new ProcessRunner();
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
    //endregion

    //region CLI commands
    /**
     * Get path to xcrun.
     * @return {@link String} value.
     */
    @NotNull
    public String cm_XCRun() {
        return "xcrun";
    }

    /**
     * Get path to XCode.app. We can use
     * XCode/Contents/Developer/Applications/Simulator to start a simulator.
     * @return {@link String} value.
     */
    @NotNull
    public String cm_XCode() {
        return "/Applications/Xcode.app/Contents";
    }

    /**
     * Get path to Simulator CLI.
     * @return {@link String} value.
     */
    @NotNull
    public String cm_XCodeSimulator() {
        String[] components = new String[] {
            cm_XCode(),
            "Developer",
            "Applications",
            "Simulator.app",
            "Contents",
            "MacOS",
            "Simulator"
        };

        return String.join("/", components);
    }

    /**
     * Get path to xcrun instruments.
     * @return {@link String} value.
     * @see #cm_XCRun()
     */
    @NotNull
    public String cmInstruments() {
        return String.format("%s instruments", cm_XCRun());
    }

    /**
     * Get path to simctl.
     * @return {@link String} value.
     * @see #cm_XCRun()
     */
    @NotNull
    public String cmSimctl() {
        return String.format("%s simctl", cm_XCRun());
    }

    /**
     * Get an environment variable specified by a key name. When this command
     * is executed, a value will only be emitted once the simulator has gone
     * into booted mode.
     * @param key {@link String} value specifying the variable name.
     * @param deviceUID {@link String} value specifying the device UID.
     * @return {@link String} value.
     * @see #cmSimctl()
     */
    @NotNull
    public String cmGetEnv(@NotNull String key, @NotNull String deviceUID) {
        return String.format("%1$s getenv %2$s %3$s", cmSimctl(), deviceUID, key);
    }

    /**
     * Get ${HOME} environment variable.
     * @param deviceUID {@link String} value specifying the device UID.
     * @return {@link String} value.
     * @see #cmGetEnv(String, String)
     */
    @NotNull
    public String cm_getHomeEnv(@NotNull String deviceUID) {
        return cmGetEnv("HOME", deviceUID);
    }

    /**
     * Command to get list of available instruments/simulators.
     * @return {@link String} value.
     * @see #cmSimctl()
     */
    @NotNull
    public String cmInstrumentsList() {
        return String.format("%s list", cmSimctl());
    }

    /**
     * Command to start a simulator.
     * @param deviceUID {@link String} value specifying the device UID.
     * @return {@link String} value.
     * @see #cm_XCodeSimulator()
     */
    @NotNull
    public String cm_startSimulator(@NotNull String deviceUID) {
        String simulator = cm_XCodeSimulator();
        return String.format("%1$s -CurrentDeviceUDID %2$s", simulator, deviceUID);
    }

    /**
     * Command to stop the currently active simulator.
     * @return {@link String} value.
     */
    @NotNull
    public String cm_stopSimulator() {
        return "killall \"Simulator\"";
    }

    /**
     * Command to check whether the simulator has booted up or not.
     * @param deviceUID {@link String} value specifying the device UID.
     * @return {@link String} value.
     * @see #cm_getHomeEnv(String)
     */
    @NotNull
    public String cm_checkSimulatorBooted(@NotNull String deviceUID) {
        return cm_getHomeEnv(deviceUID);
    }

    /**
     * Command to install app.
     * @param app {@link String} value specifying the app path.
     * @return {@link String} value.
     * @see #cmSimctl()
     */
    @NotNull
    public String cm_installApp(@NotNull String app) {
        return String.format("%1$s install booted %2$s", cmSimctl(), app);
    }

    /**
     * Command to launch app.
     * @param appPackage The app's package name.
     * @return {@link String} value.
     * @see #cmSimctl()
     */
    @NotNull
    public String cm_launchApp(@NotNull String appPackage) {
        return String.format("%1$s launch booted %2$s", cmSimctl(), appPackage);
    }

    /**
     * Command to uninstall an application.
     * @param appPackage The app's package name.
     * @return {@link String} value.
     * @see #cmSimctl()
     */
    @NotNull
    public String cm_uninstallApp(@NotNull String appPackage) {
        return String.format("%1$s uninstall booted %2$s", cmSimctl(), appPackage);
    }
    //endregion

    //region Start Simulator

    /**
     * Check if the simulator has been booted yet.
     * @param deviceUID {@link String} value specifying the device UID.
     * @return {@link String} value.
     * @see #cm_checkSimulatorBooted(String)
     */
    @NotNull
    public Flowable<Boolean> rxa_checkSimulatorBooted(@NotNull String deviceUID) {
        ProcessRunner processRunner = processRunner();
        String command = cm_checkSimulatorBooted(deviceUID);
        return processRunner.rxa_execute(command).map(a -> true);
    }

    /**
     * Start a simulator.
     * @param PARAM {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see ProcessRunner#execute(String, Consumer)
     * @see #processRunner()
     * @see #simulatorBootRetryDelay()
     * @see #cm_startSimulator(String)
     * @see #rxa_checkSimulatorBooted(String)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxa_startSimulator(@NotNull final StartSimulatorParam PARAM) {
        final ProcessRunner RUNNER = processRunner();
        final String COMMAND = cm_startSimulator(PARAM.deviceUID());
        final long DELAY = simulatorBootRetryDelay();

        /* We need to start the simulator on a new Thread, or else it will
         * block the rest of the operations */
        new Thread(() -> RUNNER.execute(COMMAND, a -> {})).start();

        return rxa_checkSimulatorBooted(PARAM.deviceUID())
            .retryWhen(e -> e.delay(DELAY, TimeUnit.MILLISECONDS));
    }
    //endregion

    //region Stop Simulator
    /**
     * Stop the currently active simulator.
     * @param param {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#toTrue(Object)
     * @see #cm_stopSimulator()
     * @see #processRunner()
     */
    @NotNull
    public Flowable<Boolean> rxa_stopSimulator(@NotNull RetryType param) {
        ProcessRunner processRunner = processRunner();
        String command = cm_stopSimulator();

        return processRunner
            .rxa_execute(command)
            .map(BooleanUtil::toTrue)
            .onErrorReturnItem(true);
    }
    //endregion

    //region Device Methods
    /**
     * Install an app.
     * @param app {@link String} value specifying the app path.
     * @return {@link Flowable} instance.
     * @see #cm_installApp(String)
     */
    @NotNull
    public Flowable<Boolean> rxInstallApp(@NotNull String app) {
        ProcessRunner processRunner = processRunner();
        String command = cm_installApp(app);
        return processRunner.rxa_execute(command).map(a -> true);
    }

    /**
     * Uninstall an app.
     * @param appPackage The app's package name.
     * @return {@link Flowable} instance.
     * @see #cm_uninstallApp(String)
     */
    @NotNull
    public Flowable<Boolean> rxUninstallApp(@NotNull String appPackage) {
        ProcessRunner processRunner = processRunner();
        String command = cm_uninstallApp(appPackage);
        return processRunner.rxa_execute(command).map(a -> true);
    }
    //endregion
}
