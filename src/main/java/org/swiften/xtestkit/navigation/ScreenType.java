package org.swiften.xtestkit.navigation;

/**
 * Created by haipham on 5/20/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This interface provides navigation vi{@link io.reactivex.Flowable} in
 * order to automatically map out test best way to traverse the UI.
 */
public interface ScreenType {
    /**
     * Get {@link List} of {@link Direction} that this {@link ScreenType}
     * can navigate to.
     * @param engine {@link Engine} instance. This is necessary because
     *               most of the time the navigation will rely on the
     *               {@link org.openqa.selenium.WebDriver}.
     * @return {@link List} of {@link Direction}.
     */
    @NotNull
    List<Direction> forwardAccessible(@NotNull Engine<?> engine);

    /**
     * Get a {@link List} of {@link Direction} that this {@link ScreenType}
     * can lead back to.
     * @param engine {@link Engine} instance. This is necessary because
     *               most of the time the navigation will rely on the
     *               {@link org.openqa.selenium.WebDriver}.
     * @return {@link List} of {@link Direction}.
     */
    @NotNull
    List<Direction> backwardAccessible(@NotNull Engine<?> engine);

    /**
     * Get the animation delay when the current {@link ScreenType} is being
     * navigated to. This value should be different for each
     * {@link org.swiften.xtestkit.base.type.PlatformType}, e.g.
     * {@link org.swiften.xtestkit.mobile.Platform#IOS} should have a non-zero
     * return value because we cannot disable animations.
     * @param engine {@link Engine} instance to check for
     * {@link org.swiften.xtestkit.base.type.PlatformType}.
     * @return {@link Long} value.
     */
    long animationDelay(@NotNull Engine<?> engine);

    /**
     * Get the {@link TimeUnit} to use with {@link #animationDelay(Engine)}.
     * @param engine {@link Engine} instance.
     * @return {@link TimeUnit} instance.
     * @see TimeUnit#MILLISECONDS
     */
    @NotNull
    default TimeUnit animationDelayTimeUnit(@NotNull Engine<?> engine) {
        return TimeUnit.MILLISECONDS;
    }

    /**
     * Use this class to define to which {@link ScreenType} can a
     * {@link ScreenType} lead.
     */
    final class Direction {
        @NotNull final ScreenType TARGET;
        @NotNull final Navigation NAVIGATION;
        @NotNull final TimeUnit TIME_UNIT;
        final long DELAY;

        public Direction(@NotNull Engine<?> engine,
                         @NotNull ScreenType target,
                         @NotNull Navigation navigator) {
            TARGET = target;
            NAVIGATION = navigator;
            DELAY = target.animationDelay(engine);
            TIME_UNIT = target.animationDelayTimeUnit(engine);
        }
    }

    @FunctionalInterface
    interface Navigation {
        /**
         * Get a navigation {@link Flowable} based on the previous navigation
         * result.
         * @param previousResult {@link Object} instance.
         * @return {@link Flowable} instance.
         */
        @NotNull Flowable<?> navigator(@NotNull Object previousResult);
    }
}
