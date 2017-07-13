package org.swiften.xtestkit.base.element.swipe;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Point;
import org.swiften.javautilities.protocol.DelayProviderType;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.base.Engine;
import org.swiften.javautilities.protocol.DurationProviderType;
import org.swiften.javautilities.protocol.RepeatProviderType;
import org.swiften.javautilities.protocol.TimeUnitProviderType;

import java.util.concurrent.TimeUnit;

/**
 * Parameter object for {@link Engine#rxa_swipe(RepeatProviderType)}
 */
public class SwipeParam implements DurationProviderType, RepeatProviderType, SwipeParamType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private TimeUnit unit;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int duration;
    private int times;
    private long delay;

    SwipeParam() {
        delay = Constants.DEFAULT_DELAY;
        duration = SwipeParamType.super.duration();
        unit = Constants.DEFAULT_TIME_UNIT;
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

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see SwipeParamType#startX()
     */
    @Override
    public int startX() {
        return startX;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see SwipeParamType#startY()
     */
    @Override
    public int startY() {
        return startY;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see SwipeParamType#endX()
     */
    @Override
    public int endX() {
        return endX;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see SwipeParamType#endY()
     */
    @Override
    public int endY() {
        return endY;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see DurationProviderType#duration()
     */
    @Override
    public int duration() {
        return duration;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RepeatProviderType#times()
     */
    @Override
    public int times() {
        return times;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see DelayProviderType#delay()
     */
    @Override
    public long delay() {
        return delay;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link TimeUnit} instance.
     * @see TimeUnitProviderType#timeUnit()
     */
    @NotNull
    @Override
    public TimeUnit timeUnit() {
        return unit;
    }

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
         * Set the {@link #unit} instance.
         * @param unit {@link TimeUnit} instance.
         * @return {@link Builder} instance.
         * @see #unit
         */
        @NotNull
        public Builder withTimeUnit(@NotNull TimeUnit unit) {
            PARAM.unit = unit;
            return this;
        }

        /**
         * Set the {@link #unit} instance.
         * @param param {@link TimeUnitProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withTimeUnit(TimeUnit)
         */
        @NotNull
        public Builder withTimeUnitProvider(@NotNull TimeUnitProviderType param) {
            return withTimeUnit(param.timeUnit());
        }

        /**
         * Set {@link #times} and {@link #delay}.
         * @param type {@link RepeatProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withDelay(long)
         * @see #withTimes(int)
         * @see #withTimeUnitProvider(TimeUnitProviderType)
         */
        @NotNull
        public Builder withRepeatProvider(@NotNull RepeatProviderType type) {
            return this
                .withTimes(type.times())
                .withDelay(type.delay())
                .withTimeUnitProvider(type);
        }

        /**
         * Set {@link #duration}.
         * @param type {@link DurationProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withDuration(int)
         * @see #withTimeUnitProvider(TimeUnitProviderType)
         */
        @NotNull
        public Builder withDurationProvider(@NotNull DurationProviderType type) {
            return this
                .withDuration(type.duration())
                .withTimeUnitProvider(type);
        }

        /**
         * Get {@link #PARAM}.
         * @return {@link SwipeParam} instance.
         */
        @NonNull
        public SwipeParam build() {
            return PARAM;
        }
    }
    //endregion
}
