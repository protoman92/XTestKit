package org.swiften.xtestkit.android.element.date;

/**
 * Created by haipham on 5/23/17.
 */

import org.apache.tools.ant.taskdefs.condition.And;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.AndroidView;
import org.swiften.xtestkit.android.type.AndroidSDK;
import org.swiften.xtestkit.android.type.AndroidSDKProviderType;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DatePickerType;
import org.swiften.xtestkitcomponents.direction.Direction;
import org.swiften.xtestkitcomponents.view.BaseViewType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
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
    CALENDAR,

    /**
     * On {@link AndroidSDK#SDK_23} and above, the calendar is scrolled
     * horizontally. Therefore, we need to use {@link Direction#LEFT_RIGHT}
     * and {@link Direction#RIGHT_LEFT} to navigate it.
     */
    CALENDAR_M,

    /**
     * Only relevant for {@link CalendarUnit#HOUR} and {@link CalendarUnit#MINUTE}.
     */
    HH_mm_TIME_PICKER;

    /**
     * Get the default calendar picker for a particular {@link AndroidSDK}.
     * @param sdk {@link AndroidSDK} instance.
     * @return {@link AndroidDatePickerType} instance.
     * @see #CALENDAR
     * @see #CALENDAR_M
     */
    @NotNull
    public static AndroidDatePickerType calendar(@NotNull AndroidSDK sdk) {
        return sdk.isAtLeastM() ? CALENDAR_M : CALENDAR;
    }

    /**
     * Same as above, but uses {@link AndroidSDKProviderType}.
     * @param type {@link AndroidSDKProviderType} instance.
     * @return {@link AndroidDatePickerType} instance.
     * @see AndroidSDKProviderType#androidSDK()
     * @see #calendar(AndroidSDK)
     */
    @NotNull
    public static AndroidDatePickerType calendar(@NotNull AndroidSDKProviderType type) {
        return calendar(type.androidSDK());
    }

    /**
     * Check if the current {@link AndroidDatePickerType} is calendar-based.
     * @return {@link Boolean} value.
     * @see #CALENDAR
     * @see #CALENDAR_M
     */
    public boolean isCalendar() {
        switch (this) {
            case CALENDAR:
            case CALENDAR_M:
                return true;

            default:
                return false;
        }
    }

    /**
     * Check if the current {@link AndroidDatePickerType} is
     * {@link AndroidView.ViewType#NUMBER_PICKER}-based.
     * @return {@link Boolean} value.
     * @see #HH_mm_TIME_PICKER
     */
    public boolean isNumberPicker() {
        switch (this) {
            case HH_mm_TIME_PICKER:
                return true;

            default:
                return false;
        }
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
     * @see DatePickerType#pickerViewXP(CalendarUnit)
     * @see Attributes#of(PlatformType)
     * @see Attributes#ofClass(String)
     * @see BaseViewType#className()
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see AndroidView.ViewType#LIST_VIEW
     * @see Joiner#OR
     * @see Platform#ANDROID
     * @see #CALENDAR
     * @see #CALENDAR_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath pickerViewXP(@NotNull CalendarUnit unit) {
        switch (this) {
            case CALENDAR:
            case CALENDAR_M:
                Attributes attrs = Attributes.of(Platform.ANDROID);
                String lvc = AndroidView.ViewType.LIST_VIEW.className();
                String vpc = "com.android.internal.widget.ViewPager";

                AttributeBlock block = AttributeBlock.builder()
                    .addAttribute(attrs.ofClass(lvc))
                    .addAttribute(attrs.ofClass(vpc))
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
     * @see #CALENDAR
     * @see #CALENDAR_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath targetItemXP(@NotNull CalendarUnit unit) {
        switch (this) {
            case CALENDAR:
            case CALENDAR_M:
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
     * @see BaseViewType#className()
     * @see CompoundAttribute.Builder#addAttribute(AttributeType)
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     * @see AndroidView.ViewType#TEXT_VIEW
     * @see CalendarUnit#YEAR
     * @see Platform#ANDROID
     * @see #CALENDAR
     * @see #CALENDAR_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public XPath pickerItemXP(@NotNull CalendarUnit unit) {
        switch (this) {
            case CALENDAR:
            case CALENDAR_M:
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
     * @see BaseViewType#className()
     * @see CompoundAttribute.Builder#addAttribute(AttributeType)
     * @see CompoundAttribute.Builder#withClass(String)
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     * @see AndroidView.ViewType#TEXT_VIEW
     * @see Joiner#OR
     * @see Platform#ANDROID
     * @see #CALENDAR
     * @see #CALENDAR_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private XPath dayDisplayViewXP() {
        switch (this) {
            case CALENDAR:
            case CALENDAR_M:
                Attributes attrs = Attributes.of(Platform.ANDROID);

                AttributeBlock block = AttributeBlock.builder()
                    .addAttribute(attrs.containsID("date_picker_day"))
                    .addAttribute(attrs.containsID("date_picker_header_date"))
                    .withJoiner(Joiner.OR)
                    .build();

                CompoundAttribute attribute = CompoundAttribute.builder()
                    .addAttribute(block)
                    .withClass(AndroidView.ViewType.TEXT_VIEW.className())
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
     * @see BaseViewType#className()
     * @see CompoundAttribute.Builder#addAttribute(AttributeType)
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     * @see AndroidView.ViewType#TEXT_VIEW
     * @see Platform#ANDROID
     * @see #CALENDAR
     * @see #CALENDAR_M
     */
    @NotNull
    private XPath monthDisplayViewXPath() {
        switch (this) {
            case CALENDAR:
            case CALENDAR_M:
                Attributes attrs = Attributes.of(Platform.ANDROID);

                AttributeBlock block = AttributeBlock.builder()
                    .addAttribute(attrs.containsID("date_picker_month"))
                    .addAttribute(attrs.containsID("date_picker_header_date"))
                    .withJoiner(Joiner.OR)
                    .build();

                CompoundAttribute attribute = CompoundAttribute.builder()
                    .addAttribute(block)
                    .withClass(AndroidView.ViewType.TEXT_VIEW.className())
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
     * @see BaseViewType#className()
     * @see CompoundAttribute.Builder#addAttribute(AttributeType)
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     * @see AndroidView.ViewType#TEXT_VIEW
     * @see Platform#ANDROID
     * @see #CALENDAR
     * @see #CALENDAR_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private XPath yearDisplayViewXPath() {
        switch (this) {
            case CALENDAR:
            case CALENDAR_M:
                Attributes attrs = Attributes.of(Platform.ANDROID);

                AttributeBlock block = AttributeBlock.builder()
                    .addAttribute(attrs.containsID("date_picker_year"))
                    .addAttribute(attrs.containsID("date_picker_header_year"))
                    .withJoiner(Joiner.OR)
                    .build();

                CompoundAttribute attribute = CompoundAttribute.builder()
                    .addAttribute(block)
                    .withClass(AndroidView.ViewType.TEXT_VIEW.className())
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
     * @see #CALENDAR
     * @see #CALENDAR_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String dayFormat() {
        switch (this) {
            case CALENDAR:
                return "d";

            case CALENDAR_M:
                return "EEE, MMM d";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#MONTH} is formatted in.
     * @return {@link String} value.
     * @see #CALENDAR
     * @see #CALENDAR_M
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String monthFormat() {
        switch (this) {
            case CALENDAR:
                return "MMM";

            case CALENDAR_M:
                return "EEE, MMM d";

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
            case CALENDAR_M:
                return "yyyy";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#HOUR} is formatted in.
     * @return {@link String} value.
     * @see #HH_mm_TIME_PICKER
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String hourFormat() {
        switch (this) {
            case HH_mm_TIME_PICKER:
                return "HH";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the format {@link CalendarUnit#MINUTE} is formatted in.
     * @return {@link String} value.
     * @see #HH_mm_TIME_PICKER
     * @see #NOT_AVAILABLE
     */
    @NotNull
    private String minuteFormat() {
        switch (this) {
            case HH_mm_TIME_PICKER:
                return "mm";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}
