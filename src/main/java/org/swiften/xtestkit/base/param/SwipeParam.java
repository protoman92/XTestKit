package org.swiften.xtestkit.base.param;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.BaseEngine;
import org.swiften.xtestkit.base.type.DurationType;
import org.swiften.xtestkit.base.type.RepeatableType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;

/**
 * Parameter object for
 * {@link BaseEngine#rxSwipe(RepeatableType)}
 */
public class SwipeParam implements RepeatableType, SwipeType {
    /**
     * Get a {@link Builder} instance.
     * @return A {@link Builder} instance.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int duration;
    private int times;
    private long delay;

    SwipeParam() {
        delay = RepeatableType.super.delay();
        duration = SwipeType.super.duration();
    }

    @Override
    public String toString() {
        return String.format(
            "startX %1$d, endX %2$d, startY %3$s, endY %4$s. Duration %5$d",
            startX(),
            endX(),
            startY(),
            endY(),
            duration()
        );
    }

    //region Getters
    public int startX() {
        return startX;
    }

    public int startY() {
        return startY;
    }

    public int endX() {
        return endX;
    }

    public int endY() {
        return endY;
    }

    public int duration() {
        return duration;
    }

    public int times() {
        return times;
    }

    public long delay() {
        return delay;
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link SwipeParam}.
     */
    public static final class Builder {
        @NotNull private final SwipeParam PARAM;

        Builder() {
            PARAM = new SwipeParam();
        }

        /**
         * Set the {@link #startX} value.
         * @param startX An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withStartX(int startX) {
            PARAM.startX = startX;
            return this;
        }

        /**
         * Set the {@link #startY} value.
         * @param startY An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withStartY(int startY) {
            PARAM.startY = startY;
            return this;
        }

        /**
         * Set the {@link #endX} value.
         * @param endX An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NonNull
        public Builder withEndX(int endX) {
            PARAM.endX = endX;
            return this;
        }

        /**
         * Set the {@link #endY} value.
         * @param endY An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NonNull
        public Builder withEndY(int endY) {
            PARAM.endY = endY;
            return this;
        }

        /**
         * Set the {@link #duration} value.
         * @param duration An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NonNull
        public Builder withDuration(int duration) {
            PARAM.duration = duration;
            return this;
        }

        /**
         * Set the {@link #times} value.
         * @param times An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NonNull
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

        /**
         * Set {@link #times} and {@link #delay}.
         * @param type A {@link RepeatableType} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRepeatableType(@NotNull RepeatableType type) {
            return withTimes(type.times()).withDelay(type.delay());
        }

        /**
         * Set {@link #duration}.
         * @param type A {@link DurationType} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDurationType(@NotNull DurationType type) {
            return withDuration(type.duration());
        }

        @NonNull
        public SwipeParam build() {
            return PARAM;
        }
    }
    //endregion
}
