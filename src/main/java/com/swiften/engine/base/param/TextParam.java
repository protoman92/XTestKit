package com.swiften.engine.base.param;

import com.swiften.engine.base.PlatformEngine;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Parameter object for {@link PlatformEngine#rxElementsWithText(TextParam)}.
 */
public final class TextParam {
    @NotNull
    public Builder newBuilder() {
        return new Builder();
    }

    @NotNull public String text;

    TextParam() {
        text = "";
    }

    public static final class Builder {
        @NotNull final TextParam PARAM;

        Builder() {
            PARAM = new TextParam();
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
        public TextParam build() {
            return PARAM;
        }
    }
}
