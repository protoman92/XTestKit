package com.swiften.engine.mobile;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/20/17.
 */
public enum Automation {
    APPIUM,
    SELENDROID;

    @NotNull
    public String value() {
        switch (this) {
            case APPIUM:
                return "Appium";

            case SELENDROID:
                return "Selendroid";

            default:
                return "";
        }
    }
}
