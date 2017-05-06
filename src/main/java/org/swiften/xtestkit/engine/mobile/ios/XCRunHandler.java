package org.swiften.xtestkit.engine.mobile.ios;

import org.swiften.xtestkit.engine.base.RetryProtocol;
import org.swiften.xtestkit.system.ProcessRunner;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 4/8/17.
 */
public class XCRunHandler implements XCRunDelayProtocol {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final ProcessRunner PROCESS_RUNNER;

    XCRunHandler() {
        PROCESS_RUNNER = ProcessRunner.builder().build();
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
    //endregion

    //region CLI commands
    /**
     * Get path to xcrun.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmXCRun() {
        return "xcrun";
    }

    /**
     * Get path to XCode.app. We can use
     * XCode/Contents/Developer/Applications/Simulator to start a simulator.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmXCode() {
        return "/Applications/Xcode.app/Contents";
    }

    /**
     * Get path to Simulator CLI.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmXCodeSimulator() {
        String[] components = new String[] {
            cmXCode(),
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
     * @return A {@link String} value.
     * @see #cmXCRun()
     */
    @NotNull
    public String cmInstruments() {
        return String.format("%s instruments", cmXCRun());
    }

    /**
     * Get path to simctl.
     * @return A {@link String} value.
     * @see #cmXCRun()
     */
    @NotNull
    public String cmSimctl() {
        return String.format("%s simctl", cmXCRun());
    }

    /**
     * Get an environment variable specified by a key name. When this command
     * is executed, a value will only be emitted once the simulator has gone
     * into booted mode.
     * @param key A {@link String} value specifying the variable name.
     * @param deviceUID A {@link String} value specifying the device UID.
     * @return A {@link String} value.
     * @see #cmSimctl()
     */
    @NotNull
    public String cmGetEnv(@NotNull String key, @NotNull String deviceUID) {
        return String.format("%1$s getenv %2$s %3$s", cmSimctl(), deviceUID, key);
    }

    /**
     * Get ${HOME} environment variable.
     * @param deviceUID A {@link String} value specifying the device UID.
     * @return A {@link String} value.
     * @see #cmGetEnv(String, String)
     */
    @NotNull
    public String cmGetHomeEnv(@NotNull String deviceUID) {
        return cmGetEnv("HOME", deviceUID);
    }

    /**
     * Command to get list of available instruments/simulators.
     * @return A {@link String} value.
     * @see #cmSimctl()
     */
    @NotNull
    public String cmInstrumentsList() {
        return String.format("%s list", cmSimctl());
    }

    /**
     * Command to start a simulator.
     * @param deviceUID A {@link String} value specifying the device UID.
     * @return A {@link String} value.
     * @see #cmXCodeSimulator()
     */
    @NotNull
    public String cmStartSimulator(@NotNull String deviceUID) {
        String simulator = cmXCodeSimulator();
        return String.format("%1$s -CurrentDeviceUDID %2$s", simulator, deviceUID);
    }

    /**
     * Command to stop the currently active simulator.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmStopSimulator() {
        return "killall \"Simulator\"";
    }

    /**
     * Command to check whether the simulator has booted up or not.
     * @param deviceUID A {@link String} value specifying the device UID.
     * @return A {@link String} value.
     * @see #cmGetHomeEnv(String)
     */
    @NotNull
    public String cmCheckSimulatorBooted(@NotNull String deviceUID) {
        return cmGetHomeEnv(deviceUID);
    }

    /**
     * Command to install app.
     * @param app A {@link String} value specifying the app path.
     * @return A {@link String} value.
     * @see #cmSimctl()
     */
    @NotNull
    public String cmInstallApp(@NotNull String app) {
        return String.format("%1$s install booted %2$s", cmSimctl(), app);
    }

    /**
     * Command to launch app.
     * @param appPackage The app's package name.
     * @return A {@link String} value.
     * @see #cmSimctl()
     */
    @NotNull
    public String cmLaunchApp(@NotNull String appPackage) {
        return String.format("%1$s launch booted %2$s", cmSimctl(), appPackage);
    }

    /**
     * Command to uninstall an application.
     * @param appPackage The app's package name.
     * @return A {@link String} value.
     * @see #cmSimctl()
     */
    @NotNull
    public String cmUninstallApp(@NotNull String appPackage) {
        return String.format("%1$s uninstall booted %2$s", cmSimctl(), appPackage);
    }
    //endregion

    //region Start Simulator

    /**
     * Check if the simulator has been booted yet.
     * @param deviceUID A {@link String} value specifying the device UID.
     * @return A {@link String} value.
     * @see #cmCheckSimulatorBooted(String)
     */
    @NotNull
    public Flowable<Boolean> rxCheckSimulatorBooted(@NotNull String deviceUID) {
        ProcessRunner processRunner = processRunner();
        String command = cmCheckSimulatorBooted(deviceUID);
        return processRunner.rxExecute(command).map(a -> true);
    }

    /**
     * Start a simulator.
     * @param PARAM A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #cmStartSimulator(String)
     * @see #rxCheckSimulatorBooted(String)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxStartSimulator(@NotNull final StartSimulatorParam PARAM) {
        final ProcessRunner RUNNER = processRunner();
        final String COMMAND = cmStartSimulator(PARAM.deviceUID());
        final List<Exception> ERRORS = new ArrayList<>();
        final long DELAY = simulatorBootRetryDelay();

        /* We need to start the simulator on a new Thread, or else it will
         * block the rest of the operations */
        new Thread(() -> {
            try {
                RUNNER.execute(COMMAND);
            } catch (Exception e) {
                ERRORS.add(e);
            }
        }).start();

        return Flowable.ambArray(
            Flowable
                .fromIterable(ERRORS)
                .firstElement()
                .switchIfEmpty(Maybe.error(new RuntimeException()))
                .retryWhen(e -> e.delay(DELAY, TimeUnit.MILLISECONDS))
                .flatMap(Maybe::error)
                .toFlowable()
                .map(a -> true),

            rxCheckSimulatorBooted(PARAM.deviceUID())
                .retryWhen(e -> e.delay(DELAY, TimeUnit.MILLISECONDS))
        );
    }
    //endregion

