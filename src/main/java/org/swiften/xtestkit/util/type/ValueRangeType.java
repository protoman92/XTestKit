package org.swiften.xtestkit.util.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.number.NumberTestUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by haipham on 5/11/17.
 */

/**
 * This interface provides methods to generate a {@link List} of values, based
 * on min/max/step values.
 */
public interface ValueRangeType {
    /**
     * Get a range of {@link V}, based on a min {@link V} and max {@link V}.
     * @param min A {@link V} instance.
     * @param max A {@link V} instance.
     * @param step A {@link V} instance.
     * @param converter A {@link Converter} instance.
     * @param <V> Generics parameter that extends {@link Number}.
     * @return A {@link List} of {@link V}.
     */
    @NotNull
    default <V extends Number> List<V> valueRange(@NotNull V min,
                                                  @NotNull V max,
                                                  @NotNull V step,
                                                  @NotNull Converter<V> converter) {
        List<V> range = new LinkedList<>();

        V current = min;

        while (current.doubleValue() < max.doubleValue()) {
            range.add(current);
            double currentValue = (current.doubleValue() + step.doubleValue());
            current = converter.convert(currentValue);
        }

        return range;
    }

    @FunctionalInterface
    interface Converter<V extends Number> {
        /**
         * Convert a {@link Double} value to {@link V}.
         * @param value A {@link Double} value.
         * @return A {@link V} instance.
         */
        @NotNull
        V convert(double value);
    }
}
