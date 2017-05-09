package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides a {@link PlatformType} for platform-specific
 * operations.
 */
@FunctionalInterface
public interface PlatformContainerType {
    /**
     * Get the associated {@link PlatformType} instance.
     * @return A {@link PlatformType} instance.
     */
    @NotNull PlatformType platform();
}