    //region Stop Simulator
    /**
     * Stop the currently active simulator.
     * @param param A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #cmStopSimulator()
     */
    @NotNull
    public Flowable<Boolean> rxStopSimulator(@NotNull RetryProtocol param) {
        ProcessRunner processRunner = processRunner();
        String command = cmStopSimulator();

        return processRunner
            .rxExecute(command)
            .map(a -> true)
            .onErrorReturnItem(true);
    }
    //endregion

    //region Device Methods
    /**
     * Install an app.
     * @param app A {@link String} value specifying the app path.
     * @return A {@link Flowable} instance.
     * @see #cmInstallApp(String)
     */
    @NotNull
    public Flowable<Boolean> rxInstallApp(@NotNull String app) {
        ProcessRunner processRunner = processRunner();
        String command = cmInstallApp(app);
        return processRunner.rxExecute(command).map(a -> true);
    }

    /**
     * Uninstall an app.
     * @param appPackage The app's package name.
     * @return A {@link Flowable} instance.
     * @see #cmUninstallApp(String)
     */
    @NotNull
    public Flowable<Boolean> rxUninstallApp(@NotNull String appPackage) {
        ProcessRunner processRunner = processRunner();
        String command = cmUninstallApp(appPackage);
        return processRunner.rxExecute(command).map(a -> true);
    }
    //endregion

    public static final class Builder {
        @NotNull private final XCRunHandler HANDLER;

        Builder() {
            HANDLER = new XCRunHandler();
        }

        @NotNull
        public XCRunHandler build() {
            return HANDLER;
        }
    }
}
