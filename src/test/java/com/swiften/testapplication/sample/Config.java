package com.swiften.testapplication.sample;

import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.mobile.TestMode;
import com.swiften.xtestkit.engine.mobile.android.AndroidEngine;
import com.swiften.xtestkit.engine.mobile.ios.IOSEngine;
import com.swiften.xtestkit.kit.TestKit;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by haipham on 3/25/17.
 */
public class Config {
    private static final String APP_PACKAGE;
    private static final String ANDROID_APP_NAME;
    private static final String ANDROID_APP_ACTIVITY;

    private static final String IOS_APP_NAME;

    public static final List<PlatformEngine> ENGINES;

    static {
        APP_PACKAGE = "com.swiften.testapplication";
        ANDROID_APP_ACTIVITY = ".LoginActivity";
        ANDROID_APP_NAME = "app-debug.apk";

        IOS_APP_NAME = "app-debug.app";

        ENGINES = new LinkedList<>();

        ENGINES.add(IOSEngine.newBuilder()
            .withDeviceUID("D10524D4-939E-46CA-BE40-AB21F8E745A8")
            .withApp(IOS_APP_NAME)
            .withAppPackage(APP_PACKAGE)
            .withDeviceName("iPhone 7")
            .withPlatformVersion("10.2")
            .withTestMode(TestMode.EMULATOR)
            .build());

        ENGINES.add(IOSEngine.newBuilder()
            .withDeviceUID("CF6E7ACD-F818-4145-A140-75CF1F229A8C")
            .withApp(IOS_APP_NAME)
            .withAppPackage(APP_PACKAGE)
            .withDeviceName("iPhone 7 Plus")
            .withPlatformVersion("10.2")
            .withTestMode(TestMode.EMULATOR)
            .build());

        ENGINES.add(AndroidEngine.newBuilder()
            .withAppActivity(ANDROID_APP_ACTIVITY)
            .withApp(ANDROID_APP_NAME)
            .withAppPackage(APP_PACKAGE)
            .withDeviceName("Nexus_4_API_23")
            .withPlatformVersion("6.0")
            .withTestMode(TestMode.EMULATOR)
            .build());

        ENGINES.add(AndroidEngine.newBuilder()
            .withAppActivity(ANDROID_APP_ACTIVITY)
            .withApp(ANDROID_APP_NAME)
            .withAppPackage(APP_PACKAGE)
            .withDeviceName("Nexus_4_API_22")
            .withTestMode(TestMode.EMULATOR)
            .withPlatformVersion("5.1")
            .build());
    }

    public static int runCount() {
        return ENGINES.size();
    }

    @NotNull
    public static TestKit testKit() {
        return TestKit.newBuilder()
            .withEngines(ENGINES)
            .addResourceBundle("Strings", Locale.US)
            .build();
    }
}
