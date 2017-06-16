package org.swiften.xtestkit.ios;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkit.ios.capability.IOSEngineCapability;
import org.swiften.xtestkit.ios.element.choice.IOSChoiceSelectorType;
import org.swiften.xtestkit.ios.element.date.IOSDateActionType;
import org.swiften.xtestkit.ios.element.general.IOSActionType;
import org.swiften.xtestkit.ios.element.locator.IOSLocatorType;
import org.swiften.xtestkit.ios.element.search.IOSSearchActionType;
import org.swiften.xtestkit.ios.element.switcher.IOSSwitcherActionType;
import org.swiften.xtestkit.ios.type.IOSDelayType;
import org.swiften.xtestkit.ios.type.IOSErrorType;
import org.swiften.xtestkit.mobile.Automation;
import org.swiften.xtestkit.mobile.MobileEngine;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.common.RetryType;

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
    IOSErrorType,
    IOSLocatorType,
    IOSSearchActionType,
    IOSSwitcherActionType
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

    protected IOSEngine() {
        XC_HANDLER = new XCRunHandler();
        PLATFORM_VIEW = new IOSView();
        launchTimeout = simulatorLaunchTimeout();
        deviceUID = "";
    }

    //region Getters
    /**
     * Get {@link Platform#IOS}
     * @return {@link Platform} instance.
     * @see Engine#platform()
     * @see Platform#IOS
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
     * @see MobileEngine#automation()
     * @see Automation#XC_UI_TEST
     */
    @NotNull
    public Automation automation() {
        return Automation.XC_UI_TEST;
    }

    /**
     * Return {@link #XC_HANDLER}.
     * @return {@link XCRunHandler} instance.
     * @see #XC_HANDLER
     */
    @NotNull
    public XCRunHandler xcRunHandler() {
        return XC_HANDLER;
    }

    /**
     * Return {@link #deviceUID}.
     * @return {@link String} value.
     * @see #deviceUID
     */
    @NotNull
    public String deviceUID() {
        return deviceUID;
    }

    /**
     * Return {@link #launchTimeout}.
     * @return {@link Long} value.
     * @see #launchTimeout
     */
    public long launchTimeout() {
        return launchTimeout;
    }

    //region Test Setup
    /**
     * Override this method to provide default implementation.
     * @param param {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_beforeClass(RetryType)
     * @see BooleanUtil#isTrue(boolean)
     * @see #rxa_startDriver(RetryType)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxa_beforeClass(@NotNull RetryType param) {
        return Flowable
            .concat(super.rxa_beforeClass(param), rxa_startDriver(param))
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_afterClass(RetryType)
     * @see ObjectUtil#nonNull(Object)
     * @see TestMode#isTestingOnSimulatedEnvironment()
     * @see XCRunHandler#rxa_stopSimulator(RetryType)
     * @see #testMode()
     * @see #rxa_stopDriver()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxa_afterClass(@NotNull RetryType param) {
        Flowable<Boolean> source;
        TestMode testMode = testMode();

//        if (testMode.isTestingOnSimulatedEnvironment()) {
//            source = XC_HANDLER.rxa_stopSimulator(param);
//        } else {
            source = Flowable.just(true);
//        }

        return Flowable
            .concatArray(super.rxa_afterClass(param), source, rxa_stopDriver())
            .all(ObjectUtil::nonNull)
            .toFlowable();
    }
    //endregion

    //region Appium Setup
    /**
     * Override this method to provide default implementation.
     * @return {@link Map} instance.
     * @see MobileEngine#capabilities()
     * @see #appPackage()
     * @see #deviceUID()
     * @see #launchTimeout()
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = new HashMap<>(super.capabilities());
        capabilities.put(IOSMobileCapabilityType.BUNDLE_ID, appPackage());
        capabilities.put(IOSMobileCapabilityType.LAUNCH_TIMEOUT, launchTimeout());
        capabilities.put(MobileCapabilityType.UDID, deviceUID());
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
    public static class Builder extends MobileEngine.Builder<IOSEngine> {
        /**
         * Override this constructor to provide custom {@link IOSEngine}
         * and {@link IOSEngineCapability.Builder} instances.
         * @param engine {@link IOSEngine} instance.
         * @param builder {@link IOSEngineCapability.Builder} instance.
         */
        protected Builder(@NotNull IOSEngine engine,
                          @NotNull IOSEngineCapability.Builder builder) {
            super(engine, builder);
        }

        Builder() {
            this(new IOSEngine(), IOSEngineCapability.builder());
        }

        /**
         * Set the {@link #deviceUID} value. This value will be used to start
         * the correct simulator.
         * @param uid {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            ENGINE.deviceUID = uid;
            return this;
        }

        /**
         * Set the {@link #launchTimeout} value.
         * @param timeout {@link Long} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withLaunchTimeout(long timeout) {
            ENGINE.launchTimeout = timeout;
            return this;
        }
    }
    //endregion
}
