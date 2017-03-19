package com.swiften.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created by haipham on 3/19/17.
 */
public class Log {
    public static void println(@NotNull Object...objects) {
        System.out.println(Arrays.toString(objects));
    }
}
