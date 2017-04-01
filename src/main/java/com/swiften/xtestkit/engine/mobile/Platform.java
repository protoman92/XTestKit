package com.swiften.xtestkit.engine.mobile;

import com.swiften.xtestkit.engine.base.protocol.PlatformProtocol;
import io.appium.java_client.remote.MobilePlatform;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by haipham on 3/20/17.
 */
public enum Platform implements PlatformProtocol {
    ANDROID,
    IOS;

    @NotNull
    public static Optional<Platform> fromValue(@NotNull String value) {
        return Arrays.stream(values())
            .filter(a -> a.value().equalsIgnoreCase(value))
            .findFirst();
    }

    @NotNull
    @Override
    public String value() {
        switch (this) {
            case ANDROID:
                return MobilePlatform.ANDROID;

            case IOS:
                return MobilePlatform.IOS;

            default:
                return "";
        }
    }

    @NotNull
    @Override
    public String hintAttribute() {
        switch (this) {
            case ANDROID:
                return "hint";

            case IOS:
                return "placeholder";

            default:
                return "";
        }
    }
}
