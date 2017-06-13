package org.swiften.xtestkit.base.param;

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.direction.Direction;
import org.swiften.xtestkit.base.element.swipe.RelativeSwipePositionType;
import org.swiften.xtestkitcomponents.common.DurationType;
import org.swiften.xtestkitcomponents.common.RepeatType;
import org.swiften.xtestkitcomponents.direction.DirectionProviderType;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * Parameter object for
 * {@link Engine#rxa_swipeGeneric(WebElement, DirectionProviderType)}.
 * Use this to perform unidirectional swipe actions.
 */
public class DirectionParam implements
    DurationType,
    DirectionProviderType,
    RelativeSwipePositionType,
    RepeatType
{
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    Direction direction;
    private double startRatio, endRatio, anchorRatio;
    private int times, duration;
    private long delay;

    DirectionParam() {
        direction = Direction.LEFT_RIGHT;
        delay = RepeatType.super.delay();
        times = 1;
        startRatio = RelativeSwipePositionType.super.startRatio();
        endRatio = RelativeSwipePositionType.super.endRatio();
        anchorRatio = RelativeSwipePositionType.super.anchorRatio();
    }

    //region Getters
    /**
     * Get {@link #direction}.
     * @return {@link Direction} instance.
     * @see #direction
     */
    @NotNull
    @Override
    public Direction direction() {
        return direction;
    }

    /**
     * Get {@link #times}.
     * @return {@link Integer} value.
     * @see #times
     */
    @Override
    public int times() {
        return times;
    }

    /**
     * Get {@link #delay}.
     * @return {@link Long} value.
     * @see #delay
     */
    @Override
    public long delay() {
        return delay;
    }

    /**
     * Get {@link #duration}.
     * @return {@link Integer} value.
     * @see #duration
     */
    @Override
    public int duration() {
        return duration;
    }

    /**
     * Get {@link #startRatio}.
     * @return {@link Double} value.
     * @see #startRatio
     */
    @Override
    public double startRatio() {
        return startRatio;
    }

    /**
     * Get {@link #endRatio}.
     * @return {@link Double} value.
     * @see #endRatio
     */
    @Override
    public double endRatio() {
        return endRatio;
    }

    /**
     * Get {@link #anchorRatio}.
     * @return {@link Double} value.
     * @see #anchorRatio
     */
    @Override
    public double anchorRatio() {
        return anchorRatio;
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link DirectionParam}.
     */
    public static final class Builder {
        @NotNull
        private final DirectionParam PARAM;

        Builder() {
            PARAM = new DirectionParam();
        }

        /**
         * Set the {@link #direction} instance.
         * @param direction {@link Direction} instance.
         * @return {@link Builder} instance.
         * @see #direction
         */
        @NotNull
        public Builder withDirection(@NotNull Direction direction) {
            PARAM.direction = direction;
            return this;
        }

        /**
         * Set the {@link #direction} instance.
         * @param param {@link DirectionProviderType} instance.
         * @return {@link Builder} instance.
         * @see DirectionProviderType#direction()
         * @see #withDirection(Direction)
         */
        @NotNull
        public Builder withDirectionContainer(@NotNull DirectionProviderType param) {
            return withDirection(param.direction());
        }

        /**
         * Set the {@link #times} value.
         * @param times {@link Integer} value.
         * @return {@link Builder} instance.
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
         * @return {@link Builder} instance.
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
         * @return {@link Builder} instance.
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
         * @return {@link Builder} instance.
         * @see RepeatType#times()
         * @see RepeatType#delay()
         */
        @NotNull
        public Builder withRepeatableType(@NotNull RepeatType type) {
            return withTimes(type.times()).withDelay(type.delay());
        }

        /**
         * Set {@link #duration}.
         * @param type {@link DurationType} instance.
         * @return {@link Builder} instance.
         * @see DurationType#duration()
         * @see #withDuration(int)
         */
        @NotNull
        public Builder withDurationType(@NotNull DurationType type) {
            return withDuration(type.duration());
        }

        /**
         * Set {@link #startRatio}.
         * @param startRatio {@link Double} value.
         * @return {@link Builder} instance.
         * @see #startRatio
         */
        @NotNull
        public Builder withStartRatio(double startRatio) {
            PARAM.startRatio = startRatio;
            return this;
        }

        /**
         * Set {@link #endRatio}.
         * @param endRatio {@link Double} value.
         * @return {@link Builder} instance.
         * @see #endRatio
         */
        @NotNull
        public Builder withEndRatio(double endRatio) {
            PARAM.endRatio = endRatio;
            return this;
        }

        /**
         * Set {@link #anchorRatio}.
         * @param anchorRatio {@link Double} value.
         * @return {@link Builder} instance.
         * @see #anchorRatio
         */
        @NotNull
        public Builder withAnchorRatio(double anchorRatio) {
            PARAM.anchorRatio = anchorRatio;
            return this;
        }

        /**
         * Set the {@link #startRatio} and {@link #endRatio} values.
         * @param type {@link RelativeSwipePositionType} instance.
         * @return {@link Builder} instance.
         * @see RelativeSwipePositionType#anchorRatio()
         * @see RelativeSwipePositionType#endRatio()
         * @see RelativeSwipePositionType#startRatio()
         * @see #withAnchorRatio(double)
         * @see #withEndRatio(double)
         * @see #withStartRatio(double)
         */
        @NotNull
        public Builder withSwipeDampenType(@NotNull RelativeSwipePositionType type) {
            return this
                .withStartRatio(type.startRatio())
                .withEndRatio(type.endRatio())
                .withAnchorRatio(type.anchorRatio());
        }

        @NonNull
        public DirectionParam build() {
            return PARAM;
        }
    }
    //endregion
}
