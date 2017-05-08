package org.swiften.xtestkit.locator.type.base;

/**
 * Created by haipham on 5/7/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This protocol is the base for attribute-based locator operations.
 */
public interface AttributeType<T> {
    /**
     * Get the associated {@link T} value.
     * @return A {@link T} value.
     */
    T value();
}
