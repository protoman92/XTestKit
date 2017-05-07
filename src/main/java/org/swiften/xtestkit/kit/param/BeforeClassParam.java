package org.swiften.xtestkit.kit.param;

/**
 * Created by haipham on 4/1/17.
 */

import org.swiften.xtestkit.engine.base.type.IndexType;
import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.engine.base.type.RetryType;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for
 * {@link PlatformEngine#rxBeforeClass(BeforeClassParam)}
 */
public class BeforeClassParam implements IndexType, RetryType {
    public static BeforeClassParam DEFAULT;

    static {
        DEFAULT = new BeforeClassParam();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private int index;

    BeforeClassParam() {
        index = 0;
    }

    public int index() {
        return index;
    }

    public static final class Builder {
        @NotNull private final BeforeClassParam PARAM;

        Builder() {
            PARAM = new BeforeClassParam();
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
        public BeforeClassParam build() {
            return PARAM;
        }
    }
}
