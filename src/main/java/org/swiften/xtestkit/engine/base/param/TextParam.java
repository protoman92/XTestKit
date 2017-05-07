package org.swiften.xtestkit.engine.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.engine.base.type.RetriableType;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Parameter object for {@link PlatformEngine#rxElementsWithText(TextParam)}.
 */
public class TextParam implements RetriableType {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String text = "";

    TextParam() {
        text = "";
    }

    @NotNull
    public String text() {
        return text;
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
