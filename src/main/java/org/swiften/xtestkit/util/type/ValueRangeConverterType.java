package org.swiften.xtestkit.util.type;

/**
 * Created by haipham on 5/11/17.
 */

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Similar to {@link ValueRangeType}, but takes a generics parameter and
 * requires a converter to be accessible from a method.
 * @param <V> Generics parameter that extends {@link Number}.
 */
@FunctionalInterface
public interface ValueRangeConverterType<V extends Number> extends ValueRangeType {
    /**
     * Get a {@link org.swiften.xtestkit.util.type.ValueRangeType.Converter}
     * instance to convert {@link Double} to {@link V}.
     * @return A {@link org.swiften.xtestkit.util.type.ValueRangeType.Converter}.
     */
    @NotNull
    Converter<V> converter();

    /**
     * Get a range of {@link V}, based on inclusive min {@link V} and
     * exclusive max {@link V}.
     * @param min A {@link V} instance.
     * @param max A {@link V} instance.
     * @param step A {@link V} instance.
     * @return A {@link List} of {@link V}.
     */
    @NotNull
    default List<V> valueRange(@NotNull V max, @NotNull V min, @NotNull V step) {
        return valueRange(max, min, step, converter());
    }
}
