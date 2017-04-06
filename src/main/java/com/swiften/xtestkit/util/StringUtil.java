package com.swiften.xtestkit.util;

import com.google.common.base.Strings;
import org.jetbrains.annotations.Nullable;

/**
 * Created by haipham on 4/6/17.
 */
public class StringUtil {
    /**
     * Check if a {@link String} is not null and not empty.
     * @param text A {@link String} value.
     * @return A {@link Boolean} value.
     */
    public static boolean isNotNullOrEmpty(@Nullable String text) {
        return !Strings.isNullOrEmpty(text);
    }
}
