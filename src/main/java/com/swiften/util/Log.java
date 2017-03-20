package com.swiften.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Created by haipham on 3/19/17.
 */
public class Log {
    public static void println(@Nullable Object object) {
        System.out.println(object);
    }

    @SafeVarargs
    public static <T> void println(@NotNull T...objects) {
        System.out.println(Arrays.toString(objects));
    }
}
