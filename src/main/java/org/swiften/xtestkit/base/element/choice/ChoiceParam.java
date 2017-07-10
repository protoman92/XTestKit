package org.swiften.xtestkit.base.element.choice;

/**
 * Created by haipham on 5/23/17.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.android.element.choice.AndroidChoiceMultiSwipeType;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;

/**
 * Concrete class that implements {@link ChoiceType}.
 * Use this with
 * {@link ChoiceSelectorType#rxa_selectChoice(ChoiceType)}.
 */
public class ChoiceParam implements ChoiceType, ErrorProviderType {
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
     * @see AndroidChoiceMultiSwipeType#choiceHelper()
     * @see ObjectUtil#requireNotNull(Object, String)
     * @see #mode
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public ChoiceMode mode() {
        ObjectUtil.requireNotNull(mode, NOT_AVAILABLE);
        return mode;
    }

    /**
     * @return {@link ChoiceInputType} instance.
     * @see AndroidChoiceMultiSwipeType#choiceInput()
     * @see ObjectUtil#requireNotNull(Object, String)
     * @see #input
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public ChoiceInputType input() {
        ObjectUtil.requireNotNull(input, NOT_AVAILABLE);
        return input;
    }

    /**
     * @return {@link String} value.
     * @see AndroidChoiceMultiSwipeType#selectedChoice()
     * @see ObjectUtil#nonNull(Object)
     * @see #selected
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public String selectedChoice() {
        ObjectUtil.requireNotNull(selected, NOT_AVAILABLE);
        return selected;
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
         * @return {@link Builder} instance.
         * @see #mode
         */
        @NotNull
        public Builder withMode(@NotNull ChoiceMode mode) {
            PARAM.mode = mode;
            return this;
        }

        /**
         * Set {@link #mode} to be {@link ChoiceMode#GENERAL}.
         * @return {@link Builder} instance.
         * @see ChoiceMode#GENERAL
         * @see #withMode(ChoiceMode)
         */
        @NotNull
        public Builder withGeneralMode() {
            return withMode(ChoiceMode.GENERAL);
        }

        /**
         * Set the {@link #input} instance.
         * @param input {@link ChoiceInputType} instance.
         * @return {@link Builder} instance.
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
         * @return {@link Builder} instance.
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
