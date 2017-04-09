package com.swiften.xtestkit.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Created by haipham on 3/19/17.
 */
public class LogUtil {
    public static void println(@Nullable Object object) {
        if (Constants.isLoggingEnabled()) {
            System.out.println(object);
        }
    }

    public static void println(@NotNull Throwable t) {
        if (Constants.isLoggingEnabled()) {
            t.printStackTrace();
        }
    }

    @SafeVarargs
    public static <T> void println(@NotNull T...objects) {
        if (Constants.isLoggingEnabled()) {
            System.out.println(Arrays.toString(objects));
        }
    }

    public static void printf(@NotNull String format, @Nullable Object...object) {
        if (Constants.isLoggingEnabled()) {
            println(String.format(format, object));
        }
    }
}
