package org.swiften.xtestkit.mobile.android.element.action.date.type;

/**
 * Created by haipham on 5/23/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.type.DatePickerContainerType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.android.AndroidView;

/**
 * Represents the available types of calendar views.
 */
public enum AndroidDatePickerType implements DatePickerContainerType.DatePickerType {
    CALENDAR,
    SPINNER;

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
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public String stringFormat(@NotNull CalendarUnit unit) {
        switch (unit) {
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
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerContainerType.DatePickerType#pickerListViewXPath(CalendarUnit)
     * @see #xPathBuilder()
     * @see AndroidView.ViewType#LIST_VIEW
     * @see XPath.Builder#ofClass(String)
     */
    @NotNull
    @Override
    public XPath pickerListViewXPath(@NotNull CalendarUnit unit) {
        String cls = AndroidView.ViewType.LIST_VIEW.className();
        return xPathBuilder().ofClass(cls).build();
    }

    /**
     * Get the display view {@link XPath} that corresponds to
     * {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see #dayDisplayViewXPath()
     * @see #monthDisplayViewXPath()
     * @see #yearDisplayViewXPath()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath componentDisplayViewXPath(@NotNull CalendarUnit unit) {
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
     * @see DatePickerContainerType.DatePickerType#targetListViewItemXPath(CalendarUnit)
     * @see XPath.Builder#containsID(String)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public XPath targetListViewItemXPath(@NotNull CalendarUnit unit) {
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
     * @see DatePickerContainerType.DatePickerType#pickerListViewItemXPath(CalendarUnit)
     * @see XPath.Builder#containsID(String)
     * @see AndroidView.ViewType#TEXT_VIEW
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath pickerListViewItemXPath(@NotNull CalendarUnit unit) {
        switch (unit) {
            case YEAR:
                return xPathBuilder()
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
        return xPathBuilder()
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
        return xPathBuilder()
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
        return xPathBuilder()
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
