package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.common.BaseErrorType;

/**
 * This interface provides {@link PlatformType} for platform-specific
 * operations.
 */
public interface PlatformContainerType extends BaseErrorType {
    /**
     * Get the associated {@link PlatformType} instance.
     * @return {@link PlatformType} instance.
     */
    @NotNull PlatformType platform();

    /**
     * Get the associated platform name.
     * @return {@link String} value.
     * @see PlatformType#value()
     */
    @NotNull String platformName();
}
