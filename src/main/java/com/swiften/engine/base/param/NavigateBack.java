package com.swiften.engine.base.param;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Parameter object for {@link PlatformEngine#rxNavigateBack(NavigateBack)}.
 */
public final class NavigateBack implements RetryProtocol {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    private int times;

    NavigateBack() {
        times = 1;
    }

    public int times() {
        return times;
    }

    public static final class Builder {
        @NotNull final NavigateBack PARAM;

        Builder() {
            PARAM = new NavigateBack();
        }

        /**
         * Specifies how many times the Appium driver should navigate back.
         * @param times An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withTimes(int times) {
            PARAM.times = times;
            return this;
        }

        @NotNull
        public NavigateBack build() {
            return PARAM;
        }
    }
}
