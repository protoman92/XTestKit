package org.swiften.xtestkit.android.element.date;

/**
 * Created by haipham on 5/23/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DatePickerType;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.base.type.BaseErrorType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.android.AndroidView;

/**
 * Represents the available types of calendar views for {@link Platform#ANDROID}.
 */
public enum AndroidDatePickerType implements DatePickerType, BaseErrorType {
    /**
     * On {@link org.swiften.xtestkit.android.type.AndroidVersion#SDK_22} and
     * below, the calendar is scrolled vertically. Therefore, we need to use
     * {@link org.swiften.xtestkit.base.element.general.Unidirection#UP_DOWN}
     * and {@link org.swiften.xtestkit.base.element.general.Unidirection#DOWN_UP}
     * to navigate it.
     *
     * On {@link org.swiften.xtestkit.android.type.AndroidVersion#SDK_23} and
     * above, the calendar is scrolled horizontally. Therefore, we need to use
     * {@link org.swiften.xtestkit.base.element.general.Unidirection#LEFT_RIGHT}
     * and {@link org.swiften.xtestkit.base.element.general.Unidirection#RIGHT_LEFT}
     * to navigate it.
     */
    CALENDAR,

    /**
     * This is only relevant for {@link CalendarUnit#HOUR} and
     * {@link CalendarUnit#MINUTE}.
     */
    hh_mm_SPINNER;

    /**
     * Check if the current {@link AndroidDatePickerType} is calendar-based.
     * @return {@link Boolean} value.
     * @see #CALENDAR
     */
    public boolean isCalendarMode() {
        switch (this) {
            case CALENDAR:
                return true;

            default:
                return false;
        }
    }

    /**
     * @return {@link XPath.Builder} instance.
     * @see Platform#ANDROID
     */
    @NotNull
    public XPath.Builder xPathBuilder() {
        return XPath.builder(Platform.ANDROID);
    }

    /**
     * Get the {@link String} format for a particular {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see #dayFormat()
     * @see #monthFormat()
     * @see #yearFormat()
     * @see #hourFormat()
     * @see #minuteFormat()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public String valueStringFormat(@NotNull CalendarUnit unit) {
        switch (unit) {
            case DAY:
                return dayFormat();

            case MONTH:
                return monthFormat();

            case YEAR:
                return yearFormat();

            case HOUR:
                return hourFormat();

            case MINUTE:
                return minuteFormat();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Override this method to provide default implementation.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#pickerViewXPath(CalendarUnit)
     * @see AndroidView.ViewType#LISTVIEW
     * @see XPath.Builder#ofClass(String)
     * @see #CALENDAR
     * @see #xPathBuilder()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath pickerViewXPath(@NotNull CalendarUnit unit) {
        switch (this) {
            case CALENDAR:
                String cls = AndroidView.ViewType.LISTVIEW.className();
                return xPathBuilder().ofClass(cls).build();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the display view {@link XPath} that corresponds to {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see #dayDisplayViewXPath()
     * @see #monthDisplayViewXPath()
     * @see #yearDisplayViewXPath()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath unitLabelViewXPath(@NotNull CalendarUnit unit) {
        switch (unit) {
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
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see #xPathBuilder()
     * @see DatePickerType#targetItemXPath(CalendarUnit)
     * @see XPath.Builder#containsID(String)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public XPath targetItemXPath(@NotNull CalendarUnit unit) {
        switch (unit) {
            case YEAR:
                return xPathBuilder().containsID("month_text_view").build();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#pickerItemXPath(CalendarUnit)
     * @see XPath.Builder#containsID(String)
     * @see AndroidView.ViewType#TEXTVIEW
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath pickerItemXPath(@NotNull CalendarUnit unit) {
        switch (unit) {
            case YEAR:
                return xPathBuilder()
                    .containsID("month_text_view")
                    .ofClass(AndroidView.ViewType.TEXTVIEW.className())
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
     * @see AndroidView.ViewType#TEXTVIEW
     */
    @NotNull
    private XPath dayDisplayViewXPath() {
        return xPathBuilder()
            .containsID("date_picker_day")
            .ofClass(AndroidView.ViewType.TEXTVIEW.className())
            .build();
    }

    /**
     * Get the month display view {@link XPath}.
     * @return {@link XPath} instance.
     * @see XPath.Builder#containsID(String)
     * @see XPath.Builder#ofClass(String)
     * @see AndroidView.ViewType#TEXTVIEW
     */
    @NotNull
    private XPath monthDisplayViewXPath() {
        return xPathBuilder()
            .containsID("date_picker_month")
            .ofClass(AndroidView.ViewType.TEXTVIEW.className())
            .build();
    }

    /**
     * * Get the year display view {@link XPath}.
     * @return {@link XPath} instance.
     * @see XPath.Builder#containsID(String)
     * @see XPath.Builder#ofClass(String)
     * @see AndroidView.ViewType#TEXTVIEW
     */
    @NotNull
    private XPath yearDisplayViewXPath() {
        return xPathBuilder()
            .containsID("date_picker_year")
            .ofClass(AndroidView.ViewType.TEXTVIEW.className())
            .build();
    }

    /**
     * Get the format {@link CalendarUnit#DAY} is formatted in.
     * @return {@link String} value.
     * @see #CALENDAR
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String dayFormat() {
        switch (this) {
            case CALENDAR:
                return "d";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#MONTH} is formatted in.
     * @return {@link String} value.
     * @see #CALENDAR
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String monthFormat() {
        switch (this) {
            case CALENDAR:
                return "MMM";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#YEAR} is formatted in.
     * @return {@link String} value.
     * @see #CALENDAR
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String yearFormat() {
        switch (this) {
            case CALENDAR:
                return "yyyy";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#HOUR} is formatted in.
     * @return {@link String} value.
     * @see #hh_mm_SPINNER
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String hourFormat() {
        switch (this) {
            case hh_mm_SPINNER:
                return "hh";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#MINUTE} is formatted in.
     * @return {@link String} value.
     * @see #hh_mm_SPINNER
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String minuteFormat() {
        switch (this) {
            case hh_mm_SPINNER:
                return "mm";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}
