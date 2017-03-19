package com.swiften.engine.param;

import com.swiften.engine.TestEngine;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Parameter object for {@link TestEngine#rxNavigateBack(NavigateBack)}.
 */
public final class NavigateBack {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    public int times;

    NavigateBack() {
        times = 1;
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
