package org.swiften.xtestkit.util;

/**
 * Created by haipham on 5/21/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;

/**
 * This interface provides an {@link Engine} instance.
 */
@FunctionalInterface
public interface EngineContainerType {
    /**
     * Get the associated {@link Engine} instance.
     * @return An {@link Engine} instance.
     */
    @NotNull Engine<?> engine();
}
