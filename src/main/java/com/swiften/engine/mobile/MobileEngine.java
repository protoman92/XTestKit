package com.swiften.engine.mobile;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.engine.base.XPath;
import com.swiften.engine.mobile.protocol.MobileEngineError;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;

/**
 * Created by haipham on 3/20/17.
 */
public abstract class MobileEngine<
    E extends WebElement,
    T extends MobileDriver<E>
    > extends PlatformEngine<T>
    implements MobileEngineError {
    @NotNull protected String app;
    @NotNull protected String appPackage;
    @NotNull protected String appiumVersion;
    @NotNull protected String automationName;
    @NotNull protected String deviceName;
    @NotNull protected String platformName;
    @NotNull protected String platformVersion;

    public MobileEngine() {
        app = "";
        appPackage = "";
        appiumVersion = "1.6.3";
        automationName = "";
        deviceName = "";
        platformName = "";
        platformVersion = "";
    }

    /**
     * @return A {@link List} of {@link String}.
     * @see PlatformEngine#requiredCapabilities().
     */
    @NotNull
    @Override
    protected List<String> requiredCapabilities() {
        List<String> required = super.requiredCapabilities();

        Collections.addAll(required,
            app,
            appPackage,
            automationName,
            deviceName,
            platformName,
            platformVersion);

        return required;
    }

    /**
     * @return A {@link Map} of {@link String} and {@link Object}.
     * @see PlatformEngine#capabilities()
     */
    @NotNull
    @Override
    protected Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        capabilities.put(MobileCapabilityType.APP, app);
        capabilities.put(MobileCapabilityType.APPIUM_VERSION, appiumVersion);
        capabilities.put(MobileCapabilityType.AUTOMATION_NAME, automationName);
        capabilities.put(MobileCapabilityType.DEVICE_NAME, deviceName);
        capabilities.put(MobileCapabilityType.PLATFORM_NAME, platformName);
        capabilities.put(MobileCapabilityType.PLATFORM_VERSION, platformVersion);
        return capabilities;
    }

    /**
     * @return A {@link XPath.Builder} instance.
     * @see MobileEngine#newXPathBuilderInstance()
     */
    @NotNull
    @Override
    protected XPath.Builder newXPathBuilderInstance() {
        Optional<Platform> platform = Platform.fromValue(platformName);

        if (platform.isPresent()) {
            return XPath.newBuilder(platform.get());
        }

        throw new RuntimeException(new Error(PLATFORM_UNAVAILABLE));
    }

    public static abstract class Builder<T extends MobileEngine> extends
        PlatformEngine.Builder<T> {
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
        @NotNull
        public Builder<T> withAutomationName(@NotNull String automationName) {
            ENGINE.automationName = automationName;
            return this;
        }

        /**
         * Same as above, but use a {@link Automation} instance instead.
         * @param automation An {@link Automation} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withAutomation(@NotNull Automation automation) {
            return withAutomationName(automation.value());
        }

        /**
         * Set the {@link #ENGINE#deviceName} value.
         * @param deviceName The device name on which tests will be executed.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withDeviceName(@NotNull String deviceName) {
            ENGINE.deviceName = deviceName;
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

        /**
         * Same as above, but use a {@link Platform} instance.
         * @param platform A {@link Platform} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withPlatform(@NotNull Platform platform) {
            return withPlatformName(platform.value());
        }
    }
}