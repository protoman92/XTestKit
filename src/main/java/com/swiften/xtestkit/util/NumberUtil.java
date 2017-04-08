package com.swiften.xtestkit.util;

/**
 * Created by haipham on 4/9/17.
 */
public class NumberUtil {
    /**
     * Check if a number is even.
     * @param number An {@link Integer} value.
     * @return A {@link Boolean} value.
     */
    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    /**
     * Check if a number is odd.
     * @param number An {@link Integer} value.
     * @return A {@link Boolean} value.
     */
    public static boolean isOdd(int number) {
        return !isEven(number);
    }
}
