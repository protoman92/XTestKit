package com.swiften.xtestkit.engine.base.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/20/17.
 */
public interface View {
    @NotNull
    View ANY_VIEW = new View() {
        @NotNull
        @Override
        public String className() {
            return "*";
        }
    };

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
    default boolean hasText() {
        return false;
    }

    /**
     * Check whether the current {@link View} is clickable. For e.g., Android's
     * Button and iOS's UIButton classes.
     * @return A {@link Boolean} value.
     */
    default boolean isClickable() {
        return false;
    }

    /**
     * Check whether the current {@link View} is editable.
     * @return A {@link Boolean} value.
     */
    default boolean isEditable() {
        return false;
    }
}
