package org.swiften.xtestkit.base.element.date;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.property.base.AttributeType;
import org.swiften.xtestkit.base.type.BaseErrorType;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Use this enum with
 * {@link BaseDateActionType}
 * for better code reuse.
 */
public enum CalendarUnit implements AttributeType<Integer>, BaseErrorType {
    DAY,
    MONTH,
    YEAR,
    HOUR,
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
     * Get the {@link CalendarUnit} involved in time selection.
     * @return {@link List} of {@link CalendarUnit}.
     * @see #HOUR
     * @see #MINUTE
     */
    @NotNull
    public static List<CalendarUnit> timePickerUnits() {
        return Arrays.asList(HOUR, MINUTE);
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
     * Check if the current {@link CalendarUnit} is {@link #HOUR}.
     * @return {@link Boolean} value.
     * @see #HOUR
     */
    public boolean isHour() {
        return equals(HOUR);
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

            case HOUR:
                return Calendar.HOUR;

            case MINUTE:
                return Calendar.MINUTE;

            case PERIOD:
                return Calendar.AM_PM;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}
