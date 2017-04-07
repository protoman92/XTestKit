package com.swiften.xtestkit.engine.base.param;

/**
 * Created by haipham on 4/1/17.
 */

import com.swiften.xtestkit.engine.base.param.protocol.IndexProtocol;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for
 * {@link com.swiften.xtestkit.engine.base.PlatformEngine#rxBeforeMethod(BeforeParam)}
 */
public class BeforeParam implements IndexProtocol, RetryProtocol {
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
         * @param index An {@link Integer} value.
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
