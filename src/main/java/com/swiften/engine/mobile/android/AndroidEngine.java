package com.swiften.engine.mobile.android;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.engine.mobile.MobileEngine;
import com.swiften.engine.mobile.android.protocol.AndroidEngineError;
import com.swiften.engine.mobile.protocol.MobileEngineError;
import com.swiften.util.Log;
import com.swiften.util.ProcessUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * Created by haipham on 3/22/17.
 */
public final class AndroidEngine extends MobileEngine<
    AndroidElement,
    AndroidDriver<AndroidElement>
    >
    implements AndroidEngineError {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull String appActivity;

    AndroidEngine() {
        super();
        appActivity = "";
    }

    /**
     * @return A {@link Map} of capabilities.
     * @see MobileEngine#capabilities()
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        capabilities.put(AndroidMobileCapabilityType.APP_PACKAGE, appPackage);
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
            URL url = new URL(serverUrl);
            DesiredCapabilities capabilities = desiredCapabilities();
            return new AndroidDriver<>(url, capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get ${ANDROID_HOME} from Environment variables.
     * @return A {@link String} value.
     */
    @NotNull
    public String androidHome() {
        String androidHome = System.getenv("ANDROID_HOME");

        if (Objects.isNull(androidHome) || androidHome.isEmpty()) {
            throw new RuntimeException(ANDROID_HOME_NOT_SET);
        }

        return androidHome;
    }

    /**
     * Get path to adb.
     * @return A {@link String} value.
     */
    @NotNull
    public String adb() {
        return String.format("%s/platform-tools/adb", androidHome());
    }

    /**
     * Get path to adb shell CLI.
     * @return A {@link String} value.
     */
    @NotNull
    public String adbShell() {
        return String.format("%s shell", adb());
    }

    /**
     * Get path to Android emulator CLI.
     * @return A {@link String} value.
     */
    @NotNull
    public String emulator() {
        return String.format("%s/tools/emulator", androidHome());
    }

    @NotNull
    public Flowable<Boolean> rxStartApp() {
        String start = String.format("%1$s am start -m %2$s/%3$s",
            androidHome(),
            appPackage,
            appActivity);

        Log.println(start);

        return ProcessUtil.executeCommand(start);
    }

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
    }
}
