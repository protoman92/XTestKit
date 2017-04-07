package com.swiften.xtestkit.engine.mobile.ios;

import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.AfterClassParam;
import com.swiften.xtestkit.engine.base.param.AfterParam;
import com.swiften.xtestkit.engine.base.param.BeforeClassParam;
import com.swiften.xtestkit.engine.base.param.BeforeParam;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.Automation;
import com.swiften.xtestkit.engine.mobile.MobileEngine;
import com.swiften.xtestkit.engine.base.Platform;
import com.swiften.xtestkit.engine.mobile.ios.protocol.IOSDelayProtocol;
import com.swiften.xtestkit.engine.mobile.ios.protocol.IOSErrorProtocol;
import com.swiften.xtestkit.system.ProcessRunner;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 3/31/17.
 */
public class IOSEngine extends MobileEngine<
    IOSElement,
    IOSDriver<IOSElement>>
    implements
    IOSDelayProtocol,
    IOSErrorProtocol {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String deviceUID;

    private long launchTimeout;

    IOSEngine() {
        deviceUID = "";
        launchTimeout = simulatorLaunchTimeout();
    }

    //region Getters
    /**
     * Return {@link #deviceUID}.
     * @return A {@link String} value.
     */
    @NotNull
    public String deviceUID() {
        return deviceUID;
    }

    /**
     * Return {@link #launchTimeout}.
     * @return A {@link Long} value.
     */
    public long launchTimeout() {
        return launchTimeout;
    }

    /**
     * Check whether the app file extension matches {@link #testMode()}.
     * @return A {@link Boolean} value.
     * @see #testMode()
     */
    public boolean hasCorrectFileExtension() {
        String extension = FilenameUtils.getExtension(app());

        switch (testMode()) {
            case EMULATOR:
                return extension.equalsIgnoreCase("app");

            case DEVICE:
                return extension.equalsIgnoreCase("ipa");

            default:
                return false;
        }
    }

    /**
     * Same as above, but returns a {@link Flowable} which may emit an
     * {@link Exception} for easier chaining.
     * @return A {@link Flowable} instance.
     * @see #hasCorrectFileExtension()
     */
    public Flowable<Boolean> rxHasCorrectFileExtension() {
        boolean correct = hasCorrectFileExtension();

        if (correct) {
            return Flowable.just(true);
        }

        return Flowable.error(new Exception(INVALID_APP_EXTENSION));
    }
    //endregion

    //region Test Setup
    /**
     * @param param A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeClass(BeforeClassParam)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
        return Flowable.just(true);
    }

    /**
     * @param PARAM A {@link AfterClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterClass(AfterClassParam)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxAfterClass(@NotNull final AfterClassParam PARAM) {
        switch (testMode()) {
            case EMULATOR:
                return rxStopSimulator(PARAM);

            default:
                Exception error = new Exception(PLATFORM_UNAVAILABLE);
                return Flowable.error(error);
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
    @NotNull
    @Override
    public List<String> requiredCapabilities() {
        List<String> required = super.requiredCapabilities();
        Collections.addAll(required, deviceUID());
        return required;
    }

    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        capabilities.put(IOSMobileCapabilityType.BUNDLE_ID, appPackage());
        capabilities.put(IOSMobileCapabilityType.LAUNCH_TIMEOUT, launchTimeout());
//        capabilities.put("autoLaunch", false);

        /* Prevent Appium from resetting/shutting down opened simulators */
//        capabilities.put(MobileCapabilityType.NO_RESET, true);
//        capabilities.put(MobileCapabilityType.FULL_RESET, true);

        /* We need to add different capabilities depending on whether the
         * tests are running on simulator or real device */
        switch (testMode()) {
            case DEVICE:
                capabilities.put(MobileCapabilityType.UDID, deviceUID());
                break;

            default:
                break;
        }

        return capabilities;
    }

    /**
     * Override
     * {@link com.swiften.xtestkit.engine.base.PlatformEngine#rxHasAllRequiredInformation()}
     * to add additional validations.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxHasAllRequiredInformation()
     * @see #rxHasCorrectFileExtension()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxHasAllRequiredInformation() {
        return Flowable
            .concat(
                super.rxHasAllRequiredInformation(),
                rxHasCorrectFileExtension()
            )
            .all(a -> a)
            .toFlowable();
    }

    @NotNull
    @Override
    protected IOSDriver<IOSElement> createDriverInstance() {
        try {
            URL url = new URL(serverUri());
            DesiredCapabilities capabilities = desiredCapabilities();
            return new IOSDriver<>(url, capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region CLI commands
    /**
     * Get path to xcrun.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmXcrun() {
        return "xcrun";
    }

    /**
     * Get path to XCode.app. We can use
     * XCode/Contents/Developer/Applications/Simulator to start a simulator,
     * whose name is specified by {@link #deviceName()}.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmXCode() {
        return "/Applications/Xcode.app/Contents";
    }

    /**
     * Get path to Simulator CLI. We can use this to start a simulator whose
     * name is specified by {@link #deviceName()}.
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
     * @see #cmXcrun()
     */
    @NotNull
    public String cmInstruments() {
        return String.format("%s instruments", cmXcrun());
    }

    /**
     * Get path to simctl.
     * @return A {@link String} value.
     * @see #cmXcrun()
     */
    @NotNull
    public String cmSimctl() {
        return String.format("%s simctl", cmXcrun());
    }

    /**
     * Get an environment variable specified by a key name. When this command
     * is executed, a value will only be emitted once the simulator has gone
     * into booted mode.
     * @param key A {@link String} value specifying the variable name.
     * @return A {@link String} value.
     * @see #cmSimctl()
     * @see #deviceUID()
     */
    @NotNull
    public String cmGetEnv(@NotNull String key) {
        return String.format("%1$s getenv %2$s %3$s", cmSimctl(), deviceUID(), key);
    }

    /**
     * Get ${HOME} environment variable.
     * @return A {@link String} value.
     * @see #cmGetEnv(String)
     */
    @NotNull
    public String cmGetHomeEnv() {
        return cmGetEnv("HOME");
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
     * Command to start a simulator whose uid is specified by
     * {@link #deviceUID()}
     * @return A {@link String} value.
     * @see #cmXCodeSimulator()
     * @see #deviceUID()
     */
    @NotNull
    public String cmStartSimulator() {
        String simulator = cmXCodeSimulator();
        String uid = deviceUID();
        return String.format("%1$s -CurrentDeviceUDID %2$s", simulator, uid);
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
     * @return A {@link String} value.
     * @see #cmGetHomeEnv()
     */
    @NotNull
    public String cmCheckSimulatorBooted() {
        return cmGetHomeEnv();
    }

    /**
     * Command to install app.
     * @return A {@link String} value.
     * @see #cmSimctl()
     * @see #app()
     */
    @NotNull
    public String cmInstallApp() {
        return String.format("%1$s install booted %2$s", cmSimctl(), app());
    }

    /**
     * Command to launch app.
     * @return A {@link String} value.
     * @see #cmSimctl()
     * @see #appPackage()
     */
    @NotNull
    public String cmLaunchApp() {
        return String.format("%1$s launch booted %2$s", cmSimctl(), appPackage());
    }

    /**
     * Command to uninstall an application.
     * @return A {@link String} value.
     * @see #cmSimctl()
     * @see #appPackage()
     */
    @NotNull
    public String cmUninstallApp() {
        return String.format("%1$s uninstall booted %2$s", cmSimctl(), appPackage());
    }
    //endregion

    //region Start Simulator

    /**
     * Check if the simulator has been booted yet.
     * @return A {@link String} value.
     * @see #cmCheckSimulatorBooted()
     */
    @NotNull
    public Flowable<Boolean> rxCheckSimulatorBooted() {
        ProcessRunner processRunner = processRunner();
        String command = cmCheckSimulatorBooted();
        return processRunner.rxExecute(command).map(a -> true);
    }

    /**
     * Start a simulator whose name is specified by {@link #deviceName()}.
     * @param param A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #cmStartSimulator()
     * @see #rxCheckSimulatorBooted()
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxStartSimulator(@NotNull RetryProtocol param) {
        final ProcessRunner RUNNER = processRunner();
        final String COMMAND = cmStartSimulator();
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

            rxCheckSimulatorBooted()
                .retryWhen(e -> e.delay(DELAY, TimeUnit.MILLISECONDS))
        );
    }

    /**
     * Same as above, but uses a default {@link RetryProtocol}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStartSimulator() {
        return rxStartSimulator(RetryProtocol.DEFAULT);
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

    /**
     * Same as above, but uses a default {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStopSimulator() {
        return rxStopSimulator(RetryProtocol.DEFAULT);
    }
    //endregion

    //region Device Methods
    /**
     * Install an app as specified by {@link #app()}.
     * @return A {@link Flowable} instance.
     * @see #cmInstallApp()
     */
    @NotNull
    public Flowable<Boolean> rxInstallApp() {
        ProcessRunner processRunner = processRunner();
        String command = cmInstallApp();
        return processRunner.rxExecute(command).map(a -> true);
    }

    /**
     * Uninstall an app as specified by {@link #appPackage()}.
     * @return A {@link Flowable} instance.
     * @see #cmUninstallApp()
     */
    @NotNull
    public Flowable<Boolean> rxUninstallApp() {
        ProcessRunner processRunner = processRunner();
        String command = cmUninstallApp();
        return processRunner.rxExecute(command).map(a -> true);
    }
    //endregion

    public static final class Builder extends MobileEngine.Builder<IOSEngine> {
        @NotNull
        @Override
        protected IOSEngine createEngineInstance() {
            return new IOSEngine();
        }

        /**
         * Set the {@link #ENGINE#deviceUID} value. This value will be used
         * to start the correct simulator.
         * @param uid A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            ENGINE.deviceUID = uid;
            return this;
        }

        /**
         * Set the {@link #ENGINE#launchTimeout} value.
         * @param timeout A {@link Long} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withLaunchTimeout(long timeout) {
            ENGINE.launchTimeout = timeout;
            return this;
        }

        @NotNull
        @Override
        public IOSEngine build() {
            withAutomation(Automation.XC_UI_TEST);
            withPlatform(Platform.IOS);
            withPlatformView(new IOSView());
            return super.build();
        }
    }
}
