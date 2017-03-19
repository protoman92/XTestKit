package com.swiften.engine;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by haipham on 3/19/17.
 */
public enum Platform {
    ANDROID,
    IOS,
    WEB;

    @NotNull
    public static Optional<Platform> fromValue(@NotNull String value) {
        return Arrays.stream(values())
            .filter(a -> a.value()
            .equalsIgnoreCase(value))
            .findFirst();
    }

    @NotNull
    public String value() {
        switch (this) {
            case ANDROID:
                return "Android";

            default:
                throw new RuntimeException();
        }
    }
}
