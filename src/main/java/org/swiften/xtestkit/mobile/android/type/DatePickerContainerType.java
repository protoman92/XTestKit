package org.swiften.xtestkit.mobile.android.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.CalendarElement;

/**
 * This interface provides date view properties for
 * {@link org.swiften.xtestkit.mobile.android.AndroidEngine}. This
 * helps us hide the implementation for
 * {@link org.swiften.xtestkit.mobile.android.element.action.date.type.AndroidDateActionType}.
 */
public interface DatePickerContainerType {
    /**
     * Represents the available types of calendar views.
     */
    enum DatePickerType {
        CALENDAR,
        SPINNER;

        /**
         * Check if the current {@link DatePickerType} is of type calendar.
         * @return A {@link Boolean} value.
         */
        public boolean isCalendarType() {
            switch (this) {
                case CALENDAR:
                    return true;

                default:
                    return false;
            }
        }

        /**
         * Check if the current {@link DatePickerType} is of type spinner.
         * @return A {@link Boolean} value.
         */
        public boolean isSpinnerType() {
            switch (this) {
                case SPINNER:
                    return true;

                default:
                    return false;
            }
        }

        /**
         * Get the {@link String} format for a particular {@link CalendarElement}.
         * @param element A {@link CalendarElement} instance.
         * @return A {@link String} value.
         */
        @NotNull
        public String stringFormat(@NotNull CalendarElement element) {
            switch (element) {
                case DAY:
                    return dayFormat();

                case MONTH:
                    return monthFormat();

                case YEAR:
                    return yearFormat();

                default:
                    return "";
            }
        }

        /**
         * Get the display view id that corresponds to a {@link CalendarElement}.
         * @param element A {@link CalendarElement} instance.
         * @return A {@link String} value.
         */
        @NotNull
        public String displayViewId(@NotNull CalendarElement element) {
            switch (element) {
                case DAY:
                    return "date_picker_day";

                case MONTH:
                    return "date_picker_month";

                case YEAR:
                    return "date_picker_year";

                default:
                    return "";
            }
        }

        /**
         * Get the picker view id that corresponds to a {@link CalendarElement}.
         * @param element A {@link CalendarElement} instance.
         * @return A {@link String} value.
         */
        @NotNull
        public String pickerViewId(@NotNull CalendarElement element) {
            switch (element) {
                case YEAR:
                    return "month_text_view";

                default:
                    return "";
            }
        }

        /**
         * Get the list view's item id that corresponds to a
         * {@link CalendarElement}.
         * @param element A {@link CalendarElement} instance.
         * @return A {@link String} value.
         */
        @NotNull
        public String listViewItemId(@NotNull CalendarElement element) {
            switch (element) {
                case YEAR:
                    return "month_text_view";

                default:
                    return "";
            }
        }

        /**
         * Get the format the day is formatted in.
         * @return A {@link String} value.
         */
        @NotNull
        private String dayFormat() {
            return "d";
        }

        /**
         * Get the format the month is formatted in.
         * @return A {@link String} value.
         */
        @NotNull
        private String monthFormat() {
            return "MMM";
        }

        /**
         * Get the format the year is formatted in.
         * @return A {@link String} value.
         */
        @NotNull
        private String yearFormat() {
            return "yyyy";
        }
    }

    /**
     * Get the associated {@link DatePickerType} instance.
     * @return A {@link DatePickerType} instance.
     */
    @NotNull
    default DatePickerType datePickerViewType() {
        return DatePickerType.CALENDAR;
    }
}
