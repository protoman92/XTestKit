package org.swiften.xtestkit.kit.param;

import org.swiften.xtestkitcomponents.common.IndexType;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/1/17.
 */
public class AfterParam implements IndexType, RetryType {
    @NotNull public static AfterParam DEFAULT;

    static {
        DEFAULT = new AfterParam();
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

    AfterParam() {}

    @Override
    public int index() {
        return index;
    }

    //region Builder
    /**
     * Builder class for {@link AfterParam},
     */
    public static final class Builder {
        @NotNull private final AfterParam PARAM;

        Builder() {
            PARAM = new AfterParam();
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
        public AfterParam build() {
            return PARAM;
        }
    }
    //endregion
}
