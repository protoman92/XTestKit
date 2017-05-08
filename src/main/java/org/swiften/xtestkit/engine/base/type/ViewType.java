package org.swiften.xtestkit.engine.base.type;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * This interface provides view-specific properties. Each {@link PlatformType}
 * should have a set of {@link ViewType} that can be used to search for
 * elements.
 */
public interface ViewType {
    @NotNull
    ViewType ANY_VIEW = new ViewType() {
        @NotNull
        @Override
        public String className() {
            return "*";
        }
    };

    /**
     * Get the {@link ViewType} class name for XPath requests.
     * @return A {@link String} value.
     */
    @NotNull String className();

    /**
     * Check whether the current {@link ViewType} could display a {@link String}
     * text.
     * @return A {@link Boolean} value.
     */
    default boolean hasText() {
        return false;
    }

    /**
     * Check whether the current {@link ViewType} is clickable. For e.g., Android's
     * Button and iOS's UIButton classes.
     * @return A {@link Boolean} value.
     */
    default boolean isClickable() {
        return false;
    }

    /**
     * Check whether the current {@link ViewType} is editable.
     * @return A {@link Boolean} value.
     */
    default boolean isEditable() {
        return false;
    }
}
