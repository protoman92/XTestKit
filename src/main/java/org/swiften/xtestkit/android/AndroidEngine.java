package org.swiften.xtestkit.android;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.capability.AndroidEngineCapability;
import org.swiften.xtestkit.android.element.choice.AndroidChoiceSelectorType;
import org.swiften.xtestkit.android.element.date.AndroidDateActionType;
import org.swiften.xtestkit.android.element.general.AndroidActionType;
import org.swiften.xtestkit.android.element.input.AndroidInputActionType;
import org.swiften.xtestkit.android.element.input.AndroidKeyboardActionType;
import org.swiften.xtestkit.android.element.locator.AndroidLocatorType;
import org.swiften.xtestkit.android.element.password.AndroidPasswordActionType;
import org.swiften.xtestkit.android.element.search.AndroidSearchActionType;
import org.swiften.xtestkit.android.element.switcher.AndroidSwitcherActionType;
import org.swiften.xtestkit.android.param.ClearCacheParam;
import org.swiften.xtestkit.android.param.StartEmulatorParam;
import org.swiften.xtestkit.android.param.StopEmulatorParam;
import org.swiften.xtestkit.android.type.*;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkit.base.type.AppPackageType;
import org.swiften.xtestkit.mobile.Automation;
import org.swiften.xtestkit.mobile.MobileEngine;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.swiften.xtestkitcomponents.system.network.NetworkHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haipham on 3/22/17.
 */
