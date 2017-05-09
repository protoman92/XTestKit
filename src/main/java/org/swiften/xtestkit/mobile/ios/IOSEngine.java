package org.swiften.xtestkit.mobile.ios;

import org.swiften.xtestkit.base.BaseEngine;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.mobile.ios.capability.IOSCap;
import org.swiften.xtestkit.mobile.ios.type.IOSDelayType;
import org.swiften.xtestkit.mobile.ios.type.IOSErrorType;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.mobile.Automation;
import org.swiften.xtestkit.mobile.MobileEngine;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.base.type.BaseEngineErrorType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by haipham on 3/31/17.
 */
public class IOSEngine extends MobileEngine<
    IOSElement,
    IOSDriver<IOSElement>> implements
    IOSDelayType,
    IOSErrorType
{
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

    //region Test Setup
    /**
     * @param param A {@link BeforeClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see BaseEngine#rxBeforeClass(BeforeClassParam)
     * @see #startDriverOnlyOnce()
     * @see #rxStartDriver(RetryType)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
        Flowable<Boolean> startApp;

        if (startDriverOnlyOnce()) {
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
     * @see BaseEngine#rxAfterClass(AfterClassParam)
     * @see XCRunHandler#rxStopSimulator(RetryType)
     * @see #startDriverOnlyOnce()
     * @see #rxStopDriver()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        final Flowable<Boolean> QUIT_APP;
        Flowable<Boolean> source;

        switch (testMode()) {
            case SIMULATED:
                source = XC_HANDLER.rxStopSimulator(param);
                break;

            default:
                Exception error = new Exception(BaseEngineErrorType.PLATFORM_UNAVAILABLE);
                source = Flowable.error(error);
                break;
        }

        if (startDriverOnlyOnce()) {
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
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        capabilities.put(IOSMobileCapabilityType.BUNDLE_ID, appPackage());
        capabilities.put(IOSMobileCapabilityType.LAUNCH_TIMEOUT, launchTimeout());
        capabilities.put(MobileCapabilityType.UDID, deviceUID());
        return capabilities;
    }

    @NotNull
    @Override
    protected IOSDriver<IOSElement> driver(@NotNull String serverUrl,
                                           @NotNull DesiredCapabilities caps) {
        try {
            URL url = new URL(serverUrl);
            return new IOSDriver<>(url, caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link IOSEngine}.
     */
    public static final class Builder extends MobileEngine.Builder<IOSEngine> {
        Builder() {
            super(new IOSEngine(), IOSCap.builder());
        }

        /**
         * Set the {@link #deviceUID} value. This value will be used to start
         * the correct simulator.
         * @param uid A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            ENGINE.deviceUID = uid;
            return this;
        }

        /**
         * Set the {@link #launchTimeout} value.
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
    //endregion
}
