package org.swiften.xtestkit.base.param;

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.protocol.DurationProviderType;
import org.swiften.javautilities.protocol.RepeatProviderType;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.element.swipe.RLSwipePositionType;
import org.swiften.xtestkitcomponents.coordinate.RLPositionType;
import org.swiften.xtestkitcomponents.direction.Direction;
import org.swiften.xtestkitcomponents.direction.DirectionProviderType;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * Parameter object for
 * {@link Engine#rxa_swipeGeneric(WebElement, DirectionProviderType)}.
 * Use this to perform unidirectional swipe actions.
 */
public class DirectionParam implements
    DurationProviderType,
    DirectionProviderType,
    RLSwipePositionType,
    RepeatProviderType
{
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get {@link DirectionParam} instance from {@link P} instance.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link DirectionParam} instance.
     * @see Builder#withParam(DirectionProviderType)
     */
    @NotNull
    public static <P extends
        DirectionProviderType &
        DurationProviderType &
        RepeatProviderType &
        RLSwipePositionType> DirectionParam from(@NotNull P param) {
        return builder().withParam(param).build();
    }

    @NotNull Direction direction;
    private double startRatio, endRatio, anchorRatio;
    private int times, duration;
    private long delay;

    DirectionParam() {
        direction = Direction.LEFT_RIGHT;
        delay = Constants.DEFAULT_DELAY;
        times = 1;
        startRatio = RLSwipePositionType.super.startRatio();
        endRatio = RLSwipePositionType.super.endRatio();
        anchorRatio = RLSwipePositionType.super.anchorRatio();
    }

    @NotNull
    @Override
    public String toString() {
        return direction().toString();
    }

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
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see #times
     */
    @Override
    public int times() {
        return times;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Long} value.
     * @see #delay
     */
    @Override
    public long delay() {
        return delay;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see #duration
     */
    @Override
    public int duration() {
        return duration;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Double} value.
     * @see #startRatio
     */
    @Override
    public double startRatio() {
        return startRatio;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Double} value.
     * @see #endRatio
     */
    @Override
    public double endRatio() {
        return endRatio;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Double} value.
     * @see #anchorRatio
     */
    @Override
    public double anchorRatio() {
        return anchorRatio;
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

    /**
     * Create a new {@link DirectionParam} with a possibly new {@link Direction}
     * instance.
     * @param direction {@link Direction} instance.
     * @return {@link DirectionParam} instance.
     * @see Builder#withDirection(Direction)
     * @see Builder#withParam(DirectionProviderType)
     * @see #builder()
     */
    @NotNull
    public DirectionParam withDirection(@NotNull Direction direction) {
        return builder().withParam(this).withDirection(direction).build();
    }

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
        public Builder withDirectionProvider(@NotNull DirectionProviderType param) {
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
         * @param type {@link RepeatProviderType} instance.
         * @return {@link Builder} instance.
         * @see RepeatProviderType#times()
         * @see RepeatProviderType#delay()
         */
        @NotNull
        public Builder withRepeatProvider(@NotNull RepeatProviderType type) {
            return withTimes(type.times()).withDelay(type.delay());
        }

        /**
         * Set {@link #duration}.
         * @param type {@link DurationProviderType} instance.
         * @return {@link Builder} instance.
         * @see DurationProviderType#duration()
         * @see #withDuration(int)
         */
        @NotNull
        public Builder withDurationProvider(@NotNull DurationProviderType type) {
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
         * Same as above, but uses {@link RLPositionType}.
         * @param type {@link RLPositionType} instance.
         * @return {@link Builder} instance.
         * @see RLPositionType#dimensionRatio()
         * @see #withAnchorRatio(double)
         */
        @NotNull
        public Builder withAnchorRLPosition(@NotNull RLPositionType type) {
            return withAnchorRatio(type.dimensionRatio());
        }

        /**
         * Set the {@link #startRatio} and {@link #endRatio} values.
         * @param type {@link RLSwipePositionType} instance.
         * @return {@link Builder} instance.
         * @see RLSwipePositionType#anchorRatio()
         * @see RLSwipePositionType#endRatio()
         * @see RLSwipePositionType#startRatio()
         * @see #withAnchorRatio(double)
         * @see #withEndRatio(double)
         * @see #withStartRatio(double)
         */
        @NotNull
        public Builder withRLSwipePosition(@NotNull RLSwipePositionType type) {
            return this
                .withStartRatio(type.startRatio())
                .withEndRatio(type.endRatio())
                .withAnchorRatio(type.anchorRatio());
        }

        /**
         * Copy properties from another {@link P} instance.
         * @param param {@link P} instance.
         * @param <P> Generics parameter.
         * @return {@link Builder} instance.
         * @see #withDirectionProvider(DirectionProviderType)
         * @see #withDurationProvider(DurationProviderType)
         * @see #withRepeatProvider(RepeatProviderType)
         * @see #withRLSwipePosition(RLSwipePositionType)
         */
        @NotNull
        public <P extends
            DirectionProviderType &
            DurationProviderType &
            RepeatProviderType &
            RLSwipePositionType> Builder withParam(@NotNull P param)
        {
            return this
                .withDirectionProvider(param)
                .withRepeatProvider(param)
                .withDurationProvider(param)
                .withRLSwipePosition(param);
        }

        @NonNull
        public DirectionParam build() {
            return PARAM;
        }
    }
    //endregion
}
