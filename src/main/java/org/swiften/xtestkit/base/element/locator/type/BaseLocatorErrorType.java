package org.swiften.xtestkit.base.element.locator.type;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 5/8/17.
 */
public interface BaseLocatorErrorType {
    String NO_SUCH_ELEMENT = "No such element";

    @NotNull
    default String noElementsWithClass(@NotNull String cls) {
        return String.format("No elements with class: %s", cls);
    }

    @NotNull
    default String noElementsWithId(@NotNull String id) {
        return String.format("No elements with id: %s", id);
    }

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
