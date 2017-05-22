package org.swiften.xtestkit.kit.param;

/**
 * Created by haipham on 4/1/17.
 */

import org.swiften.xtestkit.base.type.IndexType;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.type.RetryType;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for
 * {@link Engine#rx_beforeMethod(BeforeParam)}
 */
public class BeforeParam implements IndexType, RetryType {
    @NotNull public static BeforeParam DEFAULT;

    static {
        DEFAULT = new BeforeParam();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private int index;

    BeforeParam() {}

    public int index() {
        return index;
    }

    public static final class Builder {
        @NotNull private final BeforeParam PARAM;

        Builder() {
            PARAM = new BeforeParam();
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
        public BeforeParam build() {
            return PARAM;
        }
    }
}
