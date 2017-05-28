package org.swiften.xtestkit.system.network.type;

/**
 * Created by haipham on 5/28/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.system.network.NetworkHandler;

/**
 * This interface provides a {@link NetworkHandler} instance.
 */
@FunctionalInterface
public interface NetworkHandlerHolderType {
    /**
     * Get the associated {@link NetworkHandler} instance.
     * @return {@link NetworkHandler} instance.
     */
    @NotNull NetworkHandler networkHandler();
}
