package org.swiften.xtestkit.base.element.locator;

/**
 * Created by haipham on 5/14/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.localizer.LCFormat;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.property.base.FormatType;
import org.swiften.javautilities.protocol.RetryType;

/**
 * Parameter object for {@link Engine#rxe_containsText(FormatType[])}.
 */
public class TextFormatParam implements FormatType, RetryType {
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
        ignoreCase = FormatType.super.ignoreCase();
        retries = Constants.DEFAULT_RETRIES;
    }

    //region StringType
    @NotNull
    @Override
    public LCFormat value() {
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
