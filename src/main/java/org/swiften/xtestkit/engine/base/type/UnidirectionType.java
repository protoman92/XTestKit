package org.swiften.xtestkit.engine.base.type;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides {@link Unidirection} for certain actions, such as
 * swipes.
 */
@FunctionalInterface
public interface UnidirectionType {
    enum Unidirection {
        UP_DOWN,
        DOWN_UP,
        LEFT_RIGHT,
        RIGHT_LEFT;
    }

    /**
     * Get the associated {@link Unidirection}.
     * @return A {@link Unidirection} instance.
     */
    @NotNull
    Unidirection direction();
}