public class AndroidEngine extends
    MobileEngine<AndroidDriver<AndroidElement>> implements
    ADBHandlerProviderType,
    AndroidActionType,
    AndroidChoiceSelectorType,
    AndroidDateActionType,
    AndroidInputActionType,
    AndroidInstanceProviderType,
    AndroidLocatorType,
    AndroidKeyboardActionType,
    AndroidPasswordActionType,
    AndroidSearchActionType,
    AndroidSDKProviderType,
    AndroidSwitcherActionType
{
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final PlatformView PLATFORM_VIEW;
    @NotNull String appActivity;
    @Nullable AndroidInstance androidInstance;

    protected AndroidEngine() {
        super();
        ADB_HANDLER = new ADBHandler();
        PLATFORM_VIEW = new AndroidView();
        appActivity = "";
    }

    //region DistinctiveType
    /**
     * Since we can control multiple emulators/devices at once, there is no
     * need to have only one {@link AndroidEngine} active at a time. Therefore,
     * we override {@link Engine#comparisonObject()} to disable
     * this comparison filter.
     * @return {@link Object} instance.
     * @see Engine#comparisonObject()
     */
    @NotNull
    @Override
    public Object comparisonObject() {
        return deviceName();
    }
    //endregion

//    //region TestListenerType
//    @NotNull
//    @Override
//    public Flowable<Boolean> rxa_onFreshStart() {
//        /* We restart adb server at the start of all test to avoid problems
//         * with inactive adb instances */
//        return super.rxa_onFreshStart().flatMap(a -> ADB_HANDLER.rxa_restartAdb());
//    }
//    //endregion

    //region Getters
    /**
     * Get {@link Platform#ANDROID}
     * @return {@link Platform} instance.
     * @see Platform#ANDROID
     * @see MobileEngine#platform()
     */
    @NotNull
    @Override
    public Platform platform() {
        return Platform.ANDROID;
    }

    /**
     * Get {@link AndroidView}.
     * @return {@link PlatformView} instance.
     * @see #PLATFORM_VIEW
     */
    @NotNull
    @Override
    public PlatformView platformView() {
        return PLATFORM_VIEW;
    }

    /**
     * Get {@link Automation#APPIUM} or {@link Automation#SELENDROID}.
     * @return {@link Automation} instance.
     * @see Automation#APPIUM
     * @see Automation#SELENDROID
     * @see MobileEngine#automation()
     * @see #platformVersion()
     */
    @NotNull
    public Automation automation() {
        String version = platformVersion();

        if (version.compareToIgnoreCase("4.2") < 0) {
            return Automation.SELENDROID;
        } else {
            return Automation.APPIUM;
        }
    }

    /**
     * Return {@link #ADB_HANDLER}.
     * @return {@link ADBHandler} instance.
     * @see #ADB_HANDLER
     */
    @NotNull
    public ADBHandler adbHandler() {
        return ADB_HANDLER;
    }

    /**
     * Return {@link #appActivity}. This can be stubbed out for custom
     * implementation.
     * @return {@link String} value.
     * @see #appActivity
     */
    @NotNull
    public String appActivity() {
        return appActivity;
    }

    /**
     * Return {@link #androidInstance}.
     * @return {@link AndroidInstance} instance.
     * @see ObjectUtil#requireNotNull(Object, String)
     * @see #androidInstance
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @SuppressWarnings("ConstantConditions")
    public AndroidInstance androidInstance() {
        ObjectUtil.requireNotNull(androidInstance, NOT_AVAILABLE);
        return androidInstance;
    }
    //endregion

    //region Test Setup
    /**
     * Override this method to provide default implementation.
     * @param PARAM {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_beforeClass(RetryType)
     * @see ADBHandler#rxa_disableAnimations(DeviceUIDType)
     * @see ADBHandler#rxa_startEmulator(StartEmulatorParam)
     * @see ADBHandler#rxa_clearCache(AppPackageType)
     * @see ADBHandler#rxe_availablePort(RetryType)
     * @see ADBHandler#rxe_appInstalled(AppPackageType)
     * @see AndroidInstance#setPort(int)
     * @see ClearCacheParam.Builder#withAppPackage(String)
     * @see ClearCacheParam.Builder#withDeviceUIDType(DeviceUIDType)
     * @see ClearCacheParam.Builder#withRetryType(RetryType)
     * @see ObjectUtil#nonNull(Object)
     * @see TestMode#isTestingOnSimulatedEnvironment()
     * @see #adbHandler()
     * @see #androidInstance()
     * @see #appPackage()
     * @see #deviceName()
     * @see #testMode()
     * @see #rxa_startDriver(RetryType)
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxa_beforeClass(@NotNull final RetryType PARAM) {
        final AndroidEngine THIS = this;
        final ADBHandler HANDLER = adbHandler();
        final AndroidInstance A_INSTANCE = androidInstance();
        final String APP_PACKAGE = appPackage();
        Flowable<Boolean> source;
        TestMode testMode = testMode();

        if (testMode.isTestingOnSimulatedEnvironment()) {
            source = HANDLER.rxe_availablePort(PARAM)
                .doOnNext(A_INSTANCE::setPort)
                .map(a -> StartEmulatorParam.builder()
                    .withDeviceName(deviceName())
                    .withAndroidInstance(A_INSTANCE)
                    .withRetries(100)
                    .build())
                .flatMap(HANDLER::rxa_startEmulator)
                .onErrorReturnItem(true);
        } else {
            /* Assuming the device is already started up */
            source = Flowable.just(true);
        }

        return Flowable
            .concatArray(super.rxa_beforeClass(PARAM), source)
            .all(ObjectUtil::nonNull)
            .toFlowable()
            .flatMap(a -> Flowable.concatArray(
                HANDLER.rxa_disableAnimations(A_INSTANCE).onErrorReturnItem(true),

                /* Clear cached data such as SharedPreferences. If the app is
                 * not found in the active device/emulator, throw an error */
                Flowable.<ClearCacheParam>create(obs -> {
                    /* At this time, the AndroidInstance should already have
                     * information about the device port */
                    ClearCacheParam ccParam = ClearCacheParam.builder()
                        .withAppPackage(APP_PACKAGE)
                        .withDeviceUIDType(A_INSTANCE)
                        .withRetryType(PARAM)
                        .build();

                    obs.onNext(ccParam);
                    obs.onComplete();
                }, BackpressureStrategy.BUFFER
                ).flatMap(b -> Flowable.concatArray(
                    HANDLER.rxe_appInstalled(b),
                    HANDLER.rxa_clearCache(b)
                ))).all(ObjectUtil::nonNull).toFlowable())
            .flatMap(a -> THIS.rxa_startDriver(PARAM));
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_afterClass(RetryType)
     * @see ADBHandler#rxa_stopEmulator(StopEmulatorParam)
     * @see AndroidInstance#port()
     * @see BooleanUtil#isTrue(boolean)
     * @see NetworkHandler#markPortAvailable(int)
     * @see TestMode#isTestingOnSimulatedEnvironment()
     * @see #adbHandler()
     * @see #androidInstance()
     * @see #networkHandler()
     * @see #testMode()
     * @see #rxa_resetApp()
     * @see #rxa_stopDriver()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxa_afterClass(@NotNull RetryType param) {
        AndroidInstance androidInstance = androidInstance();
        final NetworkHandler HANDLER = networkHandler();
        final int PORT = androidInstance.port();
        Flowable<Boolean> source;
        TestMode mode = testMode();

