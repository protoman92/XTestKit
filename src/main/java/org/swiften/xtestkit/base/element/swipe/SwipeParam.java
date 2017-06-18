package org.swiften.xtestkit.base.element.swipe;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Point;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.common.DurationType;
import org.swiften.xtestkitcomponents.common.RepeatType;

/**
 * Parameter object for {@link Engine#rxa_swipe(RepeatType)}
 */
public class SwipeParam implements RepeatType, SwipeParamType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
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
        delay = RepeatType.super.delay();
        duration = SwipeParamType.super.duration();
        times = 1;
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
         * @param startX {@link Integer} value.
         * @return {@link Builder} instance.
         * @see #startX
         */
        @NotNull
        public Builder withStartX(int startX) {
            PARAM.startX = startX;
            return this;
        }

        /**
         * Set the {@link #startY} value.
         * @param startY {@link Integer} value.
         * @return {@link Builder} instance.
         * @see #startY
         */
        @NotNull
        public Builder withStartY(int startY) {
            PARAM.startY = startY;
            return this;
        }

        /**
         * Set the {@link #endX} value.
         * @param endX {@link Integer} value.
         * @return {@link Builder} instance.
         * @see #endX
         */
        @NonNull
        public Builder withEndX(int endX) {
            PARAM.endX = endX;
            return this;
        }

        /**
         * Set the {@link #endY} value.
         * @param endY {@link Integer} value.
         * @return {@link Builder} instance.
         * @see #endY
         */
        @NonNull
        public Builder withEndY(int endY) {
            PARAM.endY = endY;
            return this;
        }

        /**
         * Set {@link #startX} and {@link #startY}.
         * @param point {@link Point} instance.
         * @return {@link Builder} instance.
         * @see Point#getX()
         * @see Point#getY()
         * @see #withStartX(int)
         * @see #withStartY(int)
         */
        @NotNull
        public Builder withStartXY(@NotNull Point point) {
            return this.withStartX(point.getX()).withStartY(point.getY());
        }

        /**
         * Set {@link #endX} and {@link #endY}.
         * @param point {@link Point} instance.
         * @return {@link Builder} instance.
         * @see Point#getX()
         * @see Point#getY()
         * @see #withStartX(int)
         * @see #withStartY(int)
         */
        @NotNull
        public Builder withEndXY(@NotNull Point point) {
            return this.withEndX(point.getX()).withEndY(point.getY());
        }

        /**
         * Set the {@link #duration} value.
         * @param duration {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NonNull
        public Builder withDuration(int duration) {
            PARAM.duration = duration;
            return this;
        }

        /**
         * Set the {@link #times} value.
         * @param times {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NonNull
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

        /**
         * Set {@link #times} and {@link #delay}.
         * @param type {@link RepeatType} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withRepeatableType(@NotNull RepeatType type) {
            return withTimes(type.times()).withDelay(type.delay());
        }

        /**
         * Set {@link #duration}.
         * @param type {@link DurationType} instance.
         * @return {@link Builder} instance.
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
