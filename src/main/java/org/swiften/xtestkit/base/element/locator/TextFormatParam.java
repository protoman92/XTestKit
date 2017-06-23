package org.swiften.xtestkit.base.element.locator;

/**
 * Created by haipham on 5/14/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.localizer.LCFormat;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.property.base.FormatProviderType;
import org.swiften.xtestkitcomponents.property.base.StringProviderType;

/**
 * Parameter object for {@link Engine#rxe_containsText(FormatProviderType[])}.
 */
public class TextFormatParam implements FormatProviderType, RetryProviderType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private LCFormat format;

    private boolean ignoreCase;
    private int retries;

    TextFormatParam() {
        format = LCFormat.builder().build();
        ignoreCase = FormatProviderType.super.ignoreCase();
        retries = Constants.DEFAULT_RETRIES;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link LCFormat} instance.
     * @see FormatProviderType#value()
     * @see #format
     */
    @NotNull
    @Override
    public LCFormat value() {
        return format;
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
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withLCFormat(@NotNull LCFormat format) {
            PARAM.format = format;
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
         * Set the {@link #ignoreCase} value.
         * @param ignore {@link Boolean} value.
         * @return {@link Builder} instance.
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
