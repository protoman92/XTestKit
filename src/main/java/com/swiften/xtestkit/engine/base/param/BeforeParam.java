package com.swiften.xtestkit.engine.base.param;

/**
 * Created by haipham on 4/1/17.
 */

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for
 * {@link com.swiften.xtestkit.engine.base.PlatformEngine#rxBefore(BeforeParam)}
 */
public class BeforeParam implements RetryProtocol {
    @NotNull public static BeforeParam DEFAULT;

    static {
        DEFAULT = new BeforeParam();
    }

    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    BeforeParam() {}

    public static final class Builder {
        @NotNull private final BeforeParam PARAM;

        Builder() {
            PARAM = new BeforeParam();
        }

        @NotNull
        public BeforeParam build() {
            return PARAM;
        }
    }
}
