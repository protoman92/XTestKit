package com.swiften.xtestkit.engine.mobile.android;

import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.*;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.MobileEngine;
import com.swiften.xtestkit.engine.base.Platform;
import com.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
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
    AndroidDriver<AndroidElement>> {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull String appActivity;

    AndroidEngine() {
        super();
        ADB_HANDLER = ADBHandler.builder().build();
        appActivity = "";
    }

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
    //endregion

    //region Test Setup
    /**
     * @param param A {@link BeforeClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeClass(BeforeClassParam)
     * @see ADBHandler#rxDisableEmulatorAnimations()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
        Flowable<Boolean> source;

        switch (testMode()) {
            case EMULATOR:
                final ADBHandler HANDLER = adbHandler();

                StartEmulatorParam sParam = StartEmulatorParam
                    .builder()
                    .withDeviceName(deviceName())
                    .withRetryProtocol(param)
                    .build();

                source = HANDLER.rxStartEmulator(sParam)
                    /* Disable animations to avoid erratic behaviors */
                    .flatMap(a -> HANDLER.rxDisableEmulatorAnimations());

                break;

            default:
                source = Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
                break;
        }

        return Flowable
            .concat(source, super.rxBeforeClass(param))
            .toList().toFlowable().map(a -> true);
    }

    /**
     * @param param A {@link AfterClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterClass(AfterClassParam)
     * @see ADBHandler#rxStopEmulator(RetryProtocol)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        Flowable<Boolean> source;

        switch (testMode()) {
            case EMULATOR:
                source = adbHandler().rxStopEmulator(param);
                break;

            default:
                source = Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
                break;
        }

        return Flowable
            .concat(source, super.rxAfterClass(param))
            .toList().toFlowable().map(a -> true);
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
        capabilities.put(AndroidMobileCapabilityType.APP_PACKAGE, appPackage());
        capabilities.put(AndroidMobileCapabilityType.APP_ACTIVITY, appActivity());
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
     * keyboard is present with {@link ADBHandler#rxCheckKeyboardOpen()},
     * and then call
     * {@link #rxNavigateBack(NavigateBack)}.
     * @return A {@link Flowable} instance.
     * @see #rxNavigateBack(NavigateBack)
     * @see ADBHandler#rxCheckKeyboardOpen()
     */
    @NotNull
    public Flowable<Boolean> rxDismissKeyboard() {
        return adbHandler().rxCheckKeyboardOpen()
            .filter(isOpen -> isOpen)
            .flatMap(a -> rxNavigateBack())
            .defaultIfEmpty(true);
    }
    //endregion

    //region Builder
    public static final class Builder extends MobileEngine.Builder<AndroidEngine> {
        @NotNull
        @Override
        protected AndroidEngine createEngineInstance() {
            return new AndroidEngine();
        }

        /**
         * Set the {@link #ENGINE#appActivity} value. This value is used to
         * determine which Activity is started first.
         * @param appActivity A {@link String} value.
         * @return A {@link Builder} instance.
         */
        @NotNull
        public Builder withAppActivity(@NotNull String appActivity) {
            ENGINE.appActivity = appActivity;
            return this;
        }

        @NotNull
        @Override
        public AndroidEngine build() {
            withPlatform(Platform.ANDROID);
            withPlatformView(new AndroidView());
            return super.build();
        }
    }
    //endregion
}
