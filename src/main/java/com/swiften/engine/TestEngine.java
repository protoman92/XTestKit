package com.swiften.engine;

/**
 * Created by haipham on 3/19/17.
 */

import com.swiften.engine.param.NavigateBack;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A base class for platform-specific implementations. Each Different platform
 * should extend this class and provide its own wrappers for Appium methods.
 */
public abstract class TestEngine<T extends WebDriver> implements
    DeviceProtocol,
    EngineDelay,
    EngineError,
    DriverProtocol {
    @Nullable protected  T driver;

    @NotNull protected String app;
    @NotNull protected String appPackage;
    @NotNull protected String appiumVersion;
    @NotNull protected String automationName;
    @NotNull protected String browserName;
    @NotNull protected String deviceName;
    @NotNull protected String platformName;
    @NotNull protected String platformVersion;
    @NotNull protected String serverUrl;
    protected long deviceReadyTimeout;

    public TestEngine() {
        app = "";
        appPackage = "";
        appiumVersion = "1.6.3";
        automationName = "";
        browserName = "";
        deviceName = "";
        deviceReadyTimeout = 10000;
        platformName = "";
        platformVersion = "";
        serverUrl = "http://localhost:4723/wd/hub";
    }

    /**
     * Get a {@link List} of required capabilities. We convert all values to
     * {@link String} and check if all values are non-empty. If a value is
     * not a {@link String}, but is falsy (e.g. a zero value), we replace
     * it with an empty {@link String}. Subclasses of {@link TestEngine}
     * can append to this {@link List}.
     * @return A {@link List} of {@link String}
     */
    @NotNull
    protected List<String> requiredCapabilities() {
        List<String> required = Arrays.asList(
                app,
                appPackage,
                automationName,
                deviceName,
                deviceName,
                platformName,
                platformVersion,
                serverUrl
        );

        return new ArrayList<>(required);
    }

    /**
     * If this method returns false, we should throw an {@link Exception}.
     * @return A {@link Boolean} value.
     */
    protected boolean hasAllRequiredInformation() {
        List<String> required = requiredCapabilities();
        return required.stream().allMatch(String::isEmpty);
    }

    /**
     * Get a {@link Map} of capabilities to pass to Appium driver.
     * @return A {@link Map} instance.
     */
    @NotNull
    private Map<String,Object> capabilities() {
        Map<String,Object> capabilities = new HashMap<String,Object>();
        capabilities.put("app", app);
        capabilities.put("appPackage", appPackage);
        capabilities.put("appium-version", appiumVersion);
        capabilities.put("automationName", appiumVersion);
        capabilities.put("deviceName", deviceName);
        capabilities.put("deviceReadyTimeout", deviceReadyTimeout);
        capabilities.put("platformName", platformName);
        capabilities.put("platformVersion", platformVersion);
        return capabilities;
    }

    /**
     * Get a {@link DesiredCapabilities} instance from {@link #capabilities()}.
     * @return A {@link DesiredCapabilities} instance.
     */
    protected DesiredCapabilities desiredCapabilities() {
        Map<String,Object> capabilities = capabilities();
        return new DesiredCapabilities(capabilities);
    }

    //region DriverProtocol
    /**
     * Start the Appium driver. If {@link #hasAllRequiredInformation()}
     * returns false, throw an {@link Exception}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxStartDriver() {
        if (hasAllRequiredInformation()) {
            return Completable
                .fromAction(() -> driver = createDriverInstance())
                .toFlowable();
        }

        return Flowable.error(new Exception(INSUFFICIENT_SETTINGS));
    }

    /**
     * Quit the active Appium driver. If it is null, throw an {@link Exception}
     * instead.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxStopDriver() {
        if (Objects.nonNull(driver)) {
            return Completable.fromAction(() -> driver.quit()).toFlowable();
        }

        return Flowable.error(new Exception(DRIVER_UNAVAILABLE));
    }
    //endregion

    //region DeviceProtocol
    /**
     * Navigate backwards for certain number of times.
     * @param param A {@link NavigateBack} object.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxNavigateBack(@NotNull NavigateBack param) {
        final T DRIVER = driver;
        final int TIMES = param.times;
        final long DELAY = BACK_NAVIGATION_DELAY;

        if (Objects.nonNull(DRIVER)) {
            class Back {
                /**
                 * Loop the operation until a stopping point is reached.
                 * finished navigating back x times.
                 */
                @NotNull
                Completable back(final int ITERATION) {
                    if (ITERATION < TIMES) {
                        return Completable
                            .fromAction(() -> DRIVER.navigate().back())
                            .delay(DELAY, TimeUnit.MILLISECONDS)
                            .andThen(new Back().back(ITERATION + 1));
                    }

                    return Completable.complete();
                }
            }

            return new Back().back(0).toFlowable().map(a -> true);
        }

        return Flowable.error(new Error(DRIVER_UNAVAILABLE));
    }
    //endregion

    @NotNull
    public XPath.Builder newXPathBuilderInstance() {
        Optional<Platform> platform = Platform.fromValue(platformName);

        if (platform.isPresent()) {
            return XPath.newBuilder(platform.get());
        }

        throw new RuntimeException(new Error(PLATFORM_UNAVAILABLE));
    }

    @NotNull
    public Flowable<Object> rxElementsWithXPath() {
        if (Objects.nonNull(driver)) {
        }

        return Flowable.error(new Error(DRIVER_UNAVAILABLE));
    }

    protected abstract T createDriverInstance();

    public static abstract class Builder<T extends TestEngine> {
        @NotNull final protected T ENGINE;

        protected Builder() {
            ENGINE = createEngineInstance();
        }

        /**
         * Set the {@link #ENGINE#appiumVersion} value.
         * @param version The Appium version that will run the tests.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withAppiumVersion(@NotNull String version) {
            ENGINE.appiumVersion = version;
            return this;
        }

        /**
         * Set the {@link #ENGINE#app} value. We assume that the app is
         * placed in {currentProject}/app folder.
         * @param app The app's file name.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withApp(@NotNull String app) {
            String path = System.getProperty("user.dir");
            ENGINE.app = String.format("%s/%s", path, app);
            return this;
        }

        /**
         * Set the {@link #ENGINE#appPackage} value.
         * @param appPackage The app's package name.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withAppPackage(@NotNull String appPackage) {
            ENGINE.appPackage = appPackage;
            return this;
        }

        /**
         * Set the {@link #ENGINE#automationName} value.
         * @param automationName The app's automation name. For e.g., Android
         *                       SDK 16 or less should specify Selendroid,
         *                       and Appium otherwise. In order to minimize
         *                       platform differences, we should use
         *                       {@link XPath} as much as possible for
         *                       locator operations.
         * @return The current {@link Builder} instance.
         */
        public Builder<T> withAutomationName(@NotNull String automationName) {
            ENGINE.automationName = automationName;
            return this;
        }

        /**
         * Set the {@link #ENGINE#browserName} value.
         * @param browser The browser name for Web tests. Android/iOS tests
         *                should leave this blank.
         * @return The current {@link Builder} instance.
         */
        public Builder<T> withBrowserName(@NotNull String browser) {
            ENGINE.browserName = browser;
            return this;
        }

        /**
         * Set the {@link #ENGINE#deviceName} value.
         * @param name The name of the device on which tests will be run.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withDeviceName(@NotNull String name) {
            ENGINE.deviceName = name;
            return this;
        }

        /**
         * Set the {@link #ENGINE#deviceReadyTimeout} value. This value
         * specifies how long Appium should wait until the device is ready.
         * If the app takes to long to boot up, Appium will terminate the
         * running tests.
         * @param timeout A {@link Long} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withDeviceReadyTimeout(long timeout) {
            ENGINE.deviceReadyTimeout = timeout;
            return this;
        }

        /**
         * Set the {@link #ENGINE#platformName} value.
         * @param name The name of the platform for which tests are executed.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withPlatformName(@NotNull String name) {
            ENGINE.platformName = name;
            return this;
        }

        @NotNull
        public Builder<T> withPlatform(@NotNull Platform platform) {
            return withPlatformName(platform.value());
        }

        /**
         * Set the {@link #ENGINE#platformVersion} value.
         * @param version The platform version to be executed.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withPlatformVersion(@NotNull String version) {
            ENGINE.platformVersion = version;
            return this;
        }

        /**
         * Set the {@link #ENGINE#serverUrl}. This {@link String} represents
         * the Appium server address.
         * @param url The server url.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withServerUrl(@NotNull String url) {
            ENGINE.serverUrl = url;
            return this;
        }

        @NotNull
        public T build() {
            return ENGINE;
        }

        @NotNull
        protected abstract T createEngineInstance();
    }
}