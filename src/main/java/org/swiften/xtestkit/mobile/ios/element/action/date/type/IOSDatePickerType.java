package org.swiften.xtestkit.mobile.ios.element.action.date.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.type.DatePickerContainerType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.ios.element.locator.xpath.IOSXPath;

/**
 * Created by haipham on 5/23/17.
 */
public enum IOSDatePickerType implements DatePickerContainerType.DatePickerType {
    BASIC;

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see DatePickerContainerType.DatePickerType#stringFormat(CalendarUnit)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public String stringFormat(@NotNull CalendarUnit unit) {
        switch (unit) {
            case DAY:
                return "dd";

            case MONTH:
                return "MMM";

            case YEAR:
                return "YYYY";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerContainerType.DatePickerType#pickerListViewXPath(CalendarUnit)
     */
    @NotNull
    @Override
    public XPath pickerListViewXPath(@NotNull CalendarUnit unit) {
        return IOSXPath.builder().containsText("Date Picker").build();
    }

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerContainerType.DatePickerType#componentDisplayViewXPath(CalendarUnit)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath componentDisplayViewXPath(@NotNull CalendarUnit unit) {
        return IOSXPath.builder().build();
    }

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerContainerType.DatePickerType#targetListViewItemXPath(CalendarUnit)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath targetListViewItemXPath(@NotNull CalendarUnit unit) {
        return IOSXPath.builder().build();
    }

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerContainerType.DatePickerType#pickerListViewItemXPath(CalendarUnit)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath pickerListViewItemXPath(@NotNull CalendarUnit unit) {
        return IOSXPath.builder().build();
    }

    /**
     * Get the picker wheel {@link XPath} that corresponds to a
     * {@link CalendarUnit} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see XPath.Builder#ofClass(String)
     * @see XPath.Builder#atIndex(int)
     */
    @NotNull
    private XPath pickerWheelXPath(@NotNull CalendarUnit unit) {
        return IOSXPath.builder().ofClass("XCUIElementTypePickerWheel").build();
    }
}