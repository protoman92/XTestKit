package org.swiften.xtestkit.base.element.locator.general.param;

/**
 * Created by haipham on 5/14/17.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.localizer.LocalizationFormat;
import org.swiften.xtestkit.base.BaseEngine;
import org.swiften.xtestkit.base.element.property.type.base.FormatStringType;
import org.swiften.xtestkit.base.element.property.type.base.StringType;
import org.swiften.xtestkit.base.type.RetryType;

/**
 * Parameter object for {@link BaseEngine#rxElementsContainingText(TextFormatParam)}.
 */
public class TextFormatParam implements FormatStringType, RetryType {
    /**
     * Get a {@link Builder} instance.
     * @return A {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private LocalizationFormat format;

    private boolean ignoreCase;
    private int retries;

    TextFormatParam() {
        format = LocalizationFormat.builder().build();
        ignoreCase = FormatStringType.super.ignoreCase();
        retries = RetryType.super.retries();
    }

    //region StringType
    @NotNull
    @Override
    public LocalizationFormat value() {
        return format;
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
     * Builder class for {@link TextFormatParam}.
     */
    public static final class Builder {
        @NotNull final TextFormatParam PARAM;

        Builder() {
            PARAM = new TextFormatParam();
        }

        /**
         * Set the {@link #format} value.
         * @param format The text to be used to query elements.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withLocalizationFormat(@NotNull LocalizationFormat format) {
            PARAM.format = format;
            return this;
        }

        /**
         * Set the {@link #retries} value.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set the {@link #ignoreCase} value.
         * @param ignore A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder shouldIgnoreCase(boolean ignore) {
            PARAM.ignoreCase = ignore;
            return this;
        }

        @NotNull
        public TextFormatParam build() {
            return PARAM;
        }
    }
    //endregion
}
