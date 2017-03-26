package com.swiften.testapplication;

import com.swiften.engine.mobile.TestMode;
import com.swiften.engine.mobile.android.AndroidEngine;
import com.swiften.kit.TestKit;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Created by haipham on 3/25/17.
 */
public class Config {
    private static final String APP_PACKAGE;
    private static final String ANDROID_PERMISSION_APP_PACKAGE;
    private static final String ANDROID_APP_NAME;
    private static final String ANDROID_APP_ACTIVITY;
    private static final String ANDROID_PERMISSION_ACTIVITY;

    static {
        APP_PACKAGE = "com.swiften.testapplication";
        ANDROID_APP_ACTIVITY = ".LoginActivity";
        ANDROID_APP_NAME = "app-debug.apk";
        ANDROID_PERMISSION_APP_PACKAGE = "com.android.packageinstaller";
        ANDROID_PERMISSION_ACTIVITY = ".permission.ui.GrantPermissionsActivity";
    }

    @NotNull
    public static TestKit testKit() {
        return TestKit.newBuilder()
            .addEngine(AndroidEngine.newBuilder()
                .withAppActivity(ANDROID_APP_ACTIVITY)
                .withApp(ANDROID_APP_NAME)
                .withAppPackage(APP_PACKAGE)
                .withDeviceName("Nexus_4_API_23")
                .withPlatformVersion("6.0")
                .withTestMode(TestMode.EMULATOR)
                .build())
            .addEngine(AndroidEngine.newBuilder()
                .withAppActivity(ANDROID_APP_ACTIVITY)
                .withApp(ANDROID_APP_NAME)
                .withAppPackage(APP_PACKAGE)
                .withDeviceName("Nexus_4_API_22")
                .withTestMode(TestMode.EMULATOR)
                .withPlatformVersion("5.1")
                .build())
            .addResourceBundle("Strings", Locale.US)
            .build();
    }
}
