package com.swiften.xtestkit.engine.mobile.ios;

import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.kit.param.AfterClassParam;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.Automation;
import com.swiften.xtestkit.engine.mobile.MobileEngine;
import com.swiften.xtestkit.engine.base.Platform;
import com.swiften.xtestkit.engine.mobile.ios.protocol.IOSDelayProtocol;
import com.swiften.xtestkit.engine.mobile.ios.protocol.IOSErrorProtocol;
import com.swiften.xtestkit.kit.param.BeforeClassParam;
import com.swiften.xtestkit.util.BooleanUtil;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Flowable;
import org.apache.commons.io.FilenameUtils;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeMethod;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @NotNull private final XCRunHandler XC_HANDLER;
    @NotNull private String deviceUID;

    private long launchTimeout;

    IOSEngine() {
        XC_HANDLER = XCRunHandler.builder().build();
        deviceUID = "";
        launchTimeout = simulatorLaunchTimeout();
    }

    //region Getters
    /**
     * Return {@link #XC_HANDLER}.
     * @return A {@link XCRunHandler} instance.
     */
    @NotNull
    public XCRunHandler xcRunHandler() {
        return XC_HANDLER;
    }

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
     * @param param A {@link BeforeClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeClass(BeforeClassParam)
     * @see #rxStartDriver(RetryProtocol)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
        Flowable<Boolean> startApp;

        if (startDriverOnlyOnce) {
            startApp = rxStartDriver(param);
        } else {
            startApp = Flowable.just(true);
        }

        return Flowable
            .concat(super.rxBeforeClass(param), startApp)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }

    /**
     * @param param A {@link AfterClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterClass(AfterClassParam)
     * @see XCRunHandler#rxStopSimulator(RetryProtocol)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        final Flowable<Boolean> QUIT_APP;
        Flowable<Boolean> source;

        switch (testMode()) {
            case EMULATOR:
                source = XC_HANDLER.rxStopSimulator(param);
                break;

            default:
                Exception error = new Exception(PLATFORM_UNAVAILABLE);
                source = Flowable.error(error);
                break;
        }

        if (startDriverOnlyOnce) {
            QUIT_APP = rxStopDriver();
        } else {
            QUIT_APP = Flowable.just(true);
        }

        return Flowable
            .concat(super.rxAfterClass(param), source)
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .flatMap(a -> QUIT_APP);
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
         * test are running on simulator or real device */
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
