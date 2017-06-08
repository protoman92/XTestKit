package org.swiften.xtestkit.navigation;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 8/6/17.
 */

/**
 * This interface provides navigation {@link Flowable} that will be executed
 * when {@link ScreenManagerType} navigates {@link ScreenType}.
 */
public interface NavigationSupplier {
    /**
     * Get a navigation {@link Flowable} based on the previous navigation
     * result.
     * @param prev {@link Object} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull Flowable<?> navigation(@NotNull Object prev);
}
