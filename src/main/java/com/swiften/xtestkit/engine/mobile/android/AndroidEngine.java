package com.swiften.xtestkit.engine.mobile.android;

import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.*;
import com.swiften.xtestkit.engine.base.protocol.AppPackageProtocol;
import com.swiften.xtestkit.engine.mobile.MobileEngine;
import com.swiften.xtestkit.engine.base.Platform;
import com.swiften.xtestkit.engine.mobile.TestMode;
import com.swiften.xtestkit.engine.mobile.android.param.ClearCacheParam;
import com.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import com.swiften.xtestkit.engine.mobile.android.param.StopEmulatorParam;
import com.swiften.xtestkit.engine.mobile.android.protocol.AndroidErrorProtocol;
import com.swiften.xtestkit.engine.mobile.android.protocol.DeviceUIDProtocol;
import com.swiften.xtestkit.system.NetworkHandler;
import com.swiften.xtestkit.util.BooleanUtil;
import com.swiften.xtestkit.util.LogUtil;
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
    AndroidErrorProtocol {
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

    //region Distinctive
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

    //region TestListener
    @NotNull
    @Override
    public Flowable<Boolean> rxOnFreshStart() {
        /* We restart adb server at the start of all test to avoid problems
         * with inactive adb instances */
        return super.rxOnFreshStart().flatMap(a -> ADB_HANDLER.rxRestartAdb());
    }
    //endregion

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
     * @see ADBHandler#rxDisableEmulatorAnimations(DeviceUIDProtocol)
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxBeforeClass(@NotNull final BeforeClassParam PARAM) {
        Flowable<Boolean> source;

        switch (testMode()) {
            case EMULATOR:
                final ADBHandler HANDLER = adbHandler();
                final AndroidInstance ANDROID_INSTANCE = androidInstance();

                source = HANDLER.rxFindAvailablePort(PARAM)
                    .doOnNext(ANDROID_INSTANCE::setPort)
                    .map(a -> StartEmulatorParam.builder()
                        .withDeviceName(deviceName())
                        .withAndroidInstance(ANDROID_INSTANCE)
                        .withRetries(100)
                        .build())
                    .flatMap(SE_PARAM -> HANDLER
                        .rxStartEmulator(SE_PARAM)

                        /* Disable animations to avoid erratic behaviors */
                        .flatMap(a -> HANDLER
                            .rxDisableEmulatorAnimations(SE_PARAM)
                            /* This is not absolutely crucial, so even if
                             * there is an error, we proceed anyway */
                            .onErrorResumeNext(Flowable.just(true))));

                break;

            default:
                source = Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
                break;
        }

        return Flowable
            .concat(super.rxBeforeClass(PARAM), source)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }

    /**
     * @param param A {@link AfterClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterClass(AfterClassParam)
     * @see ADBHandler#rxStopEmulator(StopEmulatorParam)
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        Flowable<Boolean> source;
        AndroidInstance androidInstance = androidInstance();

        switch (testMode()) {
            case EMULATOR:
                String deviceUID = androidInstance.deviceUID();
                LogUtil.printf("Stopping %1$s for %2$s", deviceUID, this);

                StopEmulatorParam seParam = StopEmulatorParam
                    .builder()
                    .withRetryProtocol(param)
                    .withPortProtocol(androidInstance)
                    .build();

                final NetworkHandler HANDLER = networkHandler();
                int PORT = androidInstance.port();

                source = Flowable
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
                source = Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
                break;
        }

        return Flowable
            .concat(super.rxAfterClass(param), source)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }

    /**
     * @param param A {@link AfterParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterMethod(AfterParam)
     * @see ADBHandler#rxClearCachedData(AppPackageProtocol)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxAfterMethod(@NotNull AfterParam param) {
        ADBHandler adbHandler = adbHandler();

        ClearCacheParam ccParam = ClearCacheParam
            .builder()
            .withAppPackage(appPackage())
            .withDeviceUIDProtocol(androidInstance())
            .withRetryProtocol(param)
            .build();

        /* Clear cached data such as SharedPreferences */
        Flowable<Boolean> clearCache = adbHandler.rxClearCachedData(ccParam);

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
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        AndroidInstance androidInstance = androidInstance();
        capabilities.put(AndroidMobileCapabilityType.APP_PACKAGE, appPackage());
        capabilities.put(AndroidMobileCapabilityType.APP_ACTIVITY, appActivity());
//        capabilities.put(AndroidMobileCapabilityType.AVD, deviceName());

        /* androidInstance should have already called setPort(), during
         * initialization phase */
//        capabilities.put(AndroidMobileCapabilityType.ADB_PORT, androidInstance.port());
        return capabilities;
    }

    /**
     * @return An {@link AndroidDriver} instance.
     * @see PlatformEngine#createDriverInstance()
     */
    @NotNull
    @Override
    protected AndroidDriver<AndroidElement> createDriverInstance() {
        try {
            URL url = new URL(serverUri());
            DesiredCapabilities capabilities = desiredCapabilities();
            return new AndroidDriver<>(url, capabilities);
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
     * {@link ADBHandler#rxCheckKeyboardOpen(DeviceUIDProtocol)},
     * and then call
     * {@link #rxNavigateBack(NavigateBack)}.
     * @return A {@link Flowable} instance.
     * @see #rxNavigateBack(NavigateBack)
     * @see ADBHandler#rxCheckKeyboardOpen(DeviceUIDProtocol)
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
            super();
            ANDROID_INSTANCE_BUILDER = AndroidInstance.builder();
        }

        @NotNull
        @Override
        protected AndroidEngine createEngineInstance() {
            return new AndroidEngine();
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
        public MobileEngine.Builder<AndroidEngine> withTestMode(@NotNull TestMode mode) {
            ANDROID_INSTANCE_BUILDER.withTestMode(mode);
            return super.withTestMode(mode);
        }

        public Builder withAppActivity(@NotNull String appActivity) {
            ENGINE.appActivity = appActivity;
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
