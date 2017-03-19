package com.swiften.engine.param;

import com.swiften.engine.TestEngine;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Parameter object for {@link TestEngine#rxElementsWithText()}.
 */
public final class HasText {
    @NotNull
    public Builder newBuilder() {
        return new Builder();
    }

    @NotNull public String text;

    HasText() {
        text = "";
    }

    public static final class Builder {
        @NotNull final HasText PARAM;

        Builder() {
            PARAM = new HasText();
        }

        /**
         * Set the {@link #PARAM##text} value.
         * @param text The text to be used to element query.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withText(@NotNull String text) {
            PARAM.text = text;
            return this;
        }

        @NotNull
        public HasText build() {
            return PARAM;
        }
    }
}
