package org.swiften.xtestkit.ios.element.date;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DatePickerType;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.javautilities.protocol.ClassNameProviderType;
import org.swiften.xtestkitcomponents.xpath.*;

/**
 * Created by haipham on 5/23/17.
 */

/**
 * This {@link Enum} contains different types of date picker for
 * {@link Platform#IOS}.
 */
public enum IOSDatePickerType implements DatePickerType, ErrorProviderType {
    PICKER_WHEEL_MMMM_d_yyyy,
    PICKER_WHEEL_MMMd_h_mm_a;

    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see DatePickerType#valueStringFormat(CalendarUnit)
     * @see CalendarUnit#DAY
     * @see CalendarUnit#HOUR_12
     * @see CalendarUnit#HOUR_24
     * @see CalendarUnit#MONTH
     * @see CalendarUnit#MINUTE
     * @see CalendarUnit#PERIOD
     * @see CalendarUnit#YEAR
     * @see #dayStringFormat()
     * @see #monthStringFormat()
     * @see #yearStringFormat()
     * @see #hour12StringFormat()
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

            case HOUR_12:
                return hour12StringFormat();

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
     * @see #PICKER_WHEEL_MMMM_d_yyyy
     * @see #PICKER_WHEEL_MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String dayStringFormat() {
        switch (this) {
            case PICKER_WHEEL_MMMM_d_yyyy:
                return "d";

            case PICKER_WHEEL_MMMd_h_mm_a:
                return "MMM d";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#MONTH}.
     * @return {@link String} value.
     * @see #PICKER_WHEEL_MMMM_d_yyyy
     * @see #PICKER_WHEEL_MMMd_h_mm_a
     * @see #dayStringFormat()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String monthStringFormat() {
        switch (this) {
            case PICKER_WHEEL_MMMM_d_yyyy:
                return "MMMM";

            case PICKER_WHEEL_MMMd_h_mm_a:
                return dayStringFormat();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#YEAR}.
     * @return {@link String} value.
     * @see #PICKER_WHEEL_MMMM_d_yyyy
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String yearStringFormat() {
        switch (this) {
            case PICKER_WHEEL_MMMM_d_yyyy:
                return "yyyy";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#HOUR_12}.
     * @return {@link String} value.
     * @see #PICKER_WHEEL_MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String hour12StringFormat() {
        switch (this) {
            case PICKER_WHEEL_MMMd_h_mm_a:
                return "h";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#MINUTE}.
     * @return {@link String} value.
     * @see #PICKER_WHEEL_MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String minuteStringFormat() {
        switch (this) {
            case PICKER_WHEEL_MMMd_h_mm_a:
                return "mm";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the {@link String} format that corresponds to
     * {@link CalendarUnit#PERIOD}.
     * @return {@link String} value.
     * @see #PICKER_WHEEL_MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String periodStringFormat() {
        switch (this) {
            case PICKER_WHEEL_MMMd_h_mm_a:
                return "a";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Override this method to provide default implementation.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#pickerViewXP(CalendarUnit)
     * @see CompoundAttribute.Builder#withClass(ClassNameProviderType)
     * @see CompoundAttribute.Builder#withIndex(Integer)
     * @see CompoundAttribute.Builder#withPath(Path)
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     * @see IOSView.Type#UI_PICKER
     * @see IOSView.Type#UNDEFINED
     * @see Path#DIRECT
     * @see #pickerViewIndex(CalendarUnit)
     */
    @NotNull
    @Override
    public XPath pickerViewXP(@NotNull CalendarUnit unit) {
        Attributes attrs = Attributes.of(Platform.IOS);

        return XPath.builder()
            .addAttribute(attrs.ofClass(IOSView.Type.UNDEFINED))
            .addAttribute(CompoundAttribute.builder()
                .withPath(Path.DIRECT)
                .withClass(IOSView.Type.UI_PICKER_WHEEL)
                .withIndex(pickerViewIndex(unit) + 1)
                .build())
            .build();
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
     * Since {@link Platform#IOS} uses {@link IOSView.Type#UI_PICKER_WHEEL}
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
     * Since {@link Platform#IOS} uses {@link IOSView.Type#UI_PICKER_WHEEL}
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
     * @see CalendarUnit#DAY
     * @see CalendarUnit#HOUR_12
     * @see CalendarUnit#HOUR_24
     * @see CalendarUnit#MONTH
     * @see CalendarUnit#MINUTE
     * @see CalendarUnit#PERIOD
     * @see CalendarUnit#YEAR
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

            case HOUR_12:
            case HOUR_24:
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
     * Get the index of {@link IOSView.Type#UI_PICKER_WHEEL} that
     * corresponds to {@link CalendarUnit#DAY}.
     * @return {@link Integer} value.
     * @see #PICKER_WHEEL_MMMM_d_yyyy
     * @see #PICKER_WHEEL_MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    private int dayPickerIndex() {
        switch (this) {
            case PICKER_WHEEL_MMMM_d_yyyy:
                return 1;

            case PICKER_WHEEL_MMMd_h_mm_a:
                /* In this case, the day picker and the month picker share
                 * the same index - they are grouped together into one
                 * compound value */
                return 0;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.Type#UI_PICKER_WHEEL} that
     * corresponds to {@link CalendarUnit#MONTH}.
     * @return {@link Integer} value.
     * @see #PICKER_WHEEL_MMMM_d_yyyy
     * @see #PICKER_WHEEL_MMMd_h_mm_a
     * @see #dayPickerIndex()
     * @see #NOT_AVAILABLE
     */
    private int monthPickerIndex() {
        switch (this) {
            case PICKER_WHEEL_MMMM_d_yyyy:
                return 0;

            case PICKER_WHEEL_MMMd_h_mm_a:
                return dayPickerIndex();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.Type#UI_PICKER_WHEEL} that
     * corresponds to {@link CalendarUnit#YEAR}.
     * @return {@link Integer} value.
     * @see #PICKER_WHEEL_MMMM_d_yyyy
     * @see #NOT_AVAILABLE
     */
    private int yearPickerIndex() {
        switch (this) {
            case PICKER_WHEEL_MMMM_d_yyyy:
                return 2;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.Type#UI_PICKER_WHEEL} that
     * corresponds to {@link CalendarUnit#HOUR_12}.
     * @return {@link Integer} value.
     * @see #PICKER_WHEEL_MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    private int hourPickerIndex() {
        switch (this) {
            case PICKER_WHEEL_MMMd_h_mm_a:
                return 1;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.Type#UI_PICKER_WHEEL} that
     * corresponds to {@link CalendarUnit#MINUTE}.
     * @return {@link Integer} value.
     * @see #PICKER_WHEEL_MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    private int minutePickerIndex() {
        switch (this) {
            case PICKER_WHEEL_MMMd_h_mm_a:
                return 2;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the index of {@link IOSView.Type#UI_PICKER_WHEEL} that
     * corresponds to {@link CalendarUnit#PERIOD}.
     * @return {@link Integer} value.
     * @see #PICKER_WHEEL_MMMd_h_mm_a
     * @see #NOT_AVAILABLE
     */
    private int periodPickerIndex() {
        switch (this) {
            case PICKER_WHEEL_MMMd_h_mm_a:
                return 3;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}