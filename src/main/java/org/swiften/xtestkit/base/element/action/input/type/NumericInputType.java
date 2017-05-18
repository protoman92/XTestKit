package org.swiften.xtestkit.base.element.action.input.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.collection.CollectionTestUtil;
import org.swiften.javautilities.collection.CollectionUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.util.type.ValueRangeConverterType;

import java.util.List;
import java.util.Optional;

/**
 * Created by haipham on 5/12/17.
 */

/**
 * This interface provides methods to convert between numeric input values
 * and their {@link String} equivalents.
 */
public interface NumericInputType extends InputType, ValueRangeConverterType<Double> {
    @NotNull
    @Override
    default Converter<Double> converter() {
        return a -> Double.valueOf(String.format("%.2f", a));
    }

    /**
     * Convert a {@link String} value into a numeric value.
     * @param value A {@link Double} value.
     * @return A {@link Double} value.
     */
    default double numericValue(@NotNull String value) {
        return Double.valueOf(value);
    }

    /**
     * Get the numeric value's {@link String} representation.
     * @param value A {@link Double} value.
     * @return A {@link String} value.
     */
    @NotNull
    default String stringValue(double value) {
        return String.valueOf(value);
    }

    /**
     * Get the minimum numeric value that can be selected.
     * @return A {@link Double} value.
     */
    double minSelectableNumericValue();

    /**
     * Get the maximum numeric value that can be selected.
     * @return A {@link Double} value.
     */
    double maxSelectableNumericValue();

    /**
     * Get the difference between two consecutive numeric values.
     * @return A {@link Double} value.
     */
    default double selectableNumericValueStep() {
        return 1;
    }

    /**
     * Limit the number of elements within a {@link List} of numeric range
     * return by {@link #selectableNumericRange()}.
     * @return An {@link Integer} value.
     */
    @NotNull
    default Optional<Integer> numericRangeLimit() {
        return Optional.of(20);
    }

    /**
     * Get the selectable numeric range.
     * @return A {@link List} of {@link Double}.
     * @see #numericRangeLimit()
     * @see #valueRange(Number, Number, Number)
     * @see #minSelectableNumericValue()
     * @see #maxSelectableNumericValue()
     * @see #selectableNumericValueStep()
     */
    @NotNull
    default List<Double> selectableNumericRange() {
        List<Double> values = valueRange(
            minSelectableNumericValue(),
            maxSelectableNumericValue(),
            selectableNumericValueStep()
        );

        Optional<Integer> optional = numericRangeLimit();
        int limit = optional.orElseGet(values::size);
        return CollectionUtil.subList(values, 0, limit);
    }

    /**
     * Get an error message format that can be used with
     * {@link #minSelectableNumericValue()} and
     * {@link #maxSelectableNumericValue()} to create an error message.
     * @return A {@link String} value.
     */
    @NotNull
    String emptyInputErrorFormat();

    /**
     * Get a random numeric value from {@link #selectableNumericRange()}.
     * @return A {@link Double} value.
     * @see #selectableNumericRange()
     */
    default double randomNumericValue() {
        List<Double> selectableRange = selectableNumericRange();
        return CollectionTestUtil.randomElement(selectableRange);
    }
}
