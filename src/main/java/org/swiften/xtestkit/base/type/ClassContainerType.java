package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides a class name.
 */
@FunctionalInterface
public interface ClassContainerType {
    /**
     * Get the associated class name.
     * @return {@link String} value.
     */
    @NotNull String className();
}
