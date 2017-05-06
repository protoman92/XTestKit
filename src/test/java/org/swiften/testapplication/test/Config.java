package org.swiften.testapplication.test;

import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.engine.mobile.TestMode;
import org.swiften.xtestkit.engine.mobile.ios.IOSEngine;
import org.swiften.xtestkit.kit.TestKit;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by haipham on 3/25/17.
 */
public class Config {
    @NotNull private static final String APP_PACKAGE;
    @NotNull private static final String ANDROID_APP_NAME;
    @NotNull private static final String ANDROID_APP_ACTIVITY;
    @NotNull private static final String IOS_APP_NAME;
    @NotNull private static final List<PlatformEngine> ENGINES;
    @NotNull public static final TestKit TEST_KIT;

    static {
        APP_PACKAGE = "com.swiften.testapplication";
        ANDROID_APP_ACTIVITY = ".LoginActivity";
        ANDROID_APP_NAME = "app-debug.apk";
        IOS_APP_NAME = "app-debug.app";

        ENGINES = new LinkedList<>();

        ENGINES.add(IOSEngine.builder()
            .withDeviceUID("D10524D4-939E-46CA-BE40-AB21F8E745A8")
            .withApp(IOS_APP_NAME)
            .withAppPackage(APP_PACKAGE)
            .withDeviceName("iPhone 7")
            .withPlatformVersion("10.2")
            .withTestMode(TestMode.EMULATOR)
            .build());

        ENGINES.add(IOSEngine.builder()
            .withDeviceUID("CF6E7ACD-F818-4145-A140-75CF1F229A8C")
            .withApp(IOS_APP_NAME)
            .withAppPackage(APP_PACKAGE)
            .withDeviceName("iPhone 7 Plus")
            .withPlatformVersion("10.2")
            .withTestMode(TestMode.EMULATOR)
            .build());

//        ENGINES.add(AndroidEngine.builder()
//            .withAppActivity(ANDROID_APP_ACTIVITY)
//            .withApp(ANDROID_APP_NAME)
//            .withAppPackage(APP_PACKAGE)
//            .withDeviceName("Nexus_4_API_23")
//            .withPlatformVersion("6.0")
//            .withTestMode(TestMode.EMULATOR)
//            .build());
//
//        ENGINES.add(AndroidEngine.builder()
//            .withAppActivity(ANDROID_APP_ACTIVITY)
//            .withApp(ANDROID_APP_NAME)
//            .withAppPackage(APP_PACKAGE)
//            .withDeviceName("Nexus_4_API_22")
//            .withTestMode(TestMode.EMULATOR)
//            .withPlatformVersion("5.1")
//            .build());

        TEST_KIT = TestKit.builder()
            .withEngines(ENGINES)
            .addResourceBundle("Strings", Locale.US)
            .build();
    }

    public static int runCount() {
        return ENGINES.size();
    }
}
