package com.swiften.xtestkit.engine.base.param;

/**
 * Created by haipham on 4/1/17.
 */

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for {@link com.swiften.xtestkit.engine.base.PlatformEngine}
 */
public class AfterClassParam implements RetryProtocol {
    @NotNull public static final AfterClassParam DEFAULT;

    static {
        DEFAULT = new AfterClassParam();
    }

    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        @NotNull private final AfterClassParam PARAM;

        Builder() {
            PARAM = new AfterClassParam();
        }

        @NotNull
        public AfterClassParam build() {
            return PARAM;
        }
    }
}
