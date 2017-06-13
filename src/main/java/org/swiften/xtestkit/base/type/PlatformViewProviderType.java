package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkitcomponents.view.BaseViewType;

/**
 * This interface provides {@link org.swiften.xtestkit.base.PlatformView}
 * for {@link BaseViewType}-related operations.
 */
@FunctionalInterface
public interface PlatformViewProviderType {
    /**
     * Get the associated {@link PlatformView} instance.
     * @return {@link PlatformView} instance.
     */
    @NotNull PlatformView platformView();
}
