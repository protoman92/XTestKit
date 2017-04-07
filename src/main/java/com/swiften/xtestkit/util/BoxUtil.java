package com.swiften.xtestkit.util;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/7/17.
 */
public class BoxUtil {
    /**
     * Box an Array of {@link Integer}.
     * @param integers An Array of {@link Integer} primitives.
     * @return An Array of boxed {@link Integer}.
     */
    @NotNull
    public static Integer[] box(@NotNull int[] integers) {
        int length = integers.length;
        Integer[] boxed = new Integer[length];

        for (int i = 0; i < length; i++) {
            boxed[i] = integers[i];
        }

        return boxed;
    }
}
