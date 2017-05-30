package org.swiften.xtestkit.base.element.action.date;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.property.base.AttributeType;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Use this enum with
 * {@link BaseDateActionType}
 * for better code reuse.
 */
public enum CalendarUnit implements AttributeType<Integer> {
    DAY,
    MONTH,
    YEAR;

    /**
     * Get the {@link CalendarUnit} involved in date selection.
     * @return {@link List} of {@link CalendarUnit}.
     * @see #YEAR
     * @see #MONTH
     * @see #DAY
     */
    @NotNull
    public static List<CalendarUnit> datePickerUnits() {
        return Arrays.asList(YEAR, MONTH, DAY);
    }

    /**
     * Get the {@link Calendar} constant that corresponds to this
     * {@link CalendarUnit}.
     * @return {@link Integer} instance.
     */
    @NotNull
    public Integer value() {
        switch (this) {
            case DAY:
                return Calendar.DAY_OF_MONTH;

            case MONTH:
                return Calendar.MONTH;

            case YEAR:
                return Calendar.YEAR;

            default:
                return 0;
        }
    }
}
