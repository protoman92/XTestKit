package org.swiften.xtestkit.mobile;

import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.base.capability.type.CapType;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileActionType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.element.action.keyboard.type.MobileKeyboardActionType;
import org.swiften.xtestkit.mobile.element.action.password.type.MobilePasswordActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.type.MobileSwipeType;
import org.swiften.xtestkit.mobile.element.action.tap.type.MobileTapType;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by haipham on 3/20/17.
 */
public abstract class MobileEngine<D extends MobileDriver> extends
    Engine<D> implements
    MobileActionType<D>,
    MobileKeyboardActionType<D>,
    MobilePasswordActionType<D>,
    MobileTapType<D>,
    MobileSwipeType<D>
{
    @NotNull String app;
    @NotNull String appPackage;
    @NotNull String appiumVersion;
    @NotNull String automationName;
    @NotNull String deviceName;
    @NotNull String platformVersion;
    boolean autoLaunch;

    /**
     * If this is true, call {@link #rx_startDriver(RetryType)} in
     * {@link #rx_beforeClass(BeforeClassParam)}. Correspondingly,
     * {@link #rx_stopDriver()} will be called in
     * {@link #rx_beforeMethod(BeforeParam)}.
     * Otherwise, {@link #rx_startDriver(RetryType)} is called in
     * {@link #rx_beforeMethod(BeforeParam)}, and {@link #rx_stopDriver()}
     * is called in {@link #rx_afterMethod(AfterParam)}.
     */
    protected boolean startDriverOnlyOnce;

    public MobileEngine() {
        app = "";
        appPackage = "";
        appiumVersion = "1.6.4";
        automationName = "";
        deviceName = "";
        platformVersion = "";
        autoLaunch = true;
        startDriverOnlyOnce = true;
    }

    @NotNull
    @Override
    public String toString() {
        return deviceName;
    }

    //region Getters
    /**
     * Return {@link #startDriverOnlyOnce}.
     * @return {@link Boolean} value.
     * @see #startDriverOnlyOnce
     */
    public boolean startDriverOnlyOnce() {
        return startDriverOnlyOnce;
    }

    /**
     * Return {@link #app}.
     * @return {@link String} value.
     * @see #app
     */
    @NotNull
    public String app() {
        return app;
    }

    /**
     * Return {@link #appiumVersion}.
     * @return {@link String} value.
     * @see #appiumVersion
     */
    @NotNull
    public String appiumVersion() {
        return appiumVersion;
    }

    /**
     * Return {@link #automationName}.
     * @return {@link String} value.
     * @see #automationName
     */
    @NotNull
    public String automationName() {
        return automationName;
    }

    /**
     * Return {@link #appPackage}.
     * @return {@link String} value.
     * @see #appPackage
     */
    @NotNull
    public String appPackage() {
        return appPackage;
    }

    /**
     * Return {@link #deviceName}. This can be stubbed out for custom
     * implementation.
     * @return {@link String} value.
     * @see #deviceName
     */
    @NotNull
    public String deviceName() {
        return deviceName;
    }

    /**
     * Return {@link #platformVersion}.
     * @return {@link String} value.
     * @see #platformVersion
     */
    @NotNull
    public String platformVersion() {
        return platformVersion;
    }

    /**
     * Get {@link Platform} instance.
     * @return {@link Platform} instance.
     * @see #platformName()
     * @see Platform#fromValue(String)
     */
    @NotNull
    @Override
    public Platform platform() {
        String name = platformName();
        return Platform.fromValue(name);
    }

    /**
     * Return {@link #autoLaunch}.
     * @return {@link Boolean} value.
     * @see #autoLaunch
     */
    public boolean autoLaunch() {
        return autoLaunch;
    }
    //endregion

    //region Test Setup
    /**
     * @param param {@link BeforeParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_beforeMethod(BeforeParam)
     * @see #startDriverOnlyOnce()
     * @see #rx_startDriver(RetryType)
     * @see #rxLaunchApp()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rx_beforeMethod(@NotNull BeforeParam param) {
        final Flowable<Boolean> SOURCE;

        if (startDriverOnlyOnce()) {
            SOURCE = rxLaunchApp();
        } else {
            SOURCE = rx_startDriver(param);
        }

        return super.rx_beforeMethod(param).flatMap(a -> SOURCE);
    }

    /**
     * @param param {@link AfterParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_afterMethod(AfterParam)
     * @see #startDriverOnlyOnce()
     * @see #rxResetApp()
     * @see #rx_stopDriver()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rx_afterMethod(@NotNull AfterParam param) {
        final Flowable<Boolean> QUIT_APP;

        if (startDriverOnlyOnce()) {
            QUIT_APP = rxResetApp();
        } else {
            QUIT_APP = rx_stopDriver();
        }

        return super.rx_afterMethod(param).flatMap(a -> QUIT_APP);
    }
    //endregion

    //region Appium Setup
    /**
     * @return {@link Map} of {@link String} and {@link Object}. Do not
     * set {@link MobileCapabilityType#FULL_RESET} to be true because we
     * want to start a device and keep it open until all test for one
     * {@link MobileEngine} has finished. If necessary, we can clear the
     * app's data and uninstall manually.
     * @see Engine#capabilities()
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
//        capabilities.put("autoLaunch", autoLaunch());
        return capabilities;
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link MobileEngine}.
     * @param <T> Generics parameter that extends {@link MobileEngine}.
     */
    public static class Builder<T extends MobileEngine> extends Engine.Builder<T> {
        protected Builder(@NotNull T engine, @NotNull CapType.Builder capBuilder) {
            super(engine, capBuilder);
        }

        /**
         * Set the {@link #appiumVersion} value.
         * @param version The Appium version that will run the test.
         * @return The current {@link Builder} instance.
         * @see #appiumVersion
         */
        @NotNull
        public Builder<T> withAppiumVersion(@NotNull String version) {
            ENGINE.appiumVersion = version;
            return this;
        }

        /**
         * Set the {@link #app} value.
         * @param app The app's file name.
         * @return The current {@link Builder} instance.
         * @see System#getProperty(String)
         * @see Paths#get(String, String...)
         * @see #withApp(Path)
         * @see #app
         */
        @NotNull
        public Builder<T> withApp(@NotNull String app) {
            String path = System.getProperty("user.dir");
            return withApp(Paths.get(path, app));
        }

        /**
         * Set the {@link #app} value.
         * @param path {@link Path} instance.
         * @return The current {@link Builder} instance.
         * @see #app
         */
        public Builder<T> withApp(@NotNull Path path) {
            ENGINE.app = path.toString();
            return this;
        }

        /**
         * Set the {@link #appPackage} value.
         * @param appPackage The app's package name.
         * @return The current {@link Builder} instance.
         * @see #appPackage
         */
        @NotNull
        public Builder<T> withAppPackage(@NotNull String appPackage) {
            ENGINE.appPackage = appPackage;
            return this;
        }

        /**
         * Set the {@link #automationName} value.
         * @param automationName The app's automation name. For e.g., Android
         *                       SDK 16 or less should specify Selendroid,
         *                       and Appium otherwise. In order to minimize
         *                       platform differences, we should use
         *                       {@link XPath} as much as possible for
         *                       locator operations.
         * @return The current {@link Builder} instance.
         * @see #automationName
         */
        @NotNull
        public Builder<T> withAutomationName(@NotNull String automationName) {
            ENGINE.automationName = automationName;
            return this;
        }

        /**
         * Same as above, but use {@link Automation} instance instead.
         * @param automation {@link Automation} instance.
         * @return The current {@link Builder} instance.
         * @see #automationName
         */
        @NotNull
        public Builder<T> withAutomation(@NotNull Automation automation) {
            return withAutomationName(automation.value());
        }

        /**
         * Set the {@link #deviceName} value.
         * @param deviceName The device name on which test will be executed.
         * @return The current {@link Builder} instance.
         * @see #deviceName
         */
        @NotNull
        public Builder<T> withDeviceName(@NotNull String deviceName) {
            ENGINE.deviceName = deviceName;
            return this;
        }

        /**
         * Set the {@link #platformVersion} value. Automatically detect
         * {@link #automationName} as well.
         * @param version {@link String} value.
         * @return The current {@link Builder} instance.
         * @see #platformVersion
         */
        @NotNull
        public Builder<T> withPlatformVersion(@NotNull String version) {
            ENGINE.platformVersion = version;
            return this;
        }

        /**
         * Set the {@link #startDriverOnlyOnce} value.
         * @param once {@link Boolean} value.
         * @return The current {@link Builder} instance.
         * @see #startDriverOnlyOnce
         */
        @NotNull
        public Builder<T> shouldStartDriverOnlyOnce(boolean once) {
            ENGINE.startDriverOnlyOnce = once;
            return this;
        }

        /**
         * Set the {@link #autoLaunch} value.
         * @param autoLaunch {@link Boolean} value.
         * @return The current {@link Builder} instance.
         * @see #autoLaunch
         */
        @NotNull
        public Builder<T> shouldAutoLaunch(boolean autoLaunch) {
            ENGINE.autoLaunch = autoLaunch;
            return this;
        }
    }
    //endregion
}