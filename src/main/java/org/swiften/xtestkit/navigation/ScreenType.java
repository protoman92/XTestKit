package org.swiften.xtestkit.navigation;

/**
 * Created by haipham on 5/20/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.xtestkit.base.BaseEngine;

import java.util.List;

/**
 * This interface provides navigation via {@link io.reactivex.Flowable} in
 * order to automatically map out test best way to traverse the UI.
 */
public interface ScreenType {
    /**
     * Get a {@link List} of {@link Direction} that this {@link ScreenType}
     * can navigate to.
     * @param engine A {@link BaseEngine} instance. This is necessary because
     *               most of the time the navigation will rely on the
     *               {@link org.openqa.selenium.WebDriver}.
     * @return A {@link List} of {@link Direction}.
     */
    @NotNull
    List<Direction> accessibleFromHere(@NotNull BaseEngine<?> engine);

    /**
     * Use this class to define to which {@link ScreenType} can a
     * {@link ScreenType} lead.
     */
    final class Direction {
        @NotNull final ScreenType TARGET;
        @NotNull final Navigation NAVIGATION;

        Direction(@NotNull ScreenType target, @NotNull Navigation navigator) {
            TARGET = target;
            NAVIGATION = navigator;
        }
    }

    @FunctionalInterface
    interface Navigation {
        /**
         * Get a navigation {@link Flowable} based on the previous navigation
         * result.
         * @param previousResult An {@link Object} instance.
         * @return A {@link Flowable} instance.
         */
        @NotNull Flowable<?> navigator(@NotNull Object previousResult);
    }
}
