package org.swiften.xtestkit.base.element.action.date;

/**
 * Created by haipham on 5/30/17.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.type.BaseErrorType;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Parameter object for {@link BaseDateActionType#rxa_selectDate(DateType)}.
 */
public class DateParam implements DateType, BaseErrorType {
    /**
     * Get a {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull final List<CalendarUnit> UNITS;
    @Nullable DatePickerType pickerType;
    @Nullable Date date;

    DateParam() {
        UNITS = new LinkedList<>();
    }

    /**
     * @return {@link Date} instance.
     * @see DateType#date()
     * @see ObjectUtil#nonNull(Object)
     * @see #date
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public Date date() {
        if (ObjectUtil.nonNull(date)) {
            return date;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * @return {@link DatePickerType} instance.
     * @see DateType#datePickerType()
     * @see ObjectUtil#nonNull(Object)
     * @see #pickerType
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public DatePickerType datePickerType() {
        if (ObjectUtil.nonNull(pickerType)) {
            return pickerType;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * @return {@link List} of {@link CalendarUnit}.
     * @see DateType#calendarUnits()
     * @see #UNITS
     */
    @NotNull
    @Override
    public List<CalendarUnit> calendarUnits() {
        return UNITS;
    }

    //region Builder
    /**
     * Builder class for {@link DateParam}.
     */
    public static final class Builder {
        @NotNull private final DateParam PARAM;

        Builder() {
            PARAM = new DateParam();
        }

        /**
         * Set the {@link #date} instance.
         * @param date {@link Date} instance.
         * @return The current {@link Builder} instance.
         * @see #date
         */
        @NotNull
        public Builder withDate(@NotNull Date date) {
            PARAM.date = date;
            return this;
        }

        /**
         * Add {@link CalendarUnit} to {@link #UNITS}.
         * @param units {@link List} of {@link CalendarUnit}.
         * @return The current {@link Builder} instance.
         * @see #UNITS
         */
        @NotNull
        public Builder addCalendarUnits(@NotNull List<CalendarUnit> units) {
            PARAM.UNITS.addAll(units);
            return this;
        }

        /**
         * Set the {@link #pickerType} instance.
         * @param pickerType {@link DatePickerType} instance.
         * @return The current {@link Builder} instance.
         * @see #pickerType
         */
        @NotNull
        public Builder withPickerType(@NotNull DatePickerType pickerType) {
            PARAM.pickerType = pickerType;
            return this;
        }

        /**
         * Replace all {@link CalendarUnit} within {@link #UNITS}.
         * @param units {@link List} of {@link CalendarUnit}.
         * @return {@link Builder} instance.
         * @see #UNITS
         */
        @NotNull
        public Builder withCalendarUnits(@NotNull List<CalendarUnit> units) {
            PARAM.UNITS.clear();
            PARAM.UNITS.addAll(units);
            return this;
        }

        /**
         * Set up {@link #UNITS} for date selection.
         * @return The current {@link Builder} instance.
         * @see CalendarUnit#datePickerUnits()
         * @see #withCalendarUnits(List)
         */
        @NotNull
        public Builder withDatePickerUnits() {
            return withCalendarUnits(CalendarUnit.datePickerUnits());
        }

        /**
         * Set the {@link #date} instance and {@link #UNITS}.
         * @param type {@link DateType} instance.
         * @return The current {@link Builder} instance.
         * @see #withCalendarUnits(List)
         * @see #withDate(Date)
         */
        @NotNull
        public Builder withDateType(@NotNull DateType type) {
            return withCalendarUnits(type.calendarUnits()).withDate(type.date());
        }

        /**
         * Get {@link #PARAM}.
         * @return {@link DateParam} instance.
         * @see #PARAM
         */
        @NotNull
        public DateParam build() {
            return PARAM;
        }
    }
    //endregion
}
