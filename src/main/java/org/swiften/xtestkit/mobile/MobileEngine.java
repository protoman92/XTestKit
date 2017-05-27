package org.swiften.xtestkit.mobile;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.capability.type.CapType;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.keyboard.MobileKeyboardActionType;
import org.swiften.xtestkit.mobile.element.action.password.MobilePasswordActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.MobileSwipeType;
import org.swiften.xtestkit.mobile.element.action.tap.MobileTapType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

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
    @NotNull String deviceName;
    @NotNull String platformVersion;
    boolean autoLaunch;

    public MobileEngine() {
        app = "";
        appPackage = "";
        appiumVersion = "1.6.4";
        deviceName = "";
        platformVersion = "";
        autoLaunch = true;
    }

    @NotNull
    @Override
    public String toString() {
        return deviceName;
    }

    //region Getters
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
     * Return {@link #autoLaunch}.
     * @return {@link Boolean} value.
     * @see #autoLaunch
     */
    public boolean autoLaunch() {
        return autoLaunch;
    }

    /**
     * Get the {@link Automation} mode.
     * @return {@link Automation} instance.
     */
    @NotNull
    public abstract Automation automation();
    //endregion

    //region Test Setup
    /**
     * @param param {@link BeforeParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_beforeMethod(BeforeParam)
     * @see #rxLaunchApp()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxa_beforeMethod(@NotNull BeforeParam param) {
        final Flowable<Boolean> SOURCE = rxLaunchApp();
        return super.rxa_beforeMethod(param).flatMap(a -> SOURCE);
    }

    /**
     * @param param {@link AfterParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_afterMethod(AfterParam)
     * @see #rxResetApp()
     * @see #rx_stopDriver()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rx_afterMethod(@NotNull AfterParam param) {
        final Flowable<Boolean> QUIT_APP = rxResetApp();
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
     * @see #app()
     * @see #appiumVersion()
     * @see #automation()
     * @see #deviceName()
     * @see #platformName()
     * @see #platformVersion()
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        capabilities.put(MobileCapabilityType.APP, app());
        capabilities.put(MobileCapabilityType.APPIUM_VERSION, appiumVersion());
        capabilities.put(MobileCapabilityType.AUTOMATION_NAME, automation().value());
        capabilities.put(MobileCapabilityType.DEVICE_NAME, deviceName());
        capabilities.put(MobileCapabilityType.PLATFORM_NAME, platformName());
        capabilities.put(MobileCapabilityType.PLATFORM_VERSION, platformVersion());
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
         * Set the {@link #platformVersion} value.
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