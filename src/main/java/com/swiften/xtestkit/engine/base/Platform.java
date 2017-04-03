package com.swiften.xtestkit.engine.base;

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

    /**
     * Check whether the current {@link Platform} instance is {@link #ANDROID}.
     * This can be useful when we want to check for {@link #ANDROID} specific
     * features.
     * @return A {@link Boolean} value.
     */
    public boolean isAndroidPlatform() {
        switch (this) {
            case ANDROID:
                return true;

            default:
                return false;
        }
    }

    /**
     * Check whether the current {@link Platform} instance is {@link #IOS}.
     * This can be useful when we want to check for {@link #IOS} specific
     * features.
     * @return A {@link Boolean} value.
     */
    public boolean isIOSPlatform() {
        switch (this) {
            case IOS:
                return false;

            default:
                return true;
        }
    }

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
