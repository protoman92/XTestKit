package org.swiften.xtestkit.engine.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.BaseEngine;
import org.swiften.xtestkit.engine.base.type.RetryType;
import org.swiften.xtestkit.engine.base.locator.element.type.base.StringType;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Parameter object for {@link BaseEngine#rxElementsWithText(TextParam)}.
 */
public class TextParam implements StringType, RetryType {
    /**
     * Get a {@link Builder} instance.
     * @return A {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String text;

    private boolean ignoreCase;

    TextParam() {
        text = "";
        ignoreCase = StringType.super.ignoreCase();
    }

    //region StringType.
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

    /**
     * Create a new {@link TextParam} that replicates all the current
     * {@link TextParam} properties, except for the text. This is useful
     * when we want to localize the text.
     * @param text A {@link String} value.
     * @return A new {@link TextParam} instance.
     */
    @NotNull
    public TextParam withNewText(@NotNull String text) {
        return builder().withText(text).shouldIgnoreCase(ignoreCase()).build();
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
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withText(@NotNull String text) {
            PARAM.text = text;
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
        public TextParam build() {
            return PARAM;
        }
    }
    //endregion
}
