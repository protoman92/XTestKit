package org.swiften.xtestkit.android.element.date;

/**
 * Created by haipham on 5/23/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.AndroidView;
import org.swiften.xtestkit.android.type.AndroidSDK;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DatePickerType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
import org.swiften.javautilities.protocol.ClassNameProviderType;
import org.swiften.xtestkitcomponents.direction.Direction;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.xpath.*;

/**
 * Represents the available types of calendar views for {@link Platform#ANDROID}.
 */
public enum AndroidDatePickerType implements DatePickerType, BaseErrorType {
    /**
     * On {@link AndroidSDK#SDK_22} and below, the calendar is scrolled
     * vertically. Therefore, we need to use {@link Direction#UP_DOWN} and
     * {@link Direction#DOWN_UP} to navigate it.
     */
    DATE_CALENDAR_PICKER,

    /**
     * On {@link AndroidSDK#SDK_23} and above, the calendar is scrolled
     * horizontally. Therefore, we need to use {@link Direction#LEFT_RIGHT}
     * and {@link Direction#RIGHT_LEFT} to navigate it.
     */
    DATE_CALENDAR_PICKER_M,

    /**
     * Uses {@link AndroidView.Type#NUMBER_PICKER}. This is the default
     * mode if not {@link AndroidSDK#isAtLeastLollipop()}.
     */
    DATE_NUMBER_PICKER_MMM_dd_yyyy,

    /**
     * Uses {@link AndroidView.Type#NUMBER_PICKER}.
     */
    TIME_NUMBER_PICKER_HH_mm;

    /**
     * Check if the current {@link AndroidDatePickerType} is calendar-based.
     * @return {@link Boolean} value.
     * @see #DATE_CALENDAR_PICKER
     * @see #DATE_CALENDAR_PICKER_M
     */
    public boolean isCalendar() {
        switch (this) {
            case DATE_CALENDAR_PICKER:
            case DATE_CALENDAR_PICKER_M:
                return true;

            default:
                return false;
        }
    }

    /**
     * Check if the current {@link AndroidDatePickerType} is
     * {@link AndroidView.Type#NUMBER_PICKER}-based.
     * @return {@link Boolean} value.
     * @see #TIME_NUMBER_PICKER_HH_mm
     */
    public boolean isNumberPicker() {
        switch (this) {
            case DATE_NUMBER_PICKER_MMM_dd_yyyy:
            case TIME_NUMBER_PICKER_HH_mm:
                return true;

            default:
                return false;
        }
    }

