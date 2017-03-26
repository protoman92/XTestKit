package com.swiften.xtestkit.engine.base.param;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/23/17.
 */

/**
 * Parameter object for {@link }
 */
public class StopEnvParam implements RetryProtocol {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    StopEnvParam() {}

    public static final class Builder {
        @NotNull private final StopEnvParam PARAM;

        Builder() {
            PARAM = new StopEnvParam();
        }

        @NotNull
        public StopEnvParam build() {
            return PARAM;
        }
    }
}
