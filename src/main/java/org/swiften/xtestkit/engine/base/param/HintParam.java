package org.swiften.xtestkit.engine.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.BaseEngine;
import org.swiften.xtestkit.engine.base.type.RetryType;
import org.swiften.xtestkit.engine.base.locator.element.type.base.StringType;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Parameter object for {@link BaseEngine#rxElementsWithHint(HintParam)}
 */
public class HintParam implements StringType, RetryType {
    /**
     * Get a {@link Builder} instance.
     * @return A {@link Builder} instance.
     */
    @NotNull
    public static HintParam.Builder builder() {
        return new Builder();
    }

    @NotNull private String hint;

    private boolean ignoreCase;

    HintParam() {
        hint = "";
        ignoreCase = StringType.super.ignoreCase();
    }

    //region StringType
    @NotNull
    @Override
    public String value() {
        return hint;
    }

    @Override
    public boolean ignoreCase() {
        return ignoreCase;
    }
    //endregion

    /**
     * Create a new {@link HintParam} that replicates all the current
     * {@link HintParam} properties, except for the text. This is useful
     * when we want to localize the text.
     * @param text A {@link String} value.
     * @return A new {@link HintParam} instance.
     */
    @NotNull
    public HintParam withNewText(@NotNull String text) {
        return builder().withHint(text).shouldIgnoreCase(ignoreCase).build();
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
         * @param hint The hint to be used to query elements.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withHint(@NotNull String hint) {
            PARAM.hint = hint;
            return this;
        }

        /**
         * Set the {@link #ignoreCase} value.
         * @param ignore A {@link Boolean} value.
         * @return The current {@link TextParam.Builder} instance.
         */
        @NotNull
        public Builder shouldIgnoreCase(boolean ignore) {
            PARAM.ignoreCase = ignore;
            return this;
        }

        @NotNull
        public HintParam build() {
            return PARAM;
        }
    }
    //endregion
}
