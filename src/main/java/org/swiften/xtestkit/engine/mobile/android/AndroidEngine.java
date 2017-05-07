package org.swiften.xtestkit.engine.mobile.android;

import org.swiften.xtestkit.engine.base.*;
import org.swiften.xtestkit.engine.base.param.AlertParam;
import org.swiften.xtestkit.engine.base.param.NavigateBack;
import org.swiften.xtestkit.engine.base.type.AppPackageType;
import org.swiften.xtestkit.engine.base.type.RetriableType;
import org.swiften.xtestkit.engine.mobile.MobileEngine;
import org.swiften.xtestkit.engine.base.TestMode;
import org.swiften.xtestkit.engine.mobile.android.capability.AndroidCap;
import org.swiften.xtestkit.engine.mobile.android.param.ClearCacheParam;
import org.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import org.swiften.xtestkit.engine.mobile.android.param.StopEmulatorParam;
import org.swiften.xtestkit.engine.mobile.android.type.AndroidErrorType;
import org.swiften.xtestkit.engine.mobile.android.type.DeviceUIDType;
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
import org.swiften.javautilities.log.LogUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * Created by haipham on 3/22/17.
 */
public class AndroidEngine extends MobileEngine<
    AndroidElement,
    AndroidDriver<AndroidElement>> implements
    AndroidErrorType
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
     * we override {@link PlatformEngine#getComparisonObject()} to disable
     * this comparison filter.
     * @return An {@link Object} instance.
     * @see PlatformEngine#getComparisonObject()
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
        if (Objects.nonNull(androidInstance)) {
            return androidInstance;
        }

        throw new RuntimeException(ANDROID_INSTANCE_UNAVAILABLE);
    }
    //endregion

    //region Test Setup
    /**
     * @param PARAM A {@link BeforeClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeClass(BeforeClassParam)
     * @see ADBHandler#rxDisableEmulatorAnimations(DeviceUIDType)
     * @see #startDriverOnlyOnce()
     * @see #rxStartDriver(RetriableType)
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
                source = Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
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
     * @see PlatformEngine#rxAfterClass(AfterClassParam)
     * @see ADBHandler#rxStopEmulator(StopEmulatorParam)
     * @see #startDriverOnlyOnce()
     * @see #rxResetApp()
     * @see #rxStopDriver()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        final Flowable<Boolean> SOURCE;
        Flowable<Boolean> quitApp;
        AndroidInstance androidInstance = androidInstance();

        switch (testMode()) {
            case SIMULATED:
                String deviceUID = androidInstance.deviceUID();
                LogUtil.printf("Stopping %1$s for %2$s", deviceUID, this);

                StopEmulatorParam seParam = StopEmulatorParam
                    .builder()
                    .withRetryProtocol(param)
                    .withPortProtocol(androidInstance)
                    .build();

                final NetworkHandler HANDLER = networkHandler();
                int PORT = androidInstance.port();

                SOURCE = Flowable
                    .concatArray(
                        Completable
                            .fromAction(() -> HANDLER.markPortAsAvailable(PORT))
                            .toFlowable()
                            .map(a -> true)
                            .defaultIfEmpty(true),

                        adbHandler().rxStopEmulator(seParam)
                    )
                    .all(BooleanUtil::isTrue)
                    .toFlowable();

                break;

            default:
                SOURCE = Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
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
            .flatMap(a -> SOURCE);
    }

    /**
     * @param param A {@link AfterParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterMethod(AfterParam)
     * @see ADBHandler#rxClearCachedData(AppPackageType)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxAfterMethod(@NotNull AfterParam param) {
        final ADBHandler ADB_HANDLER = adbHandler();

        ClearCacheParam CC_PARAM = ClearCacheParam
            .builder()
            .withAppPackage(appPackage())
            .withDeviceUIDProtocol(androidInstance())
            .withRetryProtocol(param)
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
     * @see PlatformEngine#driver(String, DesiredCapabilities)
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
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxDismissAlert(@NotNull AlertParam param) {
        return Flowable.just(param.shouldAccept())
            .map(a -> a ? "permission_allow_button" : "permission_deny_button")
            .map(id -> String.format("com.android.packageinstaller:id/%s", id))
            .map(id -> driver().findElement(By.id(id)))
            .filter(Objects::nonNull)
            .switchIfEmpty(Flowable.error(new Exception(NO_SUCH_ELEMENT)))
            .flatMapCompletable(a -> Completable.fromAction(a::click))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Dismiss Keyboard
    /**
     * Dismiss the keyboard if it is open. We first need to check whether the
     * keyboard is present with
     * {@link ADBHandler#rxCheckKeyboardOpen(DeviceUIDType)},
     * and then call
     * {@link #rxNavigateBack(NavigateBack)}.
     * @return A {@link Flowable} instance.
     * @see #rxNavigateBack(NavigateBack)
     * @see ADBHandler#rxCheckKeyboardOpen(DeviceUIDType)
     */
    @NotNull
    public Flowable<Boolean> rxDismissKeyboard() {
        AndroidInstance instance = androidInstance();

        return adbHandler().rxCheckKeyboardOpen(instance)
            .filter(isOpen -> isOpen)
            .flatMap(a -> rxNavigateBackOnce())
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
        public PlatformEngine.Builder<AndroidEngine> withTestMode(@NotNull TestMode mode) {
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
