package com.swiften.xtestkit.engine.base;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/19/17.
 */
public interface ErrorProtocol {
    String APPIUM_NOT_INSTALLED = "Appium not installed";
    String DRIVER_UNAVAILABLE = "Driver unavailable";
    String PLATFORM_VIEW_UNAVAILABLE = "Platform view unavailable";
    String PLATFORM_UNAVAILABLE = "Platform unavailable";
    String TEXT_DELEGATE_UNAVAILABLE = "Text delegate unavailable";
    String INSUFFICIENT_SETTINGS = "Insufficient settings";
    String NO_SUCH_ELEMENT = "No such element";

    @NotNull
    default String noElementsWithText(@NotNull String text) {
        return String.format("No elements with text: %s", text);
    }

    @NotNull
    default String noElementsContainingText(@NotNull String text) {
        return String.format("No elements containing text: %s", text);
    }

    @NotNull
    default String noElementsWithHint(@NotNull String hint) {
        return String.format("No elements with hint: %s", hint);
    }

    @NotNull
    default String noElementsContainingHint(@NotNull String hint) {
        return String.format("No elements containing hint: %s", hint);
    }
}
