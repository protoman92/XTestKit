package org.swiften.xtestkit.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.protocol.RepeatProviderType;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.base.Engine;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Parameter object for {@link Engine#rxa_navigateBack(RepeatProviderType)}.
 */
public final class NavigateBack implements RepeatProviderType, RetryProviderType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private int times;
    private long delay;

    NavigateBack() {
        times = 1;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RetryProviderType#retries()
     * @see Constants#DEFAULT_RETRIES
     */
    @Override
    public int retries() {
        return Constants.DEFAULT_RETRIES;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RepeatProviderType#times()
     * @see #times
     */
    @Override
    public int times() {
        return times;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RepeatProviderType#delay()
     * @see #delay
     */
    @Override
    public long delay() {
        return delay;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RepeatProviderType#timeUnit()
     * @see Constants#DEFAULT_TIME_UNIT
     */
    @NotNull
    @Override
    public TimeUnit timeUnit() {
        return Constants.DEFAULT_TIME_UNIT;
    }

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
         * @param times {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withTimes(int times) {
            PARAM.times = times;
            return this;
        }

        /**
         * Set the {@link #delay} value.
         * @param delay {@link Long} value.
         * @return {@link Builder} instance.
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
