package org.swiften.xtestkit.engine.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.engine.base.type.RepeatableType;
import org.swiften.xtestkit.engine.base.type.RetryType;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Parameter object for {@link PlatformEngine#rxNavigateBack(NavigateBack)}.
 */
public final class NavigateBack implements RepeatableType, RetryType {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private int times;
    private long delay;

    NavigateBack() {
        times = 1;
    }

    //region RepeatableType
    @Override
    public int times() {
        return times;
    }

    @Override
    public long delay() {
        return delay;
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link NavigateBack}.
     */
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

        /**
         * Set the {@link #delay} value.
         * @param delay An {@link Long} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDelay(long delay) {
            PARAM.delay = delay;
            return this;
        }

        @NotNull
        public NavigateBack build() {
            return PARAM;
        }
    }
    //endregion
}
