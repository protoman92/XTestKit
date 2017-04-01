package com.swiften.xtestkit.engine.base.param;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/1/17.
 */
public class AfterParam implements RetryProtocol {
    @NotNull public static final AfterParam DEFAULT;

    static {
        DEFAULT = new AfterParam();
    }

    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    AfterParam() {}

    public static final class Builder {
        @NotNull private final AfterParam PARAM;

        Builder() {
            PARAM = new AfterParam();
        }

        @NotNull
        public AfterParam build() {
            return PARAM;
        }
    }
}
