package org.swiften.xtestkit.engine.mobile;

import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.engine.base.type.RetryType;
import org.swiften.xtestkit.engine.base.capability.TestCapabilityType;
import org.swiften.xtestkit.locator.xpath.XPath;
import org.swiften.xtestkit.engine.mobile.type.MobileErrorType;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;

import java.util.*;

/**
 * Created by haipham on 3/20/17.
 */
public abstract class MobileEngine<
    E extends WebElement,
    T extends MobileDriver<E>>
    extends PlatformEngine<T>
    implements MobileErrorType
{
    @NotNull String app;
    @NotNull String appPackage;
    @NotNull String appiumVersion;
    @NotNull String automationName;
    @NotNull String deviceName;
    @NotNull String platformVersion;

    /**
     * If this is true, call {@link #rxStartDriver(RetryType)} in
     * {@link #rxBeforeClass(BeforeClassParam)}. Correspondingly,
     * {@link #rxStopDriver()} will be called in
     * {@link #rxBeforeMethod(BeforeParam)}.
     * Otherwise, {@link #rxStartDriver(RetryType)} is called in
     * {@link #rxBeforeMethod(BeforeParam)}, and {@link #rxStopDriver()}
     * is called in {@link #rxAfterMethod(AfterParam)}.
     */
    protected boolean startDriverOnlyOnce;

    public MobileEngine() {
        app = "";
        appPackage = "";
        appiumVersion = "1.6.3";
        automationName = "";
        deviceName = "";
        platformVersion = "";
        startDriverOnlyOnce = true;
    }

    @NotNull
    @Override
    public String toString() {
        return deviceName;
    }

    //region Getters
    public boolean startDriverOnlyOnce() {
        return startDriverOnlyOnce;
    }

    /**
     * Return {@link #app}.
     * @return A {@link String} value.
     */
    @NotNull
    public String app() {
        return app;
    }

    /**
     * Return {@link #appiumVersion}.
     * @return A {@link String} value.
     */
    @NotNull
    public String appiumVersion() {
        return appiumVersion;
    }

    /**
     * Return {@link #automationName}.
     * @return A {@link String} value.
     */
    @NotNull
    public String automationName() {
        return automationName;
    }

    /**
     * Return {@link #appPackage}.
     * @return A {@link String} value.
     */
    @NotNull
    public String appPackage() {
        return appPackage;
    }

    /**
     * Return {@link #deviceName}. This can be stubbed out for custom
     * implementation.
     * @return A {@link String} value.
     */
    @NotNull
    public String deviceName() {
        return deviceName;
    }

    /**
     * Return {@link #platformVersion}.
     * @return A {@link String} value.
     */
    @NotNull
    public String platformVersion() {
        return platformVersion;
    }
    //endregion

    //region Test Setup
    /**
     * @param param A {@link BeforeParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeMethod(BeforeParam)
     * @see #startDriverOnlyOnce()
     * @see #rxStartDriver(RetryType)
     * @see #rxLaunchApp()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxBeforeMethod(@NotNull BeforeParam param) {
        Flowable<Boolean> source;

        if (startDriverOnlyOnce()) {
            source = rxLaunchApp();
        } else {
            source = rxStartDriver(param);
        }

        return Flowable
            .concat(super.rxBeforeMethod(param), source)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }

    /**
     * @param param A {@link AfterParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterMethod(AfterParam)
     * @see #startDriverOnlyOnce()
     * @see #rxResetApp()
     * @see #rxStopDriver()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxAfterMethod(@NotNull AfterParam param) {
        Flowable<Boolean> quitApp;

        if (startDriverOnlyOnce()) {
            quitApp = rxResetApp();
        } else {
            quitApp = rxStopDriver();
        }

        return Flowable
            .concat(super.rxAfterMethod(param), quitApp)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }
    //endregion

    //region Appium Setup
    /**
     * @return A {@link Map} of {@link String} and {@link Object}. Do not
     * set {@link MobileCapabilityType#FULL_RESET} to be true because we
     * want to start a device and keep it open until all test for one
     * {@link MobileEngine} has finished. If necessary, we can clear the
     * app's data and uninstall manually.
     * @see PlatformEngine#capabilities()
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        capabilities.put(MobileCapabilityType.APP, app());
        capabilities.put(MobileCapabilityType.APPIUM_VERSION, appiumVersion());
        capabilities.put(MobileCapabilityType.AUTOMATION_NAME, automationName());
        capabilities.put(MobileCapabilityType.DEVICE_NAME, deviceName());
        capabilities.put(MobileCapabilityType.PLATFORM_NAME, platformName());
        capabilities.put(MobileCapabilityType.PLATFORM_VERSION, platformVersion());

        /* If startDriverOnlyOnce is false, we should manually start and reset
         * the app */
        capabilities.put("autoLaunch", !startDriverOnlyOnce());
        return capabilities;
    }
    //endregion

    //region Driver Methods
    /**
     * Launch an app}.
     * @return A {@link Flowable} instance.
     * @see T#launchApp()
     */
    @NotNull
    public Flowable<Boolean> rxLaunchApp() {
        return Completable
            .fromAction(driver()::launchApp)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Reset an installed app.
     * @return A {@link Flowable} instance.
     * @see T#resetApp()
     */
    @NotNull
    public Flowable<Boolean> rxResetApp() {
        return Completable
            .fromAction(driver()::closeApp)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link MobileEngine}.
     * @param <T> Generics parameter that extends {@link MobileEngine}.
     */
    public static abstract class Builder<T extends MobileEngine> extends
        PlatformEngine.Builder<T>
    {
        protected Builder(@NotNull T engine,
                          @NotNull TestCapabilityType.Builder capBuilder) {
            super(engine, capBuilder);
        }

        /**
         * Set the {@link #ENGINE#appiumVersion} value.
         * @param version The Appium version that will run the test.
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
            ENGINE.app = String.format("%s/app/%s", path, app);
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
         * @param deviceName The device name on which test will be executed.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withDeviceName(@NotNull String deviceName) {
            ENGINE.deviceName = deviceName;
            return this;
        }

        /**
         * Set the {@link #ENGINE#platformVersion} value. Automatically
         * detect {@link #ENGINE#automationName} as well.
         * @param version A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withPlatformVersion(@NotNull String version) {
            ENGINE.platformVersion = version;

            if (version.compareToIgnoreCase("4.2") < 0) {
                withAutomation(Automation.SELENDROID);
            } else {
                withAutomation(Automation.APPIUM);
            }

            return this;
        }

        /**
         * Set the {@link #ENGINE#startDriverOnlyOnce} value.
         * @param once A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> shouldStartDriverOnlyOnce(boolean once) {
            ENGINE.startDriverOnlyOnce = once;
            return this;
        }
    }
    //endregion
}