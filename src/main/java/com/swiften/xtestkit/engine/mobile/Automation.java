package com.swiften.xtestkit.engine.mobile;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/20/17.
 */
public enum Automation {
    APPIUM,
    SELENDROID,
    XC_UI_TEST;

    @NotNull
    public String value() {
        switch (this) {
            case APPIUM:
                return "Appium";

            case SELENDROID:
                return "Selendroid";

            case XC_UI_TEST:
                return "XCUITest";

            default:
                return "";
        }
    }
}
