package org.swiften.xtestkit.kit.param;

/**
 * Created by haipham on 4/1/17.
 */

import org.swiften.xtestkit.engine.base.type.IndexType;
import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.engine.base.type.RetriableType;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for {@link PlatformEngine}
 */
public class AfterClassParam implements IndexType, RetriableType {
    @NotNull public static AfterClassParam DEFAULT;

    static {
        DEFAULT = new AfterClassParam();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private int index;

    AfterClassParam() {}

    public int index() {
        return index;
    }

    public static final class Builder {
        @NotNull private final AfterClassParam PARAM;

        Builder() {
            PARAM = new AfterClassParam();
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
        public AfterClassParam build() {
            return PARAM;
        }
    }
}
