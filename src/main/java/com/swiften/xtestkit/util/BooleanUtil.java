package com.swiften.xtestkit.util;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/7/17.
 */
public class BooleanUtil {
    /**
     * Check if a {@link Boolean} value is true.
     * @param value The {@link Boolean} value to be checked.
     * @return A {@link Boolean} value.
     */
    public static boolean isTrue(boolean value) {
        return value;
    }

    /**
     * Check is an {@link Object} is {@link Boolean#TRUE}.
     * @param object An {@link Object} instance.
     * @return A {@link Boolean} value.
     */
    public static boolean isTrue(@NotNull Object object) {
        return object instanceof Boolean && Boolean.class.cast(object);
    }
}
