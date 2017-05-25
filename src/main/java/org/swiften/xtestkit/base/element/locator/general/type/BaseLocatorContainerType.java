package org.swiften.xtestkit.base.element.locator.general.type;

/**
 * Created by haipham on 25/5/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides a {@link BaseLocatorType} instance.
 */
@FunctionalInterface
public interface BaseLocatorContainerType {
    /**
     * Get the associated {@link BaseLocatorType} instance.
     * @return {@link BaseLocatorType} instance.
     */
    @NotNull
    BaseLocatorType<?> locator();
}
