package org.swiften.xtestkit.base.param;

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.direction.Unidirection;
import org.swiften.xtestkit.base.element.swipe.SwipeDampenType;
import org.swiften.xtestkitcomponents.common.DurationType;
import org.swiften.xtestkitcomponents.common.RepeatType;
import org.swiften.xtestkitcomponents.direction.UnidirectionContainerType;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * Parameter object for {@link Engine#rxa_swipeGeneric(WebElement, DurationType)}.
 * Use this to perform unidirectional swipe actions.
 */
public class UnidirectionParam implements
    DurationType,
    RepeatType,
    SwipeDampenType,
    UnidirectionContainerType
{
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull Unidirection direction;
    private double startRatio, endRatio;
    private int times, duration;
    private long delay;

    UnidirectionParam() {
        direction = Unidirection.LEFT_RIGHT;
        delay = RepeatType.super.delay();
        times = 1;
        startRatio = SwipeDampenType.super.startRatio();
        endRatio = SwipeDampenType.super.endRatio();
    }

    //region Getters
    @NotNull
    @Override
    public Unidirection direction() {
        return direction;
    }

    @Override
    public int times() {
        return times;
    }

    @Override
    public long delay() {
        return delay;
    }

    @Override
    public int duration() {
        return duration;
    }

    @Override
    public double startRatio() {
        return startRatio;
    }

    @Override
    public double endRatio() {
        return endRatio;
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link UnidirectionParam}.
     */
    public static final class Builder {
        @NotNull
        private final UnidirectionParam PARAM;

        Builder() {
            PARAM = new UnidirectionParam();
        }

        /**
         * Set the {@link #direction} instance.
         * @param direction {@link Unidirection} instance.
         * @return The current {@link Builder} instance.
         * @see #direction
         */
        @NotNull
        public Builder withDirection(@NotNull Unidirection direction) {
            PARAM.direction = direction;
            return this;
        }

        /**
         * Set the {@link #times} value.
         * @param times {@link Integer} value.
         * @return The current {@link Builder} instance.
         * @see #times
         */
        @NonNull
        public Builder withTimes(int times) {
            PARAM.times = times;
            return this;
        }

        /**
         * Set the {@link #duration} value.
         * @param duration {@link Integer} value.
         * @return The current {@link Builder} instance.
         * @see #duration
         */
        @NotNull
        public Builder withDuration(int duration) {
            PARAM.duration = duration;
            return this;
        }

        /**
         * Set the {@link #delay} value.
         * @param delay {@link Long} value.
         * @return The current {@link Builder} instance.
         * @see #delay
         */
        @NotNull
        public Builder withDelay(long delay) {
            PARAM.delay = delay;
            return this;
        }

        /**
         * Set {@link #times} and {@link #delay}.
         * @param type {@link RepeatType} instance.
         * @return The current {@link Builder} instance.
         * @see RepeatType#times()
         * @see RepeatType#delay()
         */
        @NotNull
        public Builder withRepeatableType(@NotNull RepeatType type) {
            return withTimes(type.times()).withDelay(type.delay());
        }

        /**
         * Set the {@link #duration} value.
         * @param type {@link DurationType} instance.
         * @return The current {@link Builder} instance.
         * @see DurationType#duration()
         * @see #withDuration(int)
         */
        @NotNull
        public Builder withDurationType(@NotNull DurationType type) {
            return withDuration(type.duration());
        }

        /**
         * Set the {@link #startRatio} value.
         * @param startRatio {@link Double} value.
         * @return The current {@link Builder} instance.
         * @see #startRatio
         */
        @NotNull
        public Builder withStartRatio(double startRatio) {
            PARAM.startRatio = startRatio;
            return this;
        }

        /**
         * Set the {@link #endRatio} value.
         * @param endRatio {@link Double} value.
         * @return The current {@link Builder} instance.
         * @see #endRatio
         */
        @NotNull
        public Builder withEndRatio(double endRatio) {
            PARAM.endRatio = endRatio;
            return this;
        }

        /**
         * Set the {@link #startRatio} and {@link #endRatio} values.
         * @param type {@link SwipeDampenType} instance.
         * @return The current {@link Builder} instance.
         * @see SwipeDampenType#startRatio()
         * @see SwipeDampenType#endRatio()
         * @see #withStartRatio(double)
         * @see #withEndRatio(double)
         */
        @NotNull
        public Builder withSwipeDampenType(@NotNull SwipeDampenType type) {
            return withStartRatio(type.startRatio()).withEndRatio(type.endRatio());
        }

        @NonNull
        public UnidirectionParam build() {
            return PARAM;
        }
    }
    //endregion
}
