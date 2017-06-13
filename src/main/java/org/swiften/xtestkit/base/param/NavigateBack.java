package org.swiften.xtestkit.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.common.RepeatType;
import org.swiften.xtestkitcomponents.common.RetryType;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Parameter object for {@link Engine#rxa_navigateBack(RepeatType)}.
 */
public final class NavigateBack implements RepeatType, RetryType {
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

    //region RepeatType
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
