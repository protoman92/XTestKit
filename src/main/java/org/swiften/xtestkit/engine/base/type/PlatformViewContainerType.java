package org.swiften.xtestkit.engine.base.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.PlatformView;

/**
 * This interface provides a {@link org.swiften.xtestkit.engine.base.PlatformView}
 * for {@link ViewType}-related operations.
 */
@FunctionalInterface
public interface PlatformViewContainerType {
    /**
     * Get the associated {@link PlatformView} instance.
     * @return A {@link PlatformView} instance.
     */
    @NotNull PlatformView platformView();
}
