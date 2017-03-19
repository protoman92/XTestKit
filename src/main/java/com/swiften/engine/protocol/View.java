package com.swiften.engine.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/20/17.
 */
public interface View {
    /**
     * Get the {@link View} class name for XPath requests.
     * @return A {@link String} value.
     */
    @NotNull String className();

    /**
     * Check whether the current {@link View} could display a {@link String}
     * text.
     * @return A {@link Boolean} value.
     */
    boolean hasText();

    /**
     * Check whether the current {@link View} is clickable. For e.g., Android's
     * Button and iOS's UIButton classes.
     * @return A {@link Boolean} value.
     */
    boolean isClickable();

    /**
     * Check whether the current {@link View} is editable.
     * @return A {@link Boolean} value.
     */
    boolean isEditable();
}
