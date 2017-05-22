package org.swiften.xtestkit.mobile.android.element.action.date.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.type.DatePickerContainerType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.android.AndroidView;

/**
 * This interface provides date picker view properties for
 * {@link org.swiften.xtestkit.mobile.android.AndroidEngine}.
 */
public interface AndroidDatePickerContainerType extends DatePickerContainerType {
    /**
     * Represents the available types of calendar views.
     */
    enum AndroidDatePickerType implements DatePickerType {
        CALENDAR,
        SPINNER;

        /**
         * @return {@link XPath.Builder} instance.
         * @see Platform#ANDROID
         */
        @NotNull
        public XPath.Builder newXPathBuilder() {
            return XPath.builder(Platform.ANDROID);
        }

        /**
         * Get the {@link String} format for a particular {@link CalendarUnit}.
         * @param element {@link CalendarUnit} instance.
         * @return {@link String} value.
         * @see #dayFormat()
         * @see #monthFormat()
         * @see #yearFormat()
         * @see #NOT_AVAILABLE
         */
        @NotNull
        @Override
        public String stringFormat(@NotNull CalendarUnit element) {
            switch (element) {
                case DAY:
                    return dayFormat();

                case MONTH:
                    return monthFormat();

                case YEAR:
                    return yearFormat();

                default:
                    throw new RuntimeException(NOT_AVAILABLE);
            }
        }

        /**
         * Get the display view {@link XPath} that corresponds to
         * {@link CalendarUnit}.
         * @param element {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         * @see #dayDisplayViewXPath()
         * @see #monthDisplayViewXPath()
         * @see #yearDisplayViewXPath()
         * @see #NOT_AVAILABLE
         */
        @NotNull
        @Override
        public XPath displayViewXPath(@NotNull CalendarUnit element) {
            switch (element) {
                case DAY:
                    return dayDisplayViewXPath();

                case MONTH:
                    return monthDisplayViewXPath();

                case YEAR:
                    return yearDisplayViewXPath();

                default:
                    throw new RuntimeException(NOT_AVAILABLE);
            }
        }

        /**
         * @param element {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         * @see DatePickerContainerType.DatePickerType#pickerViewXPath(CalendarUnit)
         * @see XPath.Builder#containsID(String)
         * @see #NOT_AVAILABLE
         */
        @NotNull
        public XPath pickerViewXPath(@NotNull CalendarUnit element) {
            switch (element) {
                case YEAR:
                    return newXPathBuilder().containsID("month_text_view").build();

                default:
                    throw new RuntimeException(NOT_AVAILABLE);
            }
        }

        /**
         * @param element {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         * @see DatePickerContainerType.DatePickerType#listViewItemXPath(CalendarUnit)
         * @see XPath.Builder#containsID(String)
         * @see AndroidView.ViewType#TEXT_VIEW
         * @see #NOT_AVAILABLE
         */
        @NotNull
        @Override
        public XPath listViewItemXPath(@NotNull CalendarUnit element) {
            switch (element) {
                case YEAR:
                    return newXPathBuilder()
                        .containsID("month_text_view")
                        .ofClass(AndroidView.ViewType.TEXT_VIEW.className())
                        .build();

                default:
                    throw new RuntimeException(NOT_AVAILABLE);
            }
        }

        /**
         * Get the day display view {@link XPath}.
         * @return {@link XPath} instance.
         * @see XPath.Builder#containsID(String)
         * @see XPath.Builder#ofClass(String)
         * @see AndroidView.ViewType#TEXT_VIEW
         */
        @NotNull
        private XPath dayDisplayViewXPath() {
            return newXPathBuilder()
                .containsID("date_picker_day")
                .ofClass(AndroidView.ViewType.TEXT_VIEW.className())
                .build();
        }

        /**
         * Get the month display view {@link XPath}.
         * @return {@link XPath} instance.
         * @see XPath.Builder#containsID(String)
         * @see XPath.Builder#ofClass(String)
         * @see AndroidView.ViewType#TEXT_VIEW
         */
        @NotNull
        private XPath monthDisplayViewXPath() {
            return newXPathBuilder()
                .containsID("date_picker_month")
                .ofClass(AndroidView.ViewType.TEXT_VIEW.className())
                .build();
        }

        /**
         * * Get the year display view {@link XPath}.
         * @return {@link XPath} instance.
         * @see XPath.Builder#containsID(String)
         * @see XPath.Builder#ofClass(String)
         * @see AndroidView.ViewType#TEXT_VIEW
         */
        @NotNull
        private XPath yearDisplayViewXPath() {
            return newXPathBuilder()
                .containsID("date_picker_year")
                .ofClass(AndroidView.ViewType.TEXT_VIEW.className())
                .build();
        }

        /**
         * Get the format the day is formatted in.
         * @return {@link String} value.
         */
        @NotNull
        private String dayFormat() {
            return "d";
        }

        /**
         * Get the format the month is formatted in.
         * @return {@link String} value.
         */
        @NotNull
        private String monthFormat() {
            return "MMM";
        }

        /**
         * Get the format the year is formatted in.
         * @return {@link String} value.
         */
        @NotNull
        private String yearFormat() {
            return "yyyy";
        }
    }

    /**
     * Get the associated {@link AndroidDatePickerType} instance.
     * @return {@link AndroidDatePickerType} instance.
     */
    @NotNull
    @Override
    default AndroidDatePickerType datePickerType() {
        return AndroidDatePickerType.CALENDAR;
    }
}
