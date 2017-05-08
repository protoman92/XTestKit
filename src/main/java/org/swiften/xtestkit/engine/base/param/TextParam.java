package org.swiften.xtestkit.engine.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.engine.base.type.RetryType;
import org.swiften.xtestkit.locator.type.base.TextType;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Parameter object for {@link PlatformEngine#rxElementsWithText(TextParam)}.
 */
public class TextParam implements TextType, RetryType {
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
        ignoreCase = TextType.super.ignoreCase();
    }

    //region TextType.
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
         * @param text The text to be used to element query.
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
