package org.swiften.xtestkit.ios.element.action.date;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.DatePickerType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.BaseErrorType;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 5/23/17.
 */

/**
 * This {@link Enum} contains different types of date picker for
 * {@link Platform#IOS}.
 */
public enum IOSDatePickerType implements DatePickerType, BaseErrorType {
    MMMM_d_YYYY;

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see DatePickerType#stringFormat(CalendarUnit)
     * @see Platform#IOS
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public String stringFormat(@NotNull CalendarUnit unit) {
        switch (unit) {
            case DAY:
                return "d";

            case MONTH:
                return "MMMM";

            case YEAR:
                return "YYYY";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#pickerViewXPath(CalendarUnit)
     * @see Platform#IOS
     * @see IOSView.ViewType#UI_PICKERWHEEL
     * @see XPath.Builder#setClass(String)
     * @see XPath.Builder#setIndex(int)
     * @see #pickerViewIndex(CalendarUnit)
     */
    @NotNull
    @Override
    public XPath pickerViewXPath(@NotNull CalendarUnit unit) {
        Platform platform = Platform.IOS;
        String cls = IOSView.ViewType.UI_PICKERWHEEL.className();

        /* Add one because XPath index is 1-based */
        int index = pickerViewIndex(unit) + 1;
        return XPath.builder(platform).setClass(cls).setIndex(index).build();
    }

    /**
     * We reuse {@link #pickerViewXPath(CalendarUnit)} because the picker
     * wheel displays the text itself. We can use {@link WebElement#getText()}
     * to extract it.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#unitLabelViewXPath(CalendarUnit)
     * @see #pickerViewXPath(CalendarUnit)
     */
    @NotNull
    @Override
    public XPath unitLabelViewXPath(@NotNull CalendarUnit unit) {
        return pickerViewXPath(unit);
    }

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#targetItemXPath(CalendarUnit)
     * @see Platform#IOS
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath targetItemXPath(@NotNull CalendarUnit unit) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#pickerItemXPath(CalendarUnit)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath pickerItemXPath(@NotNull CalendarUnit unit) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Get the index of the picker wheel that corresponds to a
     * {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Integer} value.
     * @see #NOT_AVAILABLE
     */
    private int pickerViewIndex(@NotNull CalendarUnit unit) {
        switch (unit) {
            case DAY:
                return 1;

            case MONTH:
                return 0;

            case YEAR:
                return 2;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}