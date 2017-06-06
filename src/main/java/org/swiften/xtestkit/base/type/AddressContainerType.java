package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/28/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Address;

/**
 * This interface provides a {@link org.swiften.xtestkit.base.Address} instance.
 */
@FunctionalInterface
public interface AddressContainerType {
    /**
     * Get the associated {@link Address} instance.
     * @return {@link Address} instance.
     */
    @NotNull Address address();
}
