package org.swiften.xtestkit.kit.param;

/**
 * Created by haipham on 4/1/17.
 */

import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.common.IndexType;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for
 * {@link Engine#rxa_beforeClass(BeforeClassParam)}
 */
public class BeforeClassParam implements IndexType, RetryType {
    public static BeforeClassParam DEFAULT;

    static {
        DEFAULT = new BeforeClassParam();
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

    BeforeClassParam() {
        index = 0;
    }

    @Override
    public int index() {
        return index;
    }

    //region Builder
    /**
     * Builder class for {@link BeforeClassParam}.
     */
    public static final class Builder {
        @NotNull private final BeforeClassParam PARAM;

        Builder() {
            PARAM = new BeforeClassParam();
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
        public BeforeClassParam build() {
            return PARAM;
        }
    }
    //endregion
}
