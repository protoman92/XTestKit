package org.swiften.xtestkit.engine.base.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.capability.TestCapabilityErrorType;

/**
 * Created by haipham on 3/19/17.
 */
public interface PlatformErrorType extends TestCapabilityErrorType {
    String APPIUM_NOT_INSTALLED = "Appium not installed";
    String CAPABILITY_UNAVAILABLE = "Capability unavailable";
    String DRIVER_UNAVAILABLE = "Driver unavailable";
    String PLATFORM_VIEW_UNAVAILABLE = "Platform view unavailable";
    String TEXT_DELEGATE_UNAVAILABLE = "Text delegate unavailable";
    String INSUFFICIENT_SETTINGS = "Insufficient settings";
    String NO_EDITABLE_ELEMENTS = "No editable elements";
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
