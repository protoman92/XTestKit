package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides a platform name.
 */
@FunctionalInterface
public interface PlatformNameContainerType {
    /**
     * Get the associated platform name.
     * @return {@link String} value.
     */
    @NotNull String platformName();
}
