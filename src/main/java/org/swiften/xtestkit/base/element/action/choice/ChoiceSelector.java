package org.swiften.xtestkit.base.element.action.choice;

/**
 * Created by haipham on 5/23/17.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.element.action.choice.type.ChoiceSelectorSwipeType;
import org.swiften.xtestkit.base.element.action.input.type.ChoiceInputType;
import org.swiften.xtestkit.base.type.BaseErrorType;

/**
 * Concrete class that implements {@link ChoiceSelectorSwipeType}.
 * Use this to select items from a choice list view.
 */
public class ChoiceSelector implements ChoiceSelectorSwipeType, BaseErrorType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Nullable Engine<?> engine;
    @Nullable ChoiceInputType input;
    @Nullable String selected;

    /**
     * @return {@link Engine} instance.
     * @see ChoiceSelectorSwipeType#engine()
     * @see ObjectUtil#nonNull(Object)
     * @see #engine
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public Engine<?> engine() {
        if (ObjectUtil.nonNull(engine)) {
            return engine;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * @return {@link ChoiceInputType} instance.
     * @see ChoiceSelectorSwipeType#choiceInput()
     * @see ObjectUtil#nonNull(Object)
     * @see #input
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public ChoiceInputType choiceInput() {
        if (ObjectUtil.nonNull(input)) {
            return input;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * @return {@link String} value.
     * @see ChoiceSelectorSwipeType#selectedChoice()
     * @see ObjectUtil#nonNull(Object)
     * @see #selected
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public String selectedChoice() {
        if (ObjectUtil.nonNull(selected)) {
            return selected;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Builder class for {@link ChoiceSelector}.
     */
    public static final class Builder {
        @NotNull final ChoiceSelector SELECTOR;

        Builder() {
            SELECTOR = new ChoiceSelector();
        }

        /**
         * Set the {@link #engine} instance.
         * @param engine {@link Engine} instance.
         * @return The current {@link Builder} instance.
         * @see #engine
         */
        @NotNull
        public Builder withEngine(@NotNull Engine<?> engine) {
            SELECTOR.engine = engine;
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
            SELECTOR.input = input;
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
            SELECTOR.selected = selected;
            return this;
        }

        @NotNull
        public ChoiceSelector build() {
            return SELECTOR;
        }
    }
}
