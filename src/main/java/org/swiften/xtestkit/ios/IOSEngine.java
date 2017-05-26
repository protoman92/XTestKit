package org.swiften.xtestkit.ios;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.ios.capability.IOSCapability;
import org.swiften.xtestkit.ios.element.action.choice.IOSChoiceSelectorType;
import org.swiften.xtestkit.ios.element.action.date.IOSDateActionType;
import org.swiften.xtestkit.ios.element.action.general.IOSActionType;
import org.swiften.xtestkit.ios.type.IOSDelayType;
import org.swiften.xtestkit.ios.type.IOSErrorType;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.mobile.Automation;
import org.swiften.xtestkit.mobile.MobileEngine;
import org.swiften.xtestkit.mobile.Platform;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haipham on 3/31/17.
 */
public class IOSEngine extends MobileEngine<IOSDriver<IOSElement>> implements
    IOSActionType,
    IOSChoiceSelectorType,
    IOSDateActionType,
    IOSDelayType,
    IOSErrorType
{
    /**
     * Get a new {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final XCRunHandler XC_HANDLER;
    @NotNull private final PlatformView PLATFORM_VIEW;
    @NotNull private String deviceUID;

    private long launchTimeout;

    IOSEngine() {
        XC_HANDLER = new XCRunHandler();
        PLATFORM_VIEW = new IOSView();
        launchTimeout = simulatorLaunchTimeout();
        deviceUID = "";
    }

    //region Getters
    /**
     * Get {@link Platform#IOS}
     * @return {@link Platform} instance.
     * @see Platform#IOS
     * @see Engine#platform()
     */
    @NotNull
    @Override
    public Platform platform() {
        return Platform.IOS;
    }

    /**
     * Get {@link IOSView}.
     * @return {@link PlatformView} instance.
     * @see #PLATFORM_VIEW
     */
    @NotNull
    @Override
    public PlatformView platformView() {
        return PLATFORM_VIEW;
    }

    /**
     * Get {@link Automation#XC_UI_TEST}.
     * @return {@link Automation} instance.
     * @see Automation#XC_UI_TEST
     * @see MobileEngine#automation()
     */
    @NotNull
    public Automation automation() {
        return Automation.XC_UI_TEST;
    }

    /**
     * Return {@link #XC_HANDLER}.
     * @return {@link XCRunHandler} instance.
     */
    @NotNull
    public XCRunHandler xcRunHandler() {
        return XC_HANDLER;
    }

    /**
     * Return {@link #deviceUID}.
     * @return {@link String} value.
     */
    @NotNull
    public String deviceUID() {
        return deviceUID;
    }

    /**
     * Return {@link #launchTimeout}.
     * @return {@link Long} value.
     */
    public long launchTimeout() {
        return launchTimeout;
    }

    //region Test Setup
    /**
     * @param param {@link BeforeClassParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_beforeClass(BeforeClassParam)
     * @see #rx_startDriver(RetryType)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rx_beforeClass(@NotNull BeforeClassParam param) {
        final Flowable<Boolean> START_APP = rx_startDriver(param);
        return super.rx_beforeClass(param).flatMap(a -> START_APP);
    }

    /**
     * @param param {@link AfterClassParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_afterClass(AfterClassParam)
     * @see XCRunHandler#rxStopSimulator(RetryType)
     * @see #rx_stopDriver()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rx_afterClass(@NotNull AfterClassParam param) {
        final IOSEngine THIS = this;
        final Flowable<Boolean> SOURCE;

        switch (testMode()) {
//            case SIMULATED:
//                SOURCE = XC_HANDLER.rxStopSimulator(param);
//                break;

            default:
                SOURCE = Flowable.just(true);
                break;
        }

        return super.rx_afterClass(param)
            .flatMap(a -> SOURCE)
            .flatMap(a -> THIS.rx_stopDriver());
    }
    //endregion

    //region Appium Setup
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = new HashMap<>(super.capabilities());
        capabilities.put(IOSMobileCapabilityType.BUNDLE_ID, appPackage());
        capabilities.put(IOSMobileCapabilityType.LAUNCH_TIMEOUT, launchTimeout());
        capabilities.put(MobileCapabilityType.UDID, deviceUID());
        capabilities.put("fastReset", true);
        capabilities.put("autoLaunch", true);
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
            super(new IOSEngine(), IOSCapability.builder());
        }

        /**
         * Set the {@link #deviceUID} value. This value will be used to start
         * the correct simulator.
         * @param uid {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            ENGINE.deviceUID = uid;
            return this;
        }

        /**
         * Set the {@link #launchTimeout} value.
         * @param timeout {@link Long} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withLaunchTimeout(long timeout) {
            ENGINE.launchTimeout = timeout;
            return this;
        }
    }
    //endregion
}
