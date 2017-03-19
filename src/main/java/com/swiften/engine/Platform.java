package com.swiften.engine;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/19/17.
 */
public enum Platform {
    ANDROID,
    IOS,
    WEB;

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
