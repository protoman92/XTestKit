package org.swiften.xtestkit.base.param;

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.type.DurationType;
import org.swiften.xtestkit.base.type.RepeatType;
import org.swiften.xtestkit.base.type.UnidirectionContainerType;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * Parameter object for
 * {@link org.swiften.xtestkit.base.BaseEngine#rxSwipeGenericUnidirectional(DurationType)}.
 * Use this to perform unidirectional swipe actions.
 */
public class UnidirectionalSwipeParam implements
    DurationType,
    RepeatType,
    UnidirectionContainerType
{
    /**
     * Get a {@link Builder} instance.
     * @return A {@link Builder} instance.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull Unidirection direction;
    private int times;
    private int duration;
    private long delay;

    UnidirectionalSwipeParam() {
        direction = Unidirection.LEFT_RIGHT;
        delay = RepeatType.super.delay();
    }

    //region Getters
    @NotNull
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
    //endregion

    //region Builder
    /**
     * Builder class for {@link UnidirectionalSwipeParam}.
     */
    public static final class Builder {
        @NotNull
        private final UnidirectionalSwipeParam PARAM;

        Builder() {
            PARAM = new UnidirectionalSwipeParam();
        }

        /**
         * Set the {@link #direction} instance.
         * @param direction A {@link Unidirection} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDirection(@NotNull Unidirection direction) {
            PARAM.direction = direction;
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
         * Set the {@link #duration} value.
         * @param duration An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDuration(int duration) {
            PARAM.duration = duration;
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
         * @param type A {@link RepeatType} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRepeatableType(@NotNull RepeatType type) {
            return withTimes(type.times()).withDelay(type.delay());
        }

        /**
         * Set the {@link #duration} value.
         * @param type A {@link DurationType} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDurationType(@NotNull DurationType type) {
            return this;
        }

        @NonNull
        public UnidirectionalSwipeParam build() {
            return PARAM;
        }
    }
    //endregion
}