//        if (mode.isTestingOnSimulatedEnvironment()) {
//            StopEmulatorParam seParam = StopEmulatorParam.builder()
//                .withRetryType(param)
//                .withPortType(androidInstance)
//                .build();
//
//            source = adbHandler().rxa_stopEmulator(seParam);
//        } else {
            source = Flowable.just(true);
//        }

        return Flowable
            .concatArray(super.rxa_afterClass(param), rxa_stopDriver(), source)
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .doOnNext(a -> HANDLER.markPortAvailable(PORT));
    }
    //endregion

    //region Appium Setup
    /**
     * Override this method to provide default implementation.
     * @return {@link Map} of capabilities.
     * @see MobileEngine#capabilities()
     * @see #appPackage()
     * @see #appActivity()
     * @see #deviceName()
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = new HashMap<>(super.capabilities());
        capabilities.put(AndroidMobileCapabilityType.APP_PACKAGE, appPackage());
        capabilities.put(AndroidMobileCapabilityType.APP_ACTIVITY, appActivity());
        capabilities.put(AndroidMobileCapabilityType.AVD, deviceName());
        return capabilities;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link AndroidDriver} instance.
     * @see Engine#driver(String, DesiredCapabilities)
     */
    @NotNull
    @Override
    protected AndroidDriver<AndroidElement> driver(@NotNull String serverUrl,
                                                   @NotNull DesiredCapabilities caps) {
        try {
            URL url = new URL(serverUrl);
            return new AndroidDriver<>(url, caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region Builder
    /**
     * Builder for {@link AndroidEngine}.
     */
    public static class Builder extends MobileEngine.Builder<AndroidEngine> {
        @NotNull private final AndroidInstance.Builder INSTANCE_BUILDER;

        /**
         * Override this constructor to provide custom {@link AndroidEngine}
         * and {@link AndroidEngineCapability.Builder} instances.
         * @param engine {@link AndroidEngine} instance.
         * @param builder {@link AndroidEngineCapability.Builder} instance.
         */
        protected Builder(@NotNull AndroidEngine engine,
                          @NotNull AndroidEngineCapability.Builder builder) {
            super(engine, builder);
            INSTANCE_BUILDER = AndroidInstance.builder();
        }

        Builder() {
            this(new AndroidEngine(), AndroidEngineCapability.builder());
        }

        /**
         * Override to set {@link AndroidInstance#deviceName}.
         * @param name {@link String} value.
         * @return {@link Builder} instance.
         * @see MobileEngine.Builder#withDeviceName(String)
         */
        @NotNull
        @Override
        public MobileEngine.Builder<AndroidEngine> withDeviceName(@NotNull String name) {
            INSTANCE_BUILDER.withDeviceName(name);
            return super.withDeviceName(name);
        }

        /**
         * Override to set {@link AndroidInstance#mode}.
         * @param mode {@link TestMode} instance.
         * @return {@link Builder} instance.
         * @see MobileEngine.Builder#withTestMode(TestMode)
         */
        @NotNull
        @Override
        public Engine.Builder<AndroidEngine> withTestMode(@NotNull TestMode mode) {
            INSTANCE_BUILDER.withTestMode(mode);
            return super.withTestMode(mode);
        }

        /**
         * Set {@link #appActivity}.
         * @param appActivity {@link String} value.
         * @return {@link Builder} instance.
         */
        public Builder withAppActivity(@NotNull String appActivity) {
            ENGINE.appActivity = appActivity;
            return this;
        }

        /**
         * Set the {@link AndroidInstance#uid} value.
         * @param uid {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            INSTANCE_BUILDER.withDeviceUID(uid);
            return this;
        }

        /**
         * Set {@link #platformVersion}.
         * @param sdk {@link AndroidSDK} instance.
         * @return {@link Builder} instance.
         * @see AndroidSDK#version()
         * @see #withPlatformVersion(String)
         */
        @NotNull
        public Builder withSDK(@NotNull AndroidSDK sdk) {
            String version = sdk.version();
            withPlatformVersion(version);
            return this;
        }

        /**
         * Get {@link #ENGINE}.
         * @return {@link Engine} instance.
         * @see #ENGINE
         * @see #androidInstance
         */
        @NotNull
        @Override
        public AndroidEngine build() {
            ENGINE.androidInstance = INSTANCE_BUILDER.build();
            return super.build();
        }
    }
    //endregion
}
