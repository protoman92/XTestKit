package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides a {@link PlatformType} for platform-specific
 * operations.
 */
public interface PlatformContainerType extends BaseErrorType {
    /**
     * Get the associated {@link PlatformType} instance.
     * @return A {@link PlatformType} instance.
     */
    @NotNull
    default PlatformType platform() {
        throw new RuntimeException(NOT_AVAILABLE);
    }
}
