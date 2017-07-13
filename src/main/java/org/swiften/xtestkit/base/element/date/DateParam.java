package org.swiften.xtestkit.base.element.date;

/**
 * Created by haipham on 5/30/17.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.object.HPObjects;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;

import java.util.*;

/**
 * Parameter object for {@link DateActionType#rxa_selectDate(DateProviderType)}.
 */
public class DateParam implements DateProviderType, ErrorProviderType {
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
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RetryProviderType#retries()
     * @see Constants#DEFAULT_RETRIES
     */
    @Override
    public int retries() {
        return Constants.DEFAULT_RETRIES;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Date} instance.
     * @see DateProviderType#date()
     * @see HPObjects#requireNotNull(Object, String)
     * @see #date
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Date date() {
        HPObjects.requireNotNull(date, NOT_AVAILABLE);
        return date;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link DatePickerType} instance.
     * @see DateProviderType#datePickerType()
     * @see HPObjects#requireNotNull(Object, String)
     * @see #pickerType
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public DatePickerType datePickerType() {
        HPObjects.requireNotNull(pickerType, NOT_AVAILABLE);
        return pickerType;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link List} of {@link CalendarUnit}.
     * @see DateProviderType#units()
     * @see #UNITS
     */
    @NotNull
    @Override
    public List<CalendarUnit> units() {
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
         * @return {@link Builder} instance.
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
         * @return {@link Builder} instance.
         * @see #UNITS
         */
        @NotNull
        public Builder addCalendarUnits(@NotNull List<CalendarUnit> units) {
            PARAM.UNITS.addAll(units);
            return this;
        }

        /**
         * Add {@link CalendarUnit} to {@link #UNITS}.
         * @param units Varargs of {@link CalendarUnit}.
         * @return {@link Builder} instance.
         * @see #addCalendarUnits(List)
         */
        @NotNull
        public Builder addCalendarUnits(@NotNull CalendarUnit...units) {
            return addCalendarUnits(Arrays.asList(units));
        }

        /**
         * Set the {@link #pickerType} instance.
         * @param pickerType {@link DatePickerType} instance.
         * @return {@link Builder} instance.
         * @see #pickerType
         */
        @NotNull
        public Builder withPicker(@NotNull DatePickerType pickerType) {
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
         * Replace all {@link CalendarUnit} within {@link #UNITS}.
         * @param units Varargs of {@link CalendarUnit}.
         * @return {@link Builder} instance.
         * @see #withCalendarUnits(List)
         */
        @NotNull
        public Builder withCalendarUnits(@NotNull CalendarUnit...units) {
            return withCalendarUnits(Arrays.asList(units));
        }

        /**
         * Set up {@link #UNITS} for date selection.
         * @return {@link Builder} instance.
         * @see CalendarUnit#datePickerUnits()
         * @see #withCalendarUnits(List)
         */
        @NotNull
        public Builder withDatePickerUnits() {
            return withCalendarUnits(CalendarUnit.datePickerUnits());
        }

        /**
         * Set the {@link #date} instance and {@link #UNITS}.
         * @param type {@link DateProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withCalendarUnits(List)
         * @see #withDate(Date)
         */
        @NotNull
        public Builder withDateProvider(@NotNull DateProviderType type) {
            return withCalendarUnits(type.units()).withDate(type.date());
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
