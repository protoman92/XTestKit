package org.swiften.xtestkit.navigation;

/**
 * Created by haipham on 5/20/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkit.mobile.Platform;

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
     * This will be called when the screen has been navigated to.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default NavigationSupplier rxa_onInitialized() {
        return a -> Flowable.just(true);
    }

    /**
     * Get the animation delay when the current {@link ScreenType} is being
     * navigated to. This value should be different for each
     * {@link PlatformType}, e.g.
     * {@link Platform#IOS} should have a non-zero
     * return value because we cannot disable animations.
     * @param platform {@link PlatformType} instance.
     * @return {@link Long} value.
     */
    long animationDelay(@NotNull PlatformType platform);

    /**
     * Get the {@link TimeUnit} to use with {@link #animationDelay(PlatformType)}.
     * @param platform {@link Engine} instance.
     * @return {@link TimeUnit} instance.
     * @see TimeUnit#MILLISECONDS
     */
    @NotNull
    default TimeUnit animationDelayTimeUnit(@NotNull PlatformType platform) {
        return TimeUnit.MILLISECONDS;
    }

    /**
     * Use this class to define to which {@link ScreenType} can a
     * {@link ScreenType} lead.
     */
    final class Direction {
        @NotNull final ScreenType TARGET;
        @NotNull final NavigationSupplier NAVIGATION;
        @NotNull final TimeUnit TIME_UNIT;
        final long DELAY;

        public Direction(@NotNull ScreenType target,
                         @NotNull NavigationSupplier navigation,
                         @NotNull PlatformType platform) {
            TARGET = target;
            NAVIGATION = navigation;
            DELAY = target.animationDelay(platform);
            TIME_UNIT = target.animationDelayTimeUnit(platform);
        }
    }

    @FunctionalInterface
    interface NavigationSupplier {
        /**
         * Get a navigation {@link Flowable} based on the previous navigation
         * result.
         * @param prev {@link Object} instance.
         * @return {@link Flowable} instance.
         */
        @NotNull Flowable<?> navigation(@NotNull Object prev);
    }
}
