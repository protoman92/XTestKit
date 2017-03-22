package com.swiften.engine.base.param;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/22/17.
 */

/**
 * Parameter object for
 * {@link com.swiften.engine.base.PlatformEngine#rxStartTestEnvironment(StartEnvParam)}.
 */
public class StartEnvParam {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    private int retriesOnError;

    StartEnvParam() {
        retriesOnError = 10000;
    }

    public int retriesOnError() {
        return retriesOnError;
    }

    public static final class Builder {
        @NotNull private final StartEnvParam PARAM;

        Builder() {
            PARAM = new StartEnvParam();
        }

        /**
         * Set {@link #PARAM#retriesOnError} value. When used with
         * {@link io.reactivex.Flowable#retryWhen(Function)}, this number
         * will be incremented by 1, so when we call
         * {@link io.reactivex.Flowable#zipWith(Iterable, BiFunction)} with
         * a {@link io.reactivex.Flowable#range(int, int)}, we can compare
         * the second parameter to the aforementioned incremented value and
         * propagate the {@link Exception} when the retry count depletes.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetriesOnError(int retries) {
            PARAM.retriesOnError = retries;
            return this;
        }

        @NotNull
        public StartEnvParam build() {
            return PARAM;
        }
    }
}
