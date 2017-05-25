package org.swiften.xtestkit.base.element.locator.general.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.element.property.base.IgnoreCaseType;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.base.element.property.base.StringType;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Parameter object for {@link Engine#rxElementsWithText(StringType)}.
 */
public class TextParam implements StringType, RetryType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String text;

    private boolean ignoreCase;
    private int retries;

    TextParam() {
        text = "";
        ignoreCase = StringType.super.ignoreCase();
        retries = RetryType.super.retries();
    }

    //region StringType
    @NotNull
    @Override
    public String value() {
        return text;
    }

    @Override
    public boolean ignoreCase() {
        return ignoreCase;
    }
    //endregion

    //region RetryType
    @Override
    public int retries() {
        return retries;
    }
    //endregion

    //region Builder.
    /**
     * Builder class for {@link TextParam}.
     */
    public static final class Builder {
        @NotNull final TextParam PARAM;

        Builder() {
            PARAM = new TextParam();
        }

        /**
         * Set the {@link #text} value.
         * @param text The text to be used to query elements.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withText(@NotNull String text) {
            PARAM.text = text;
            return this;
        }

        /**
         * Set the {@link #retries} value.
         * @param retries {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set {@link #retries} value.
         * @param param {@link RetryType} instance.
         * @return The current {@link Builder} instance.
         * @see #withRetries(int)
         */
        @NotNull
        public Builder withRetryType(@NotNull RetryType param) {
            return withRetries(param.retries());
        }

        /**
         * Set the {@link #ignoreCase} value.
         * @param ignore {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder shouldIgnoreCase(boolean ignore) {
            PARAM.ignoreCase = ignore;
            return this;
        }

        /**
         * Set the {@link #ignoreCase} value.
         * @param type {@link IgnoreCaseType} instance.
         * @return {@link Builder} instance.
         * @see #shouldIgnoreCase(boolean)
         */
        @NotNull
        public Builder shouldIgnoreCase(@NotNull IgnoreCaseType type) {
            return shouldIgnoreCase(type.ignoreCase());
        }

        /**
         * Set {@link #text} and {@link #ignoreCase}.
         * @param type {@link StringType} instance.
         * @return The current {@link Builder} instance.
         * @see #withText(String)
         * @see #shouldIgnoreCase(boolean)
         */
        @NotNull
        public Builder withStringType(@NotNull StringType type) {
            return withText(type.value()).shouldIgnoreCase(type.ignoreCase());
        }

        @NotNull
        public TextParam build() {
            return PARAM;
        }
    }
    //endregion
}
