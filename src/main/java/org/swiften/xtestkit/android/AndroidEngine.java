package org.swiften.xtestkit.android;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.capability.AndroidCapability;
import org.swiften.xtestkit.android.element.choice.AndroidChoiceSelectorType;
import org.swiften.xtestkit.android.element.date.AndroidDateActionType;
import org.swiften.xtestkit.android.element.general.AndroidActionType;
import org.swiften.xtestkit.android.element.input.AndroidInputActionType;
import org.swiften.xtestkit.android.element.input.AndroidKeyboardActionType;
import org.swiften.xtestkit.android.element.locator.AndroidLocatorType;
import org.swiften.xtestkit.android.element.password.AndroidPasswordActionType;
import org.swiften.xtestkit.android.element.search.AndroidSearchActionType;
import org.swiften.xtestkit.android.param.ClearCacheParam;
import org.swiften.xtestkit.android.param.StartEmulatorParam;
import org.swiften.xtestkit.android.param.StopEmulatorParam;
import org.swiften.xtestkit.android.type.*;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkit.base.type.AppPackageType;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.mobile.Automation;
import org.swiften.xtestkit.mobile.MobileEngine;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.system.network.NetworkHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haipham on 3/22/17.
 */
public class AndroidEngine extends
    MobileEngine<AndroidDriver<AndroidElement>> implements
    ADBHandlerContainerType,
    AndroidActionType,
    AndroidChoiceSelectorType,
    AndroidDateActionType,
    AndroidErrorType,
    AndroidInputActionType,
    AndroidInstanceContainerType,
    AndroidLocatorType,
    AndroidKeyboardActionType,
    AndroidPasswordActionType,
    AndroidSearchActionType
{
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
     */
    @NotNull
    public ADBHandler adbHandler() {
        return ADB_HANDLER;
    }

    /**
     * Return {@link #appActivity}. This can be stubbed out for custom
     * implementation.
     * @return {@link String} value.
     */
    @NotNull
    public String appActivity() {
        return appActivity;
    }

    /**
     * Return {@link #androidInstance}.
     * @return {@link AndroidInstance} instance.
     */
    @NotNull
    public AndroidInstance androidInstance() {
        if (ObjectUtil.nonNull(androidInstance)) {
            return androidInstance;
        } else {
            throw new RuntimeException(ANDROID_INSTANCE_UNAVAILABLE);
        }
    }
    //endregion

    //region Test Setup
    /**
     * @param PARAM {@link BeforeClassParam} instance.
     * @return {@link Flowable} instance.
     * @see ADBHandler#rxa_disableEmulatorAnimations(DeviceUIDType)
     * @see ADBHandler#rxa_startEmulator(StartEmulatorParam)
     * @see ADBHandler#rxe_availablePort(RetryType)
     * @see AndroidInstance#setPort(int)
     * @see BooleanUtil#isTrue(boolean)
     * @see Engine#rxa_beforeClass(BeforeClassParam)
     * @see TestMode#isTestingOnSimulatedEnvironment()
     * @see #adbHandler()
     * @see #androidInstance()
     * @see #deviceName()
     * @see #testMode()
     * @see #rxa_startDriver(RetryType)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxa_beforeClass(@NotNull final BeforeClassParam PARAM) {
        final ADBHandler HANDLER = adbHandler();
        final AndroidInstance ANDROID_INSTANCE = androidInstance();
        final Flowable<Boolean> START_APP = rxa_startDriver(PARAM);
        Flowable<Boolean> source;
        TestMode testMode = testMode();

        if (testMode.isTestingOnSimulatedEnvironment()) {
            source = HANDLER.rxe_availablePort(PARAM)
                .doOnNext(ANDROID_INSTANCE::setPort)
                .map(a -> StartEmulatorParam.builder()
                    .withDeviceName(deviceName())
                    .withAndroidInstance(ANDROID_INSTANCE)
                    .withRetries(100)
                    .build())
                .flatMap(HANDLER::rxa_startEmulator);
        } else {
            /* Assuming the device is already started up */
            source = Flowable.just(true);
        }

        return Flowable
            .concat(super.rxa_beforeClass(PARAM), source)
            .all(BooleanUtil::toTrue)
            .toFlowable()

            /* Disable animations to avoid erratic behaviors */
            .flatMap(a -> HANDLER
                .rxa_disableEmulatorAnimations(ANDROID_INSTANCE)

                /* This is not absolutely crucial, so even if there is an
                 * error, we proceed anyway */
                .onErrorResumeNext(Flowable.just(true)))
            .flatMap(a -> START_APP);
    }

    /**
     * @param param {@link AfterClassParam} instance.
     * @return {@link Flowable} instance.
     * @see ADBHandler#rxa_stopEmulator(StopEmulatorParam)
     * @see AndroidInstance#port()
     * @see BooleanUtil#isTrue(boolean)
     * @see Engine#rxa_afterClass(AfterClassParam)
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
    public Flowable<Boolean> rxa_afterClass(@NotNull AfterClassParam param) {
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
            .concat(super.rxa_afterClass(param), rxa_stopDriver(), source)
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .doOnNext(a -> HANDLER.markPortAvailable(PORT));
    }

    /**
     * @param param {@link AfterParam} instance.
     * @return {@link Flowable} instance.
     * @see ADBHandler#rxa_clearCache(AppPackageType)
     * @see Engine#rxa_afterMethod(AfterParam)
     * @see #adbHandler()
     * @see #androidInstance()
     * @see #appPackage()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxa_afterMethod(@NotNull AfterParam param) {
        final ADBHandler ADB_HANDLER = adbHandler();

        ClearCacheParam CC_PARAM = ClearCacheParam.builder()
            .withAppPackage(appPackage())
            .withDeviceUIDType(androidInstance())
            .withRetryType(param)
            .build();

        /* Clear cached data such as SharedPreferences. If the app is not
         * found in the active device/emulator, throw an error */
        Flowable<Boolean> clearCache = ADB_HANDLER
            .rxe_appInstalled(CC_PARAM)
            .flatMap(a -> ADB_HANDLER.rxa_clearCache(CC_PARAM));

        return Flowable
            .concat(super.rxa_afterMethod(param), clearCache)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }
    //endregion

    //region Appium Setup
    /**
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
        @NotNull private final AndroidInstance.Builder ANDROID_INSTANCE_BUILDER;

        /**
         * Override this constructor to provide custom {@link AndroidEngine}
         * and {@link AndroidCapability.Builder} instances.
         * @param engine {@link AndroidEngine} instance.
         * @param builder {@link AndroidCapability.Builder} instance.
         */
        protected Builder(@NotNull AndroidEngine engine,
                          @NotNull AndroidCapability.Builder builder) {
            super(engine, builder);
            ANDROID_INSTANCE_BUILDER = AndroidInstance.builder();
        }

        Builder() {
            this(new AndroidEngine(), AndroidCapability.builder());
        }

        /**
         * Override to set {@link AndroidInstance#deviceName}.
         * @param name {@link String} value.
         * @return The current {@link Builder} instance.
         * @see MobileEngine.Builder#withDeviceName(String)
         */
        @NotNull
        @Override
        public MobileEngine.Builder<AndroidEngine> withDeviceName(@NotNull String name) {
            ANDROID_INSTANCE_BUILDER.withDeviceName(name);
            return super.withDeviceName(name);
        }

        /**
         * Override to set {@link AndroidInstance#mode}.
         * @param mode {@link TestMode} instance.
         * @return The current {@link Builder} instance.
         * @see MobileEngine.Builder#withTestMode(TestMode)
         */
        @NotNull
        @Override
        public Engine.Builder<AndroidEngine> withTestMode(@NotNull TestMode mode) {
            ANDROID_INSTANCE_BUILDER.withTestMode(mode);
            return super.withTestMode(mode);
        }

        /**
         * Set {@link #appActivity}.
         * @param appActivity {@link String} value.
         * @return The current {@link Builder} instance.
         */
        public Builder withAppActivity(@NotNull String appActivity) {
            ENGINE.appActivity = appActivity;
            return this;
        }

        /**
         * Set the {@link AndroidInstance#uid} value.
         * @param uid {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            ANDROID_INSTANCE_BUILDER.withDeviceUID(uid);
            return this;
        }

        /**
         * Set {@link #platformVersion}.
         * @param sdk {@link AndroidVersion} instance.
         * @return {@link Builder} instance.
         * @see AndroidVersion#version()
         * @see #withPlatformVersion(String)
         */
        @NotNull
        public Builder withSDK(@NotNull AndroidVersion sdk) {
            String version = sdk.version();
            withPlatformVersion(version);
            return this;
        }

        @NotNull
        @Override
        public AndroidEngine build() {
            ENGINE.androidInstance = ANDROID_INSTANCE_BUILDER.build();
            return super.build();
        }
    }
    //endregion
}
