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
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.capability.AndroidCapability;
import org.swiften.xtestkit.android.element.action.choice.AndroidChoiceSelectorType;
import org.swiften.xtestkit.android.element.action.date.AndroidDateActionType;
import org.swiften.xtestkit.android.element.action.general.AndroidActionType;
import org.swiften.xtestkit.android.element.action.input.AndroidInputActionType;
import org.swiften.xtestkit.android.element.action.input.AndroidKeyboardActionType;
import org.swiften.xtestkit.android.element.action.password.AndroidPasswordActionType;
import org.swiften.xtestkit.android.param.ClearCacheParam;
import org.swiften.xtestkit.android.param.StartEmulatorParam;
import org.swiften.xtestkit.android.param.StopEmulatorParam;
import org.swiften.xtestkit.android.type.ADBHandlerContainerType;
import org.swiften.xtestkit.android.type.AndroidErrorType;
import org.swiften.xtestkit.android.type.AndroidInstanceContainerType;
import org.swiften.xtestkit.android.type.DeviceUIDType;
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
    AndroidKeyboardActionType,
    AndroidPasswordActionType<AndroidDriver<AndroidElement>>
{
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final PlatformView PLATFORM_VIEW;
    @NotNull String appActivity;
    @Nullable AndroidInstance androidInstance;

    AndroidEngine() {
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
//    public Flowable<Boolean> rx_onFreshStart() {
//        /* We restart adb server at the start of all test to avoid problems
//         * with inactive adb instances */
//        return super.rx_onFreshStart().flatMap(a -> ADB_HANDLER.rx_restartAdb());
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
     * @see ADBHandler#rx_disableEmulatorAnimations(DeviceUIDType)
     * @see ADBHandler#rx_startEmulator(StartEmulatorParam)
     * @see AndroidInstance#setPort(int)
     * @see Engine#rx_beforeClass(BeforeClassParam)
     * @see #adbHandler()
     * @see #androidInstance()
     * @see #testMode()
     * @see #rx_startDriver(RetryType)
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rx_beforeClass(@NotNull final BeforeClassParam PARAM) {
        final ADBHandler HANDLER = adbHandler();
        final AndroidInstance ANDROID_INSTANCE = androidInstance();
        final Flowable<Boolean> START_APP = rx_startDriver(PARAM);
        final Flowable<Boolean> SOURCE;

        switch (testMode()) {
            case SIMULATED:
                SOURCE = HANDLER.rx_availablePort(PARAM)
                    .doOnNext(ANDROID_INSTANCE::setPort)
                    .map(a -> StartEmulatorParam.builder()
                        .withDeviceName(deviceName())
                        .withAndroidInstance(ANDROID_INSTANCE)
                        .withRetries(100)
                        .build())
                    .flatMap(HANDLER::rx_startEmulator);

                break;

            case ACTUAL:
                /* Assuming the device is already started up */
                SOURCE = Flowable.just(true);
                break;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }

        return super.rx_beforeClass(PARAM)
            .flatMap(a -> SOURCE)

            /* Disable animations to avoid erratic behaviors */
            .flatMap(a -> HANDLER
                .rx_disableEmulatorAnimations(ANDROID_INSTANCE)

                /* This is not absolutely crucial, so even if
                 * there is an error, we proceed anyway */
                .onErrorResumeNext(Flowable.just(true)))
            .flatMap(a -> START_APP);
    }

    /**
     * @param param {@link AfterClassParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_afterClass(AfterClassParam)
     * @see ADBHandler#rx_stopEmulator(StopEmulatorParam)
     * @see #rxResetApp()
     * @see #rx_stopDriver()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rx_afterClass(@NotNull AfterClassParam param) {
        AndroidInstance androidInstance = androidInstance();
        final NetworkHandler HANDLER = networkHandler();
        final int PORT = androidInstance.port();
        final Flowable<Boolean> SOURCE;
        final Flowable<Boolean> QUIT_APP = rx_stopDriver();

        switch (testMode()) {
            case ACTUAL:
                SOURCE = Flowable.just(true);
                break;

            case SIMULATED:
                SOURCE = Flowable.just(true);
//                StopEmulatorParam seParam = StopEmulatorParam
//                    .builder()
//                    .withRetryType(param)
//                    .withPortType(androidInstance)
//                    .build();
//
//                SOURCE = adbHandler().rx_stopEmulator(seParam);
                break;

            default:
                SOURCE = RxUtil.error(NOT_AVAILABLE);
                break;
        }

        return super.rx_afterClass(param)
            .flatMap(a -> QUIT_APP)
            .flatMap(a -> SOURCE)
            .doOnNext(a -> HANDLER.markPortAsAvailable(PORT));
    }

    /**
     * @param param {@link AfterParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_afterMethod(AfterParam)
     * @see ADBHandler#rx_clearCache(AppPackageType)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rx_afterMethod(@NotNull AfterParam param) {
        final ADBHandler ADB_HANDLER = adbHandler();

        ClearCacheParam CC_PARAM = ClearCacheParam
            .builder()
            .withAppPackage(appPackage())
            .withDeviceUIDType(androidInstance())
            .withRetryType(param)
            .build();

        /* Clear cached data such as SharedPreferences. If the app is not
         * found in the active device/emulator, throw an error */
        Flowable<Boolean> clearCache = ADB_HANDLER
            .rx_checkAppInstalled(CC_PARAM)
            .flatMap(a -> ADB_HANDLER.rx_clearCache(CC_PARAM));

        return Flowable
            .concat(super.rx_afterMethod(param), clearCache)
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
    public static final class Builder extends MobileEngine.Builder<AndroidEngine> {
        @NotNull private final AndroidInstance.Builder ANDROID_INSTANCE_BUILDER;

        Builder() {
            super(new AndroidEngine(), AndroidCapability.builder());
            ANDROID_INSTANCE_BUILDER = AndroidInstance.builder();
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

        @NotNull
        @Override
        public AndroidEngine build() {
            ENGINE.androidInstance = ANDROID_INSTANCE_BUILDER.build();
            return super.build();
        }
    }
    //endregion
}
