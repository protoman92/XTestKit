package org.swiften.xtestkit.base.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.general.Unidirection;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides {@link Unidirection} for certain actions, such as
 * swipes.
 */
@FunctionalInterface
public interface UnidirectionType {
    /**
     * Get the associated {@link Unidirection}.
     * @return {@link Unidirection} instance.
     */
    @NotNull
    Unidirection direction();
}