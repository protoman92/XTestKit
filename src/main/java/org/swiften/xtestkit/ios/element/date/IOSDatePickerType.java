package org.swiften.xtestkit.ios.element.date;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DatePickerType;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.base.type.BaseErrorType;
import org.swiften.xtestkit.base.type.BaseViewType;
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
    MMMM_d_YYYY,
    MMMd_h_mm_a;

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see DatePickerType#valueStringFormat(CalendarUnit)
     * @see #dayStringFormat()
     * @see #monthStringFormat()
     * @see #yearStringFormat()
     * @see #hourStringFormat()
     * @see #minuteStringFormat()
     * @see #periodStringFormat()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public String valueStringFormat(@NotNull CalendarUnit unit) {
        switch (unit) {
            case DAY:
                return dayStringFormat();

            case MONTH:
                return monthStringFormat();

            case YEAR:
                return yearStringFormat();

            case HOUR:
                return hourStringFormat();

            case MINUTE:
                return minuteStringFormat();

            case PERIOD:
                return periodStringFormat();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#DAY}.
     * @return {@link String} value.
     * @see #MMMM_d_YYYY
     * @see #MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String dayStringFormat() {
        switch (this) {
            case MMMM_d_YYYY:
                return "d";

            case MMMd_h_mm_a:
                return "MMM d";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#MONTH}.
     * @return {@link String} value.
     * @see #MMMM_d_YYYY
     * @see #MMMd_h_mm_a
     * @see #dayStringFormat()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String monthStringFormat() {
        switch (this) {
            case MMMM_d_YYYY:
                return "MMMM";

            case MMMd_h_mm_a:
                return dayStringFormat();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#YEAR}.
     * @return {@link String} value.
     * @see #MMMM_d_YYYY
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String yearStringFormat() {
        switch (this) {
            case MMMM_d_YYYY:
                return "YYYY";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#HOUR}.
     * @return {@link String} value.
     * @see #MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String hourStringFormat() {
        switch (this) {
            case MMMd_h_mm_a:
                return "h";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#MINUTE}.
     * @return {@link String} value.
     * @see #MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String minuteStringFormat() {
        switch (this) {
            case MMMd_h_mm_a:
                return "mm";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#PERIOD}.
     * @return {@link String} value.
     * @see #MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String periodStringFormat() {
        switch (this) {
            case MMMd_h_mm_a:
                return "a";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#pickerViewXP(CalendarUnit)
     * @see BaseViewType#className()
     * @see Platform#IOS
     * @see IOSView.ViewType#UI_PICKERWHEEL
     * @see XPath.Builder#addClass(String)
     * @see XPath.Builder#setIndex(int)
     * @see #pickerViewIndex(CalendarUnit)
     */
    @NotNull
    @Override
    public XPath pickerViewXP(@NotNull CalendarUnit unit) {
        Platform platform = Platform.IOS;
        String cls = IOSView.ViewType.UI_PICKERWHEEL.className();

        /* Add one because XPath index is 1-based */
        int index = pickerViewIndex(unit) + 1;
        return XPath.builder(platform).addClass(cls).setIndex(index).build();
    }

    /**
     * We reuse {@link #pickerViewXP(CalendarUnit)} because the picker
     * wheel displays the text itself. We can use {@link WebElement#getText()}
     * to extract it.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#unitLabelViewXPath(CalendarUnit)
     * @see #pickerViewXP(CalendarUnit)
     */
    @NotNull
    @Override
    public XPath unitLabelViewXPath(@NotNull CalendarUnit unit) {
        return pickerViewXP(unit);
    }

    /**
     * Since {@link Platform#IOS} uses {@link IOSView.ViewType#UI_PICKERWHEEL}
     * to pick date/time, there is no need to search for individual picker
     * elements.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#targetItemXP(CalendarUnit)
     * @see Platform#IOS
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath targetItemXP(@NotNull CalendarUnit unit) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Since {@link Platform#IOS} uses {@link IOSView.ViewType#UI_PICKERWHEEL}
     * to pick date/time, there is no need to search for individual picker
     * elements.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#pickerItemXP(CalendarUnit)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath pickerItemXP(@NotNull CalendarUnit unit) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Get the index of the picker wheel that corresponds to a
     * {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Integer} value.
     * @see #dayPickerIndex()
     * @see #monthPickerIndex()
     * @see #yearPickerIndex()
     * @see #hourPickerIndex()
     * @see #minutePickerIndex()
     * @see #periodPickerIndex()
     * @see #NOT_AVAILABLE
     */
    private int pickerViewIndex(@NotNull CalendarUnit unit) {
        switch (unit) {
            case DAY:
                return dayPickerIndex();

            case MONTH:
                return monthPickerIndex();

            case YEAR:
                return yearPickerIndex();

            case HOUR:
                return hourPickerIndex();

            case MINUTE:
                return minutePickerIndex();

            case PERIOD:
                return periodPickerIndex();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.ViewType#UI_PICKERWHEEL} that
     * corresponds to {@link CalendarUnit#DAY}.
     * @return {@link Integer} value.
     * @see #MMMM_d_YYYY
     * @see #MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    private int dayPickerIndex() {
        switch (this) {
            case MMMM_d_YYYY:
                return 1;

            case MMMd_h_mm_a:
                /* In this case, the day picker and the month picker share
                 * the same index - they are grouped together into one
                 * compound value */
                return 0;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.ViewType#UI_PICKERWHEEL} that
     * corresponds to {@link CalendarUnit#MONTH}.
     * @return {@link Integer} value.
     * @see #MMMM_d_YYYY
     * @see #MMMd_h_mm_a
     * @see #dayPickerIndex()
     * @see #NOT_AVAILABLE
     */
    private int monthPickerIndex() {
        switch (this) {
            case MMMM_d_YYYY:
                return 0;

            case MMMd_h_mm_a:
                return dayPickerIndex();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.ViewType#UI_PICKERWHEEL} that
     * corresponds to {@link CalendarUnit#YEAR}.
     * @return {@link Integer} value.
     * @see #MMMM_d_YYYY
     * @see #NOT_AVAILABLE
     */
    private int yearPickerIndex() {
        switch (this) {
            case MMMM_d_YYYY:
                return 2;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.ViewType#UI_PICKERWHEEL} that
     * corresponds to {@link CalendarUnit#HOUR}.
     * @return {@link Integer} value.
     * @see #MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    private int hourPickerIndex() {
        switch (this) {
            case MMMd_h_mm_a:
                return 1;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.ViewType#UI_PICKERWHEEL} that
     * corresponds to {@link CalendarUnit#MINUTE}.
     * @return {@link Integer} value.
     * @see #MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    private int minutePickerIndex() {
        switch (this) {
            case MMMd_h_mm_a:
                return 2;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.ViewType#UI_PICKERWHEEL} that
     * corresponds to {@link CalendarUnit#PERIOD}.
     * @return {@link Integer} value.
     * @see #MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    private int periodPickerIndex() {
        switch (this) {
            case MMMd_h_mm_a:
                return 3;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}