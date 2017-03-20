package com.swiften.engine.param;

import com.swiften.engine.PlatformEngine;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Parameter object for
 * {@link PlatformEngine#rxElementsWithHint(HintParam)}
 */
public final class HintParam {
    @NotNull
    public static HintParam.Builder newBuilder() {
        return new Builder();
    }

    @NotNull public String hint;

    HintParam() {
        hint = "";
    }

    public static final class Builder {
        @NotNull private final HintParam PARAM;

        Builder() {
            PARAM = new HintParam();
        }

        /**
         * Set the {@link #PARAM##hint} value.
         * @param hint The hint to be used to element query.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withHint(@NotNull String hint) {
            PARAM.hint = hint;
            return this;
        }

        @NotNull
        public HintParam build() {
            return PARAM;
        }
    }
}
