package org.swiften.xtestkit.base.element.choice;

/**
 * Created by haipham on 5/23/17.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.android.element.choice.AndroidChoiceSwipeSelectorType;
import org.swiften.xtestkit.model.ChoiceInputType;
import org.swiften.xtestkit.base.type.BaseErrorType;

/**
 * Concrete class that implements {@link ChoiceType}.
 * Use this with
 * {@link BaseChoiceSelectorType#rxa_selectChoice(ChoiceType)}.
 */
public class ChoiceParam implements ChoiceType, BaseErrorType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Nullable ChoiceMode mode;
    @Nullable ChoiceInputType input;
    @Nullable String selected;

    /**
     * @return {@link Engine} instance.
     * @see AndroidChoiceSwipeSelectorType#engine()
     * @see ObjectUtil#nonNull(Object)
     * @see #mode
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public ChoiceMode mode() {
        if (ObjectUtil.nonNull(mode)) {
            return mode;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * @return {@link ChoiceInputType} instance.
     * @see AndroidChoiceSwipeSelectorType#choiceInput()
     * @see ObjectUtil#nonNull(Object)
     * @see #input
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public ChoiceInputType input() {
        if (ObjectUtil.nonNull(input)) {
            return input;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * @return {@link String} value.
     * @see AndroidChoiceSwipeSelectorType#selectedChoice()
     * @see ObjectUtil#nonNull(Object)
     * @see #selected
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public String selectedChoice() {
        if (ObjectUtil.nonNull(selected)) {
            return selected;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    //region Builder
    /**
     * Builder class for {@link ChoiceParam}.
     */
    public static final class Builder {
        @NotNull final ChoiceParam PARAM;

        Builder() {
            PARAM = new ChoiceParam();
        }

        /**
         * Set the {@link #mode} instance.
         * @param mode {@link Engine} instance.
         * @return The current {@link Builder} instance.
         * @see #mode
         */
        @NotNull
        public Builder withMode(@NotNull ChoiceMode mode) {
            PARAM.mode = mode;
            return this;
        }

        /**
         * Set the {@link #input} instance.
         * @param input {@link ChoiceInputType} instance.
         * @return The current {@link Builder} instance.
         * @see #input
         */
        @NotNull
        public Builder withInput(@NotNull ChoiceInputType input) {
            PARAM.input = input;
            return this;
        }

        /**
         * Set the {@link #selected} value.
         * @param selected {@link String} value.
         * @return The current {@link Builder} instance.
         * @see #selected
         */
        @NotNull
        public Builder withSelectedChoice(@NotNull String selected) {
            PARAM.selected = selected;
            return this;
        }

        @NotNull
        public ChoiceParam build() {
            return PARAM;
        }
    }
    //endregion
}
