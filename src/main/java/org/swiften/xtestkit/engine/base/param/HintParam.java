package org.swiften.xtestkit.engine.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.engine.base.type.RetryType;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Parameter object for {@link PlatformEngine#rxElementsWithHint(HintParam)}
 */
public class HintParam implements RetryType {
    /**
     * Get a {@link Builder} instance.
     * @return A {@link Builder} instance.
     */
    @NotNull
    public static HintParam.Builder builder() {
        return new Builder();
    }

    @NotNull private String hint;

    HintParam() {
        hint = "";
    }

    @NotNull
    public String hint() {
        return hint;
    }

    //region Builder.
    /**
     * Builder class for {@link HintParam}.
     */
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
    //endregion
}
