package org.swiften.xtestkit.base.element.locator;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.property.base.IgnoreCaseType;
import org.swiften.xtestkitcomponents.property.base.StringProviderType;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Parameter object for {@link Engine#rxe_withText(StringProviderType[])}.
 */
public class TextParam implements StringProviderType, RetryProviderType {
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
        ignoreCase = StringProviderType.super.ignoreCase();
        retries = Constants.DEFAULT_RETRIES;
    }

    @NotNull
    @Override
    public String toString() {
        return text;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link String} value.
     * @see StringProviderType#value()
     * @see #text
     */
    @NotNull
    @Override
    public String value() {
        return text;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Boolean} value.
     * @see StringProviderType#ignoreCase()
     * @see #ignoreCase
     */
    @Override
    public boolean ignoreCase() {
        return ignoreCase;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RetryProviderType#retries()
     * @see #retries
     */
    @Override
    public int retries() {
        return retries;
    }

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
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withText(@NotNull String text) {
            PARAM.text = text;
            return this;
        }

        /**
         * Set the {@link #retries} value.
         * @param retries {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set {@link #retries} value.
         * @param param {@link RetryProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withRetries(int)
         */
        @NotNull
        public Builder withRetryProvider(@NotNull RetryProviderType param) {
            return withRetries(param.retries());
        }

        /**
         * Set the {@link #ignoreCase} value.
         * @param ignore {@link Boolean} value.
         * @return {@link Builder} instance.
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
         * @param type {@link StringProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withText(String)
         * @see #shouldIgnoreCase(boolean)
         */
        @NotNull
        public Builder withStringProvider(@NotNull StringProviderType type) {
            return withText(type.value()).shouldIgnoreCase(type.ignoreCase());
        }

        @NotNull
        public TextParam build() {
            return PARAM;
        }
    }
    //endregion
}
