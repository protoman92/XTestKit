package com.swiften.engine.base.param;

import com.swiften.engine.base.param.protocol.RetryProtocol;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/22/17.
 */

/**
 * Parameter object for
 * {@link com.swiften.engine.base.PlatformEngine#rxStartTestEnvironment(StartEnvParam)}.
 * When {@link #retriesOnError()} is used with
 * {@link io.reactivex.Flowable#retryWhen(Function)}, it will be incremented
 * by 1, so when we call
 * {@link io.reactivex.Flowable#zipWith(Iterable, BiFunction)} with
 * a {@link io.reactivex.Flowable#range(int, int)}, we can compare the second
 * parameter to the aforementioned incremented value and propagate the
 * {@link Exception} when the retry count depletes.
 */
public class StartEnvParam implements RetryProtocol {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    StartEnvParam() {}

    /**
     * We need to return a large number due to emulator bootup time being
     * somewhat ridiculous.
     * @return An {@link Integer} value.
     */
    @Override
    public int retriesOnError() {
        return 1000;
    }

    public static final class Builder {
        @NotNull private final StartEnvParam PARAM;

        Builder() {
            PARAM = new StartEnvParam();
        }

        @NotNull
        public StartEnvParam build() {
            return PARAM;
        }
    }
}
