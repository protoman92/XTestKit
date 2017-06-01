package org.swiften.xtestkit.base.element.general;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This enum provides simplistic directions for certain actions such as
 * swipes.
 */
public enum Unidirection {
    UP_DOWN,
    DOWN_UP,
    LEFT_RIGHT,
    RIGHT_LEFT;

    /**
     * Get {@link Unidirection#UP_DOWN} or {@link Unidirection#DOWN_UP},
     * depending whether top-down is true.
     * @param topDown {@link Boolean} value. If this is true, return
     *                {@link Unidirection#UP_DOWN}, and
     *                {@link Unidirection#DOWN_UP} otherwise.
     * @return {@link Unidirection} instance.
     */
    @NotNull
    public static Unidirection vertical(boolean topDown) {
        return topDown ? UP_DOWN : DOWN_UP;
    }
}
