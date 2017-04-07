package com.swiften.xtestkit.engine.base.param;

/**
 * Created by haipham on 4/8/17.
 */

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for
 * {@link com.swiften.xtestkit.engine.base.PlatformEngine#rxStartDriver(StartDriverParam)}
 */
public class StartDriverParam implements RetryProtocol {
    public static final StartDriverParam DEFAULT;

    static {
        DEFAULT = new StartDriverParam();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    StartDriverParam() {}

    public static final class Builder {
        @NotNull private final StartDriverParam PARAM;

        Builder() {
            PARAM = new StartDriverParam();
        }

        @NotNull
        public StartDriverParam build() {
            return PARAM;
        }
    }
}
