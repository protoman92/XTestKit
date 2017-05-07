package org.swiften.xtestkit.engine.base;

import org.swiften.xtestkit.engine.base.xpath.Attribute;
import io.appium.java_client.remote.MobilePlatform;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by haipham on 3/20/17.
 */
public enum Platform implements PlatformType {
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
    public Attribute hintAttribute() {
        switch (this) {
            case ANDROID:
                return Attribute.withSingleAttribute("hint");

            case IOS:
                return Attribute.withSingleAttribute("placeholder");

            default:
                return Attribute.BLANK;
        }
    }

    @NotNull
    @Override
    public Attribute textAttribute() {
        switch (this) {
            case ANDROID:
                return Attribute.withSingleAttribute("text");

            case IOS:
                return Attribute.builder()
                    .addAttribute("title")
                    .addAttribute("text")
                    .addAttribute("value")
                    .addAttribute("name")
                    .withMode(Attribute.Mode.OR)
                    .build();

            default:
                return Attribute.BLANK;
        }
    }
}
