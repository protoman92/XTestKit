package org.swiften.xtestkit.kit.param;

/**
 * Created by haipham on 4/1/17.
 */

import org.swiften.xtestkitcomponents.common.IndexType;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for {@link Engine}
 */
public class AfterClassParam implements IndexType, RetryType {
    @NotNull public static AfterClassParam DEFAULT;

    static {
        DEFAULT = new AfterClassParam();
    }

    /**
     * Get a {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private int index;

    AfterClassParam() {}

    @Override
    public int index() {
        return index;
    }

    //region Builder
    /**
     * Builder class for {@link AfterClassParam}.
     */
    public static final class Builder {
        @NotNull private final AfterClassParam PARAM;

        Builder() {
            PARAM = new AfterClassParam();
        }

        /**
         * Set the {@link #PARAM#index} value.
         * @param index {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withIndex(int index) {
            PARAM.index = index;
            return this;
        }

        @NotNull
        public AfterClassParam build() {
            return PARAM;
        }
    }
    //endregion
}
