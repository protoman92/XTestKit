package com.swiften.xtestkit.engine.mobile.ios;

import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.StartEnvParam;
import com.swiften.xtestkit.engine.base.param.StopEnvParam;
import com.swiften.xtestkit.engine.mobile.Automation;
import com.swiften.xtestkit.engine.mobile.MobileEngine;
import com.swiften.xtestkit.engine.mobile.Platform;
import com.swiften.xtestkit.engine.mobile.ios.protocol.IOSDelayProtocol;
import com.swiften.xtestkit.engine.mobile.ios.protocol.IOSErrorProtocol;
import com.swiften.xtestkit.util.Log;
import com.swiften.xtestkit.util.ProcessRunner;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.remote.CapabilityType;
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
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull private String deviceUID;

    private long launchTimeout;

    IOSEngine() {
        deviceUID = "";
        launchTimeout = simulatorLaunchTimeout();
    }

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

        /* Prevent Appium from resetting/shutting down opened simulators */
        capabilities.put(MobileCapabilityType.NO_RESET, true);

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
     * to add {@link #rxHasCorrectCapabilities()} validation.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxHasAllRequiredInformation()
     * @see #rxHasCorrectCapabilities()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxHasAllRequiredInformation() {
        return Flowable
            .concat(
                super.rxHasAllRequiredInformation(),
                rxHasCorrectCapabilities()
            )
            .all(a -> a)
            .toFlowable();
    }

    @NotNull
    @Override
    protected IOSDriver<IOSElement> createDriverInstance() {
        try {
            URL url = new URL(serverUrl());
            DesiredCapabilities capabilities = desiredCapabilities();
            return new IOSDriver<>(url, capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
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
     * Check whether the information passed to {@link DesiredCapabilities}
     * is correct. For e.g., we can check the app file extension to make sure
     * that simulator builds have .app extension, while device builds have
     * .ipa.
     * @return A {@link Boolean} value.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxHasCorrectCapabilities() {
        return Flowable
            .concatArray(
                rxHasCorrectFileExtension()
            )
            .all(a -> a)
            .toFlowable();
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

        return Flowable.error(new Exception(APP_EXTENSION_INCORRECT));
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
     * @param param A {@link StartEnvParam} instance.
     * @return A {@link Flowable} instance.
     * @see #cmStartSimulator()
     * @see #rxCheckSimulatorBooted()
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxStartSimulator(@NotNull StartEnvParam param) {
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
     * Same as above, but uses a default {@link StartEnvParam}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStartSimulator() {
        StartEnvParam param = StartEnvParam.newBuilder().build();
        return rxStartSimulator(param);
    }
    //endregion

    //region Stop Simulator
    /**
     * Stop the currently active simulator.
     * @param param A {@link StopEnvParam} instance.
     * @return A {@link Flowable} instance.
     * @see #cmStopSimulator()
     */
    @NotNull
    public Flowable<Boolean> rxStopSimulator(@NotNull StopEnvParam param) {
        ProcessRunner processRunner = processRunner();
        String command = cmStopSimulator();

        return processRunner
            .rxExecute(command)
            .map(a -> true)
            .onErrorReturnItem(true);
    }

    /**
     * Same as above, but uses a default {@link StopEnvParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStopSimulator() {
        StopEnvParam param = StopEnvParam.newBuilder().build();
        return rxStopSimulator(param);
    }
    //endregion

    //region Start and Stop Test Environment
    /**
     * @param param A {@link StartEnvParam} instance.
     * @return A {@link Flowable} instance.
     * @see com.swiften.xtestkit.engine.base.PlatformEngine#rxStartTestEnvironment(StartEnvParam)
     * @see #rxStartSimulator(StartEnvParam)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxStartTestEnvironment(@NotNull StartEnvParam param) {
        return Flowable.just(true);
//        switch (testMode()) {
//            case EMULATOR:
//                return rxStartSimulator(param);
//
//            default:
//                return Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
//        }
    }

    /**
     * @param param A {@link StopEnvParam} instance.
     * @return A {@link Flowable} instance.
     * @see com.swiften.xtestkit.engine.base.PlatformEngine#rxStopTestEnvironment(StopEnvParam)
     * @see #rxStopSimulator(StopEnvParam)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxStopTestEnvironment(@NotNull StopEnvParam param) {
        return Flowable.just(true);
//        switch (testMode()) {
//            case EMULATOR:
//                return rxStopSimulator(param);
//
//            default:
//                return Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
//        }
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
            withPlatform(Platform.IOS);
            withAutomation(Automation.XC_UI_TEST);
            return super.build();
        }
    }
}
