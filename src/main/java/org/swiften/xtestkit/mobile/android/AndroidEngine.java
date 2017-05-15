package org.swiften.xtestkit.mobile.android;

import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.*;
import org.swiften.xtestkit.base.param.AlertParam;
import org.swiften.xtestkit.base.type.AppPackageType;
import org.swiften.xtestkit.base.type.RepeatType;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.mobile.MobileEngine;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.android.adb.ADBHandler;
import org.swiften.xtestkit.mobile.android.element.action.date.type.AndroidDateActionType;
import org.swiften.xtestkit.mobile.android.capability.AndroidCap;
import org.swiften.xtestkit.mobile.android.element.action.keyboard.AndroidKeyboardActionType;
import org.swiften.xtestkit.mobile.android.element.action.password.type.AndroidPasswordActionType;
import org.swiften.xtestkit.mobile.android.element.property.type.AndroidElementPropertyType;
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
import org.swiften.xtestkit.system.NetworkHandler;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.swiften.javautilities.bool.BooleanUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.swiften.javautilities.object.ObjectUtil;

/**
 * Created by haipham on 3/22/17.
 */
public class AndroidEngine extends
    MobileEngine<AndroidElement, AndroidDriver<AndroidElement>> implements
    ADBHandlerContainerType,
    AndroidInstanceContainerType,
    AndroidDateActionType,
    AndroidElementPropertyType,
    AndroidErrorType,
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
     * we override {@link BaseEngine#getComparisonObject()} to disable
     * this comparison filter.
     * @return An {@link Object} instance.
     * @see BaseEngine#getComparisonObject()
     */
    @NotNull
    @Override
    public Object getComparisonObject() {
        return deviceName();
    }
    //endregion

//    //region TestListenerType
//    @NotNull
//    @Override
//    public Flowable<Boolean> rxOnFreshStart() {
//        /* We restart adb server at the start of all test to avoid problems
//         * with inactive adb instances */
//        return super.rxOnFreshStart().flatMap(a -> ADB_HANDLER.rxRestartAdb());
//    }
//    //endregion

    //region Getters
    /**
     * Return {@link #ADB_HANDLER}.
     * @return An {@link ADBHandler} instance.
     */
    @NotNull
    public ADBHandler adbHandler() {
        return ADB_HANDLER;
    }

    /**
     * Return {@link #appActivity}. This can be stubbed out for custom
     * implementation.
     * @return A {@link String} value.
     */
    @NotNull
    public String appActivity() {
        return appActivity;
    }

    /**
     * Return {@link #androidInstance}.
     * @return An {@link AndroidInstance} instance.
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
     * @param PARAM A {@link BeforeClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see BaseEngine#rxBeforeClass(BeforeClassParam)
     * @see ADBHandler#rxDisableEmulatorAnimations(DeviceUIDType)
     * @see #startDriverOnlyOnce()
     * @see #rxStartDriver(RetryType)
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxBeforeClass(@NotNull final BeforeClassParam PARAM) {
        final ADBHandler HANDLER = adbHandler();
        final AndroidInstance ANDROID_INSTANCE = androidInstance();
        final Flowable<Boolean> START_APP;
        Flowable<Boolean> source;

        switch (testMode()) {
            case SIMULATED:
                source = HANDLER.rxFindAvailablePort(PARAM)
                    .doOnNext(ANDROID_INSTANCE::setPort)
                    .map(a -> StartEmulatorParam.builder()
                        .withDeviceName(deviceName())
                        .withAndroidInstance(ANDROID_INSTANCE)
                        .withRetries(100)
                        .build())
                    .flatMap(HANDLER::rxStartEmulator);

                break;

            case ACTUAL:
                /* Assuming the device is already started up */
                source = Flowable.just(true);
                break;

            default:
                source = RxUtil.error(PLATFORM_UNAVAILABLE);
                break;
        }

        if (startDriverOnlyOnce()) {
            START_APP = rxStartDriver(PARAM);
        } else {
            START_APP = Flowable.just(true);
        }

        return Flowable
            .concat(super.rxBeforeClass(PARAM), source)
            .all(BooleanUtil::isTrue)
            .toFlowable()

            /* Disable animations to avoid erratic behaviors */
            .flatMap(a -> HANDLER
                .rxDisableEmulatorAnimations(ANDROID_INSTANCE)

                /* This is not absolutely crucial, so even if
                 * there is an error, we proceed anyway */
                .onErrorResumeNext(Flowable.just(true)))
            .flatMap(a -> START_APP);
    }

    /**
     * @param param A {@link AfterClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see BaseEngine#rxAfterClass(AfterClassParam)
     * @see ADBHandler#rxStopEmulator(StopEmulatorParam)
     * @see #startDriverOnlyOnce()
     * @see #rxResetApp()
     * @see #rxStopDriver()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        AndroidInstance androidInstance = androidInstance();
        final NetworkHandler HANDLER = networkHandler();
        final int PORT = androidInstance.port();
        final Flowable<Boolean> SOURCE;
        Flowable<Boolean> quitApp;

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
//                SOURCE = adbHandler().rxStopEmulator(seParam);
                break;

            default:
                SOURCE = RxUtil.error(PLATFORM_UNAVAILABLE);
                break;
        }

        if (startDriverOnlyOnce()) {
            quitApp = rxStopDriver();
        } else {
            quitApp = Flowable.just(true);
        }

        return Flowable
            .concat(super.rxAfterClass(param), quitApp)
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .flatMap(a -> SOURCE)
            .flatMapCompletable(a -> Completable.fromAction(
                () -> HANDLER.markPortAsAvailable(PORT)
            ))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * @param param A {@link AfterParam} instance.
     * @return A {@link Flowable} instance.
     * @see BaseEngine#rxAfterMethod(AfterParam)
     * @see ADBHandler#rxClearCachedData(AppPackageType)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxAfterMethod(@NotNull AfterParam param) {
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
            .rxCheckAppInstalled(CC_PARAM)
            .flatMap(a -> ADB_HANDLER.rxClearCachedData(CC_PARAM));

        return Flowable
            .concat(super.rxAfterMethod(param), clearCache)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }
    //endregion

    //region Appium Setup
    /**
     * @return A {@link Map} of capabilities.
     * @see MobileEngine#capabilities()
     * @see #appPackage()
     * @see #appActivity()
     * @see #deviceName()
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        capabilities.put(AndroidMobileCapabilityType.APP_PACKAGE, appPackage());
        capabilities.put(AndroidMobileCapabilityType.APP_ACTIVITY, appActivity());
        capabilities.put(AndroidMobileCapabilityType.AVD, deviceName());
        return capabilities;
    }

    /**
     * @return An {@link AndroidDriver} instance.
     * @see BaseEngine#driver(String, DesiredCapabilities)
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

    //region Device Methods
    /**
     * Since {@link WebDriver.TargetLocator#alert()} is not yet implemented
     * on {@link Platform#ANDROID}, we need a custom solution by using
     * {@link AndroidDriver#findElementById(String)}.
     * @param param An {@link AlertParam} instance.
     * @return A {@link Flowable} instance.
     * @see #driver()
     * @see BaseEngine#rxDismissAlert(AlertParam)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxDismissAlert(@NotNull AlertParam param) {
        return Flowable.just(param.shouldAccept())
            .map(a -> a ? "permission_allow_button" : "permission_deny_button")
            .map(id -> String.format("com.android.packageinstaller:id/%s", id))
            .map(id -> driver().findElement(By.id(id)))
            .filter(ObjectUtil::nonNull)
            .switchIfEmpty(RxUtil.error(NO_SUCH_ELEMENT))
            .flatMapCompletable(a -> Completable.fromAction(a::click))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Builder
    public static final class Builder extends MobileEngine.Builder<AndroidEngine> {
        @NotNull private final AndroidInstance.Builder ANDROID_INSTANCE_BUILDER;

        Builder() {
            super(new AndroidEngine(), AndroidCap.builder());
            ANDROID_INSTANCE_BUILDER = AndroidInstance.builder();
        }

        /**
         * Override to set {@link AndroidInstance#deviceName}.
         * @param name A {@link String} value.
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
         * @param mode A {@link TestMode} instance.
         * @return The current {@link Builder} instance.
         * @see MobileEngine.Builder#withTestMode(TestMode)
         */
        @NotNull
        @Override
        public BaseEngine.Builder<AndroidEngine> withTestMode(@NotNull TestMode mode) {
            ANDROID_INSTANCE_BUILDER.withTestMode(mode);
            return super.withTestMode(mode);
        }

        /**
         * Set {@link #appActivity}.
         * @param appActivity A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        public Builder withAppActivity(@NotNull String appActivity) {
            ENGINE.appActivity = appActivity;
            return this;
        }

        /**
         * Set the {@link AndroidInstance#uid} value.
         * @param uid A {@link String} value.
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
            withPlatform(Platform.ANDROID);
            withPlatformView(new AndroidView());
            ENGINE.androidInstance = ANDROID_INSTANCE_BUILDER.build();
            return super.build();
        }
    }
    //endregion
}
