package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.PlatformView;

/**
 * This interface provides a {@link org.swiften.xtestkit.base.PlatformView}
 * for {@link BaseViewType}-related operations.
 */
@FunctionalInterface
public interface PlatformViewContainerType {
    /**
     * Get the associated {@link PlatformView} instance.
     * @return A {@link PlatformView} instance.
     */
    @NotNull PlatformView platformView();
}
