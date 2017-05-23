package org.swiften.xtestkit.mobile.android;

import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.*;
import org.swiften.xtestkit.base.type.AppPackageType;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.mobile.Automation;
import org.swiften.xtestkit.mobile.MobileEngine;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.android.adb.ADBHandler;
import org.swiften.xtestkit.mobile.android.element.action.date.type.AndroidDateActionType;
import org.swiften.xtestkit.mobile.android.capability.AndroidCap;
import org.swiften.xtestkit.mobile.android.element.action.general.type.AndroidActionType;
import org.swiften.xtestkit.mobile.android.element.action.input.type.AndroidInputActionType;
import org.swiften.xtestkit.mobile.android.element.action.input.type.AndroidKeyboardActionType;
import org.swiften.xtestkit.mobile.android.element.action.password.type.AndroidPasswordActionType;
import org.swiften.xtestkit.mobile.android.param.ClearCacheParam;
import org.swiften.xtestkit.mobile.android.param.StartEmulatorParam;
import org.swiften.xtestkit.mobile.android.param.StopEmulatorParam;
import org.swiften.xtestkit.mobile.android.type.ADBHandlerContainerType;
import org.swiften.xtestkit.mobile.android.type.AndroidErrorType;
import org.swiften.xtestkit.mobile.android.type.AndroidInstanceContainerType;
import org.swiften.xtestkit.mobile.android.type.DeviceUIDType;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.system.network.NetworkHandler;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.swiften.javautilities.bool.BooleanUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.swiften.javautilities.object.ObjectUtil;

/**
 * Created by haipham on 3/22/17.
 */
public class AndroidEngine extends
    MobileEngine<AndroidDriver<AndroidElement>> implements
    ADBHandlerContainerType,
    AndroidActionType,
    AndroidInstanceContainerType,
    AndroidDateActionType,
    AndroidErrorType,
    AndroidInputActionType,
    AndroidKeyboardActionType,
    AndroidPasswordActionType<AndroidDriver<AndroidElement>>
{
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull String appActivity;
    @Nullable AndroidInstance androidInstance;

    AndroidEngine() {
        super();
        ADB_HANDLER = ADBHandler.builder().build();
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
     * @see Engine#rx_beforeClass(BeforeClassParam)
     * @see ADBHandler#rx_disableEmulatorAnimations(DeviceUIDType)
     * @see #startDriverOnlyOnce()
     * @see #rx_startDriver(RetryType)
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rx_beforeClass(@NotNull final BeforeClassParam PARAM) {
        final ADBHandler HANDLER = adbHandler();
        final AndroidInstance ANDROID_INSTANCE = androidInstance();
        final Flowable<Boolean> START_APP;
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
                SOURCE = RxUtil.error(NOT_AVAILABLE);
                break;
        }

        if (startDriverOnlyOnce()) {
            START_APP = rx_startDriver(PARAM);
        } else {
            START_APP = Flowable.just(true);
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
     * @see #startDriverOnlyOnce()
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
        final Flowable<Boolean> QUIT_APP;

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

        if (startDriverOnlyOnce()) {
            QUIT_APP = rx_stopDriver();
        } else {
            QUIT_APP = Flowable.just(true);
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
            super(new AndroidEngine(), AndroidCap.builder());
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

        /**
         * Override this method to set {@link #automationName} as well.
         * @param version {@link String} value.
         * @return The current {@link MobileEngine.Builder} instance.
         * @see MobileEngine.Builder#withPlatformVersion(String)
         * @see Automation#SELENDROID
         * @see Automation#APPIUM
         */
        @NotNull
        @Override
        public MobileEngine.Builder<AndroidEngine> withPlatformVersion(@NotNull String version) {
            if (version.compareToIgnoreCase("4.2") < 0) {
                withAutomation(Automation.SELENDROID);
            } else {
                withAutomation(Automation.APPIUM);
            }

            return super.withPlatformVersion(version);
        }

        @NotNull
        @Override
        public AndroidEngine build() {
            withPlatform(Platform.ANDROID);
            withPlatformView(new AndroidView());
            ENGINE.androidInstance = ANDROID_INSTANCE_BUILDER.build();
            return super.build();
        }
    }
    //endregion
}
