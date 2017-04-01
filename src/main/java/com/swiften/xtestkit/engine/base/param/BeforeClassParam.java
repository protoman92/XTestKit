package com.swiften.xtestkit.engine.base.param;

/**
 * Created by haipham on 4/1/17.
 */

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for
 * {@link com.swiften.xtestkit.engine.base.PlatformEngine#rxBeforeClass(BeforeClassParam)}
 */
public class BeforeClassParam implements RetryProtocol {
    public static final BeforeClassParam DEFAULT;

    static {
        DEFAULT = new BeforeClassParam();
    }

    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    BeforeClassParam() {}

    public static final class Builder {
        @NotNull private final BeforeClassParam PARAM;

        Builder() {
            PARAM = new BeforeClassParam();
        }

        @NotNull
        public BeforeClassParam build() {
            return PARAM;
        }
    }
}
