package com.swiften.engine.base.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/19/17.
 */
public interface EngineError {
    String DRIVER_UNAVAILABLE = "Driver unavailable";
    String PLATFORM_VIEW_UNAVAILABLE = "Platform view unavailable";
    String INSUFFICIENT_SETTINGS = "Insufficient settings";

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
    default String noElementContainingHint(@NotNull String hint) {
        return String.format("No elements containing hint: %s", hint);
    }
}
