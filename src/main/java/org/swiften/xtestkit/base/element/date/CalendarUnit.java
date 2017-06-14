package org.swiften.xtestkit.base.element.date;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkitcomponents.property.base.ValueType;
import org.swiften.xtestkitcomponents.common.BaseErrorType;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Use this enum with
 * {@link DateActionType}
 * for better code reuse.
 */
public enum CalendarUnit implements ValueType<Integer>, BaseErrorType {
    DAY,
    MONTH,
    YEAR,
    HOUR_12,
    HOUR_24,
    MINUTE,
    PERIOD;

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
     * Check if the current {@link CalendarUnit} is {@link #DAY}.
     * @return {@link Boolean} value.
     * @see #DAY
     */
    public boolean isDay() {
        return equals(DAY);
    }

    /**
     * Check if the current {@link CalendarUnit} is {@link #MONTH}.
     * @return {@link Boolean} value.
     * @see #MONTH
     */
    public boolean isMonth() {
        return equals(MONTH);
    }

    /**
     * Check if the current {@link CalendarUnit} is {@link #YEAR}.
     * @return {@link Boolean} value.
     * @see #YEAR
     */
    public boolean isYear() {
        return equals(YEAR);
    }

    /**
     * Check if the current {@link CalendarUnit} is {@link #HOUR_12}.
     * @return {@link Boolean} value.
     * @see #HOUR_12
     */
    public boolean isHour() {
        return equals(HOUR_12);
    }

    /**
     * Check if the current {@link CalendarUnit} is {@link #MINUTE}.
     * @return {@link Boolean} value.
     * @see #MINUTE
     */
    public boolean isMinute() {
        return equals(MINUTE);
    }

    /**
     * Check if the current {@link CalendarUnit} is {@link #PERIOD}.
     * @return {@link Boolean} value.
     * @see #PERIOD
     */
    public boolean isPeriod() {
        return equals(PERIOD);
    }

    /**
     * Get the {@link Calendar} constant that corresponds to this
     * {@link CalendarUnit}.
     * @return {@link Integer} instance.
     * @see #DAY
     * @see #HOUR_12
     * @see #HOUR_24
     * @see #MONTH
     * @see #MINUTE
     * @see #PERIOD
     * @see #YEAR
     * @see #NOT_AVAILABLE
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

            case HOUR_12:
                return Calendar.HOUR;

            case HOUR_24:
                return Calendar.HOUR_OF_DAY;

            case MINUTE:
                return Calendar.MINUTE;

            case PERIOD:
                return Calendar.AM_PM;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}
