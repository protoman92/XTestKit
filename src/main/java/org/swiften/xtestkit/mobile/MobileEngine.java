package org.swiften.xtestkit.mobile;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.capability.EngineCapabilityType;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.keyboard.MobileKeyboardActionType;
import org.swiften.xtestkit.mobile.element.action.password.MobilePasswordActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.MobileSwipeType;
import org.swiften.xtestkit.mobile.element.action.tap.MobileTapType;
import org.swiften.xtestkit.mobile.type.PlatformVersionProviderType;
import org.swiften.javautilities.protocol.RetryType;

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
    MobileSwipeType<D>,
    PlatformVersionProviderType
{
    @NotNull String app;
    @NotNull String appPackage;
    @NotNull String appiumVersion;
    @NotNull String deviceName;
    @NotNull String platformVersion;
    boolean autoLaunch;
    long commandTimeout;

    public MobileEngine() {
        app = "";
        appPackage = "";
        appiumVersion = "1.6.5";
        deviceName = "";
        platformVersion = "";
        autoLaunch = true;
        commandTimeout = 100000;
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
     * Get {@link #appiumVersion}.
     * @return {@link String} value.
     * @see #appiumVersion
     */
    @NotNull
    public String appiumVersion() {
        return appiumVersion;
    }

    /**
     * Get {@link #appPackage}.
     * @return {@link String} value.
     * @see #appPackage
     */
    @NotNull
    public String appPackage() {
        return appPackage;
    }

    /**
     * Get {@link #deviceName}.
     * @return {@link String} value.
     * @see #deviceName
     */
    @NotNull
    public String deviceName() {
        return deviceName;
    }

    /**
     * Get {@link #platformVersion}.
     * @return {@link String} value.
     * @see #platformVersion
     */
    @NotNull
    public String platformVersion() {
        return platformVersion;
    }

    /**
     * Get {@link #autoLaunch}.
     * @return {@link Boolean} value.
     * @see #autoLaunch
     */
    public boolean autoLaunch() {
        return autoLaunch;
    }

    /**
     * Get {@link #commandTimeout}.
     * @return {@link Long} value.
     * @see #commandTimeout
     */
    public long commandTimeout() {
        return commandTimeout;
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
     * Override this method to provide default implementation.
     * @param param {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_beforeMethod(RetryType)
     * @see #rxa_launchApp()
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxa_beforeMethod(@NotNull RetryType param) {
        final Flowable<Boolean> SOURCE = rxa_launchApp();
        return super.rxa_beforeMethod(param).flatMap(a -> SOURCE);
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_afterMethod(RetryType)
     * @see ObjectUtil#nonNull(Object)
     * @see #rxa_resetApp()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxa_afterMethod(@NotNull RetryType param) {
        return Flowable
            .concatArray(super.rxa_afterMethod(param), rxa_resetApp())
            .all(ObjectUtil::nonNull)
            .toFlowable();
    }
    //endregion

    //region Appium Setup
    /**
     * Override this method to provide default implementation.
     * @return {@link Map} of {@link String} and {@link Object}. Do not
     * set {@link MobileCapabilityType#FULL_RESET} to be true because we
     * want to start a device and keep it open until all tests for one
     * {@link MobileEngine} have finished. If necessary, we can clear the
     * app's data and uninstall manually.
     * @see Engine#capabilities()
     * @see #app()
     * @see #appiumVersion()
     * @see #automation()
     * @see #commandTimeout()
     * @see #deviceName()
     * @see #platformName()
     * @see #platformVersion()
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> caps = super.capabilities();
        caps.put(MobileCapabilityType.APP, app());
        caps.put(MobileCapabilityType.APPIUM_VERSION, appiumVersion());
        caps.put(MobileCapabilityType.AUTOMATION_NAME, automation().value());
        caps.put(MobileCapabilityType.DEVICE_NAME, deviceName());
        caps.put(MobileCapabilityType.PLATFORM_NAME, platformName());
        caps.put(MobileCapabilityType.PLATFORM_VERSION, platformVersion());
        caps.put(MobileCapabilityType.NEW_COMMAND_TIMEOUT, commandTimeout());
        caps.put("fastReset", true);
        return caps;
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link MobileEngine}.
     * @param <T> Generics parameter that extends {@link MobileEngine}.
     */
    public static class Builder<T extends MobileEngine> extends Engine.Builder<T> {
        protected Builder(@NotNull T engine, @NotNull EngineCapabilityType.Builder cb) {
            super(engine, cb);
        }

        /**
         * Set the {@link #appiumVersion} value.
         * @param version The Appium version that will run the test.
         * @return {@link Builder} instance.
         * @see #appiumVersion
         */
        @NotNull
        public Builder<T> withAppiumVersion(@NotNull String version) {
            ENGINE.appiumVersion = version;
            return this;
        }

        /**
         * Set {@link #app}.
         * @param app {@link String} value.
         * @return {@link Builder} instance.
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
         * Set {@link #app}.
         * @param path {@link Path} instance.
         * @return {@link Builder} instance.
         * @see #app
         */
        public Builder<T> withApp(@NotNull Path path) {
            ENGINE.app = path.toString();
            return this;
        }

        /**
         * Set {@link #appPackage}.
         * @param appPackage {@link String} value.
         * @return {@link Builder} instance.
         * @see #appPackage
         */
        @NotNull
        public Builder<T> withAppPackage(@NotNull String appPackage) {
            ENGINE.appPackage = appPackage;
            return this;
        }

        /**
         * Set {@link #commandTimeout}.
         * @param timeout {@link Long} value.
         * @return {@link Builder} instance.
         * @see #commandTimeout
         */
        @NotNull
        public Builder<T> withCommandTimeout(long timeout) {
            ENGINE.commandTimeout = timeout;
            return this;
        }

        /**
         * Set {@link #deviceName}.
         * @param deviceName {@link String} value.
         * @return {@link Builder} instance.
         * @see #deviceName
         */
        @NotNull
        public Builder<T> withDeviceName(@NotNull String deviceName) {
            ENGINE.deviceName = deviceName;
            return this;
        }

        /**
         * Set {@link #platformVersion}.
         * @param version {@link String} value.
         * @return {@link Builder} instance.
         * @see #platformVersion
         */
        @NotNull
        public Builder<T> withPlatformVersion(@NotNull String version) {
            ENGINE.platformVersion = version;
            return this;
        }

        /**
         * Set {@link #autoLaunch}.
         * @param autoLaunch {@link Boolean} value.
         * @return {@link Builder} instance.
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