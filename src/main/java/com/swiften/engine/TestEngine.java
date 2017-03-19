package com.swiften.engine;

/**
 * Created by haipham on 3/19/17.
 */

import io.appium.java_client.android.AndroidDriver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * A base class for platform-specific implementations. Each Different platform
 * should extend this class and provide its own wrappers for Appium methods.
 */
public class TestEngine<T extends WebDriver> {
    @NotNull String app;
    @NotNull String appPackage;
    @NotNull String appiumVersion;
    @NotNull String automationName;
    @NotNull String browserName;
    @NotNull String deviceName;
    @NotNull String platformName;
    @NotNull String platformVersion;
    @NotNull String serverUrl;
    long deviceReadyTimeout;

    public TestEngine() {
        app = "";
        appPackage = "";
        appiumVersion = "1.6.3";
        automationName = "";
        browserName = "";
        deviceName = "";
        platformName = "";
        platformVersion = "";
        serverUrl = "http://localhost:4723/wd/hub";
    }

    @NotNull
    public Map<String,String> capabilities() {
        Map<String,String> capabilities = new HashMap<String,String>();
        capabilities.put("app", app);
        capabilities.put("appPackage", appPackage);
        capabilities.put("appium-version", appiumVersion);
        capabilities.put("automationName", appiumVersion);
        capabilities.put("deviceName", deviceName);
        capabilities.put("platformName", platformName);
        capabilities.put("platformVersion", platformVersion);
        return capabilities;
    }

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