    /**
     * Get the {@link String} format for a particular {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see CalendarUnit#DAY
     * @see CalendarUnit#HOUR_12
     * @see CalendarUnit#HOUR_24
     * @see CalendarUnit#MONTH
     * @see CalendarUnit#MINUTE
     * @see CalendarUnit#YEAR
     * @see #dayFormat()
     * @see #monthFormat()
     * @see #yearFormat()
     * @see #hour24Format()
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

            case HOUR_24:
                return hour24Format();

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
     * @see DatePickerType#pickerViewXP(CalendarUnit)
     * @see Attributes#of(PlatformType)
     * @see Attributes#ofClass(String)
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see AndroidView.Type#LIST_VIEW
     * @see AndroidView.Type#VIEW_PAGER_I
     * @see Joiner#OR
     * @see Platform#ANDROID
     * @see #DATE_CALENDAR_PICKER
     * @see #DATE_CALENDAR_PICKER_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath pickerViewXP(@NotNull CalendarUnit unit) {
        switch (this) {
            case DATE_CALENDAR_PICKER:
            case DATE_CALENDAR_PICKER_M:
                Attributes attrs = Attributes.of(Platform.ANDROID);

                AttributeBlock block = AttributeBlock.builder()
                    .addAttribute(attrs.ofClass(AndroidView.Type.LIST_VIEW))
                    .addAttribute(attrs.ofClass(AndroidView.Type.VIEW_PAGER_I))
                    .withJoiner(Joiner.OR)
                    .build();

                return XPath.builder().addAttribute(block).build();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the display view {@link XPath} that corresponds to {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see CalendarUnit#DAY
     * @see CalendarUnit#MONTH
     * @see CalendarUnit#YEAR
     * @see #dayDisplayViewXP()
     * @see #monthDisplayViewXPath()
     * @see #yearDisplayViewXPath()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath unitLabelViewXPath(@NotNull CalendarUnit unit) {
        switch (unit) {
            case DAY:
                return dayDisplayViewXP();

            case MONTH:
                return monthDisplayViewXPath();

            case YEAR:
                return yearDisplayViewXPath();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Override this method to provide default implementation.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#targetItemXP(CalendarUnit)
     * @see AttributeBlock.Builder#addAttribute(AttributeType)
     * @see AttributeBlock.Builder#withJoiner(Joiner)
     * @see Attributes#of(PlatformType)
     * @see Attributes#containsID(String)
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see CalendarUnit#YEAR
     * @see Joiner#OR
     * @see Platform#ANDROID
     * @see #DATE_CALENDAR_PICKER
     * @see #DATE_CALENDAR_PICKER_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath targetItemXP(@NotNull CalendarUnit unit) {
        switch (this) {
            case DATE_CALENDAR_PICKER:
            case DATE_CALENDAR_PICKER_M:
                switch (unit) {
                    case YEAR:
                        Attributes attrs = Attributes.of(Platform.ANDROID);

                        AttributeBlock block = AttributeBlock.builder()
                            .addAttribute(attrs.containsID("month_text_view"))
                            .addAttribute(attrs.containsID("text1"))
                            .withJoiner(Joiner.OR)
                            .build();

                        return XPath.builder().addAttribute(block).build();

                    default:
                        break;
                }

            default:
                break;
        }

        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Override this method to provide default implementation.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link XPath} instance.
     * @see DatePickerType#pickerItemXP(CalendarUnit)
     * @see AttributeBlock.Builder#addAttribute(AttributeType)
     * @see AttributeBlock.Builder#withJoiner(Joiner)
     * @see Attributes#containsID(String)
     * @see Attributes#of(PlatformType)
     * @see Attributes#ofClass(String)
     * @see CompoundAttribute.Builder#addAttribute(AttributeType)
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     * @see AndroidView.Type#TEXT_VIEW
     * @see CalendarUnit#YEAR
     * @see Platform#ANDROID
     * @see #DATE_CALENDAR_PICKER
     * @see #DATE_CALENDAR_PICKER_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath pickerItemXP(@NotNull CalendarUnit unit) {
        switch (this) {
            case DATE_CALENDAR_PICKER:
            case DATE_CALENDAR_PICKER_M:
                switch (unit) {
                    case YEAR:
                        Attributes attrs = Attributes.of(Platform.ANDROID);

                        AttributeBlock block = AttributeBlock.builder()
                            .addAttribute(attrs.containsID("month_text_view"))
                            .addAttribute(attrs.containsID("text1"))
                            .withJoiner(Joiner.OR)
                            .build();

                        return XPath.builder().addAttribute(block).build();

                    default:
                        break;
                }

            default:
                break;
        }

        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Get the day display view {@link XPath}.
     * @return {@link XPath} instance.
     * @see AttributeBlock.Builder#addAttribute(AttributeType)
     * @see AttributeBlock.Builder#withJoiner(Joiner)
     * @see Attributes#containsID(String)
     * @see Attributes#of(PlatformType)
     * @see Attributes#ofClass(String)
     * @see CompoundAttribute.Builder#addAttribute(AttributeType)
     * @see CompoundAttribute.Builder#withClass(ClassNameProviderType)
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     * @see AndroidView.Type#TEXT_VIEW
     * @see Joiner#OR
     * @see Platform#ANDROID
     * @see #DATE_CALENDAR_PICKER
     * @see #DATE_CALENDAR_PICKER_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private XPath dayDisplayViewXP() {
        switch (this) {
            case DATE_CALENDAR_PICKER:
            case DATE_CALENDAR_PICKER_M:
                Attributes attrs = Attributes.of(Platform.ANDROID);

                AttributeBlock block = AttributeBlock.builder()
                    .addAttribute(attrs.containsID("date_picker_day"))
                    .addAttribute(attrs.containsID("date_picker_header_date"))
                    .withJoiner(Joiner.OR)
                    .build();

                CompoundAttribute attribute = CompoundAttribute.builder()
                    .addAttribute(block)
                    .withClass(AndroidView.Type.TEXT_VIEW)
                    .build();

                return XPath.builder().addAttribute(attribute).build();

            default:
                break;
        }

        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Get the month display view {@link XPath}.
     * @return {@link XPath} instance.
     * @see AttributeBlock.Builder#addAttribute(AttributeType)
     * @see AttributeBlock.Builder#withJoiner(Joiner)
     * @see Attributes#containsID(String)
     * @see Attributes#of(PlatformType)
     * @see Attributes#ofClass(String)
     * @see CompoundAttribute.Builder#addAttribute(AttributeType)
     * @see CompoundAttribute.Builder#withClass(ClassNameProviderType)
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     * @see AndroidView.Type#TEXT_VIEW
     * @see Platform#ANDROID
     * @see #DATE_CALENDAR_PICKER
     * @see #DATE_CALENDAR_PICKER_M
     */
    @NotNull
    private XPath monthDisplayViewXPath() {
        switch (this) {
            case DATE_CALENDAR_PICKER:
            case DATE_CALENDAR_PICKER_M:
                Attributes attrs = Attributes.of(Platform.ANDROID);

                AttributeBlock block = AttributeBlock.builder()
                    .addAttribute(attrs.containsID("date_picker_month"))
                    .addAttribute(attrs.containsID("date_picker_header_date"))
                    .withJoiner(Joiner.OR)
                    .build();

                CompoundAttribute attribute = CompoundAttribute.builder()
                    .addAttribute(block)
                    .withClass(AndroidView.Type.TEXT_VIEW)
                    .build();

                return XPath.builder().addAttribute(attribute).build();

            default:
                break;
        }

        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Get the year display view {@link XPath}.
     * @return {@link XPath} instance.
     * @see AttributeBlock.Builder#addAttribute(AttributeType)
     * @see AttributeBlock.Builder#withJoiner(Joiner)
     * @see Attributes#containsID(String)
     * @see Attributes#of(PlatformType)
     * @see Attributes#ofClass(String)
     * @see CompoundAttribute.Builder#addAttribute(AttributeType)
     * @see CompoundAttribute.Builder#withClass(ClassNameProviderType)
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     * @see AndroidView.Type#TEXT_VIEW
     * @see Platform#ANDROID
     * @see #DATE_CALENDAR_PICKER
     * @see #DATE_CALENDAR_PICKER_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private XPath yearDisplayViewXPath() {
        switch (this) {
            case DATE_CALENDAR_PICKER:
            case DATE_CALENDAR_PICKER_M:
                Attributes attrs = Attributes.of(Platform.ANDROID);

                AttributeBlock block = AttributeBlock.builder()
                    .addAttribute(attrs.containsID("date_picker_year"))
                    .addAttribute(attrs.containsID("date_picker_header_year"))
                    .withJoiner(Joiner.OR)
                    .build();

                CompoundAttribute attribute = CompoundAttribute.builder()
                    .addAttribute(block)
                    .withClass(AndroidView.Type.TEXT_VIEW)
                    .build();

                return XPath.builder().addAttribute(attribute).build();

            default:
                break;
        }

        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Get the format {@link CalendarUnit#DAY} is formatted in.
     * @return {@link String} value.
     * @see #DATE_CALENDAR_PICKER
     * @see #DATE_CALENDAR_PICKER_M
     * @see #DATE_NUMBER_PICKER_MMM_dd_yyyy
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String dayFormat() {
        switch (this) {
            case DATE_CALENDAR_PICKER:
                return "d";

            case DATE_CALENDAR_PICKER_M:
                return "EEE, MMM d";

            case DATE_NUMBER_PICKER_MMM_dd_yyyy:
                return "dd";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#MONTH} is formatted in.
     * @return {@link String} value.
     * @see #DATE_CALENDAR_PICKER
     * @see #DATE_CALENDAR_PICKER_M
     * @see #DATE_NUMBER_PICKER_MMM_dd_yyyy
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String monthFormat() {
        switch (this) {
            case DATE_CALENDAR_PICKER:
                return "MMM";

            case DATE_CALENDAR_PICKER_M:
                return "EEE, MMM d";

            case DATE_NUMBER_PICKER_MMM_dd_yyyy:
                return "MMM";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#YEAR} is formatted in.
     * @return {@link String} value.
     * @see #DATE_CALENDAR_PICKER
     * @see #DATE_CALENDAR_PICKER_M
     * @see #DATE_NUMBER_PICKER_MMM_dd_yyyy
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String yearFormat() {
        switch (this) {
            case DATE_CALENDAR_PICKER:
            case DATE_CALENDAR_PICKER_M:
            case DATE_NUMBER_PICKER_MMM_dd_yyyy:
                return "yyyy";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#HOUR_24} is formatted in.
     * @return {@link String} value.
     * @see #TIME_NUMBER_PICKER_HH_mm
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String hour24Format() {
        switch (this) {
            case TIME_NUMBER_PICKER_HH_mm:
                return "HH";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#MINUTE} is formatted in.
     * @return {@link String} value.
     * @see #TIME_NUMBER_PICKER_HH_mm
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String minuteFormat() {
        switch (this) {
            case TIME_NUMBER_PICKER_HH_mm:
                return "mm";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}
