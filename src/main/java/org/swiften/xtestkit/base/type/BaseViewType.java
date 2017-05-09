package org.swiften.xtestkit.base.type;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * This interface provides view-specific properties. Each {@link PlatformType}
 * should have a set of {@link BaseViewType} that can be used to search for
 * elements.
 */
public interface BaseViewType extends ClassContainerType {
    @NotNull
    BaseViewType ANY_VIEW = new BaseViewType() {
        @NotNull
        @Override
        public String className() {
            return "*";
        }
    };

    /**
     * Check whether the current {@link BaseViewType} could display a {@link String}
     * text.
     * @return A {@link Boolean} value.
     */
    default boolean hasText() {
        return false;
    }

    /**
     * Check whether the current {@link BaseViewType} is clickable. For e.g., Android's
     * Button and iOS's UIButton classes.
     * @return A {@link Boolean} value.
     */
    default boolean isClickable() {
        return false;
    }

    /**
     * Check whether the current {@link BaseViewType} is editable.
     * @return A {@link Boolean} value.
     */
    default boolean isEditable() {
        return false;
    }
}
