package org.swiften.xtestkit.android.element.date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.util.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.android.AndroidView;
import org.swiften.xtestkit.android.model.AndroidNumericPickerInputType;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DatePickerType;
import org.swiften.xtestkit.base.element.date.DateProviderType;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.javautilities.protocol.ClassNameProviderType;
import org.swiften.xtestkitcomponents.xpath.AttributeType;
import org.swiften.xtestkitcomponents.xpath.CompoundAttribute;
import org.swiften.xtestkitcomponents.xpath.XPath;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by haipham on 2/6/17.
 */

/**
 * This class is used to wrap
 * {@link org.swiften.xtestkit.base.element.date.CalendarUnit} in order to
 * utilize
 * {@link org.swiften.xtestkit.android.element.choice.AndroidChoiceSelectorType}
 * when we select dates using
 * {@link AndroidDateActionType#rxa_selectDate(DateProviderType)} with
 * {@link AndroidDatePickerType#TIME_NUMBER_PICKER_HH_mm}.
 */
public class UnitNumberPickerWrapper implements
    AndroidNumericPickerInputType,
    ErrorProviderType,
    ChoiceInputType
{
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Nullable private AndroidDatePickerType pickerType;
    @Nullable private CalendarUnit unit;

    private UnitNumberPickerWrapper() {}

    /**
     * Get {@link #pickerType}.
     * @return {@link AndroidDatePickerType} instance.
     * @see ObjectUtil#nonNull(Object)
     * @see #pickerType
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @SuppressWarnings({"WeakerAccess", "ConstantConditions"})
    public AndroidDatePickerType datePickerType() {
        ObjectUtil.requireNotNull(pickerType, NOT_AVAILABLE);
        return pickerType;
    }

    /**
     * Get {@link #unit}.
     * @return {@link CalendarUnit} instance.
     * @see ObjectUtil#nonNull(Object)
     * @see #unit
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @SuppressWarnings({"WeakerAccess", "ConstantConditions"})
    public CalendarUnit calendarUnit() {
        ObjectUtil.requireNotNull(unit, NOT_AVAILABLE);
        return unit;
    }

    /**
     * Override this method to provide default implementation.
     * @param helper {@link InputHelperType} instance.
     * @param value {@link String} value.
     * @return {@link Double} value.
     * @see ChoiceInputType#numericValue(InputHelperType, String)
     * @see CalendarUnit#value()
     * @see DatePickerType#valueStringFormat(CalendarUnit)
     * @see #calendarUnit()
     * @see #datePickerType()
     * @see #NOT_AVAILABLE
     */
    @Override
    @SuppressWarnings("MagicConstant")
    public double numericValue(@NotNull InputHelperType helper,
                               @NotNull String value) {
        DatePickerType pickerType = datePickerType();
        CalendarUnit unit = calendarUnit();
        String format = pickerType.valueStringFormat(unit);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        LogUtil.printft("Formatting %s with %s", value, format);

        try {
            Date date = formatter.parse(value);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int unitValue = unit.value();
            return calendar.get(unitValue);
        } catch (ParseException e) {
            LogUtil.printft("Error parsing %s", value);
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Override this method to provide default implementation.
     * @param helper {@link InputHelperType} instance.
     * @param value {@link Double} value.
     * @return {@link String} value.
     * @see ChoiceInputType#stringValue(InputHelperType, double)
     * @see CalendarUnit#value()
     * @see DatePickerType#valueStringFormat(CalendarUnit)
     * @see #calendarUnit()
     * @see #datePickerType()
     */
    @NotNull
    @Override
    @SuppressWarnings("MagicConstant")
    public String stringValue(@NotNull InputHelperType helper, double value) {
        DatePickerType pickerType = datePickerType();
        CalendarUnit unit = calendarUnit();
        int unitValue = unit.value();
        Calendar calendar = Calendar.getInstance();
        calendar.set(unitValue, (int)value);
        Date date = calendar.getTime();
        String format = pickerType.valueStringFormat(unit);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        LogUtil.printft("Formatting %s with %s", value, format);
        return formatter.format(date);
    }

    /**
     * Override this method to provide default implementation.
     * @param helper {@link InputHelperType} instance.
     * @return {@link XPath} instance.
     * @see ChoiceInputType#choicePickerXP(InputHelperType)
     * @see #androidChoicePickerXP(InputHelperType)
     */
    @NotNull
    @Override
    public XPath choicePickerXP(@NotNull InputHelperType helper) {
        return androidChoicePickerXP(helper);
    }

    /**
     * Override this method to provide default implementation.
     * @param helper {@link InputHelperType} instance.
     * @return {@link XPath} instance.
     * @see ChoiceInputType#choicePickerItemXP(InputHelperType)
     * @see #androidChoicePickerItemXP(InputHelperType)
     */
    @NotNull
    @Override
    public XPath choicePickerItemXP(@NotNull InputHelperType helper) {
        return androidChoicePickerItemXP(helper);
    }

    /**
     * Override this method to use default {@link Platform#ANDROID} index.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     * @see ChoiceInputType#scrollablePickerIndex(InputHelperType)
     * @see #androidScrollablePickerIndex(InputHelperType)
     */
    @Override
    public int scrollablePickerIndex(@NotNull InputHelperType helper) {
        return androidScrollablePickerIndex(helper);
    }

    /**
     * Override this method to provide default implementation.
     * @param helper {@link InputHelperType} instance.
     * @return {@link XPath} instance.
     * @see AndroidNumericPickerInputType#androidChoicePickerParentXP(InputHelperType)
     * @see CompoundAttribute#forClass(ClassNameProviderType)
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see AndroidDatePickerType#DATE_NUMBER_PICKER_MMM_dd_yyyy
     * @see AndroidDatePickerType#TIME_NUMBER_PICKER_HH_mm
     * @see AndroidView.Type#DATE_PICKER
     * @see AndroidView.Type#TIME_PICKER
     * @see Platform#ANDROID
     * @see #datePickerType()
     */
    @NotNull
    @Override
    public XPath androidChoicePickerParentXP(@NotNull InputHelperType helper) {
        switch (datePickerType()) {
            case DATE_NUMBER_PICKER_MMM_dd_yyyy:
                ClassNameProviderType dp = AndroidView.Type.DATE_PICKER;
                CompoundAttribute dpAttr = CompoundAttribute.forClass(dp);
                return XPath.builder().addAttribute(dpAttr).build();

            case TIME_NUMBER_PICKER_HH_mm:
                ClassNameProviderType tp = AndroidView.Type.TIME_PICKER;
                CompoundAttribute tpAttr = CompoundAttribute.forClass(tp);
                return XPath.builder().addAttribute(tpAttr).build();

            default:
                return AndroidNumericPickerInputType.super.androidChoicePickerParentXP(helper);
        }
    }

    /**
     * Override this method to get the picker index for each {@link CalendarUnit}
     * instances, based on {@link #calendarUnit()}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     * @see CalendarUnit#DAY
     * @see CalendarUnit#HOUR_12
     * @see CalendarUnit#HOUR_24
     * @see CalendarUnit#MINUTE
     * @see CalendarUnit#MONTH
     * @see CalendarUnit#YEAR
     * @see #calendarUnit()
     * @see #dayPickerIndex(InputHelperType)
     * @see #hourPickerIndex(InputHelperType)
     * @see #minutePickerIndex(InputHelperType)
     * @see #monthPickerIndex(InputHelperType)
     * @see #yearPickerIndex(InputHelperType)
     * @see #NOT_AVAILABLE
     */
    @Override
    public int androidScrollablePickerIndex(@NotNull InputHelperType helper) {
        switch (calendarUnit()) {
            case YEAR:
                return yearPickerIndex(helper);

            case MONTH:
                return monthPickerIndex(helper);

            case DAY:
                return dayPickerIndex(helper);

            case HOUR_12:
            case HOUR_24:
                return hourPickerIndex(helper);

            case MINUTE:
                return minutePickerIndex(helper);

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the picker index that corresponds to {@link CalendarUnit#YEAR}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     * @see AndroidDatePickerType#DATE_NUMBER_PICKER_MMM_dd_yyyy
     * @see #datePickerType()
     * @see #NOT_AVAILABLE
     */
    private int yearPickerIndex(@NotNull InputHelperType helper) {
        switch (datePickerType()) {
            case DATE_NUMBER_PICKER_MMM_dd_yyyy:
                return 2;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the picker index that corresponds to {@link CalendarUnit#MONTH}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     * @see AndroidDatePickerType#DATE_NUMBER_PICKER_MMM_dd_yyyy
     * @see #datePickerType()
     * @see #NOT_AVAILABLE
     */
    private int monthPickerIndex(@NotNull InputHelperType helper) {
        switch (datePickerType()) {
            case DATE_NUMBER_PICKER_MMM_dd_yyyy:
                return 0;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the picker index that corresponds to {@link CalendarUnit#DAY}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     * @see AndroidDatePickerType#DATE_NUMBER_PICKER_MMM_dd_yyyy
     * @see #datePickerType()
     * @see #NOT_AVAILABLE
     */
    private int dayPickerIndex(@NotNull InputHelperType helper) {
        switch (datePickerType()) {
            case DATE_NUMBER_PICKER_MMM_dd_yyyy:
                return 1;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the picker index that corresponds to {@link CalendarUnit#HOUR_12}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     * @see AndroidDatePickerType#TIME_NUMBER_PICKER_HH_mm
     * @see #datePickerType()
     * @see #NOT_AVAILABLE
     */
    private int hourPickerIndex(@NotNull InputHelperType helper) {
        switch (datePickerType()) {
            case TIME_NUMBER_PICKER_HH_mm:
                return 0;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the picker index that corresponds to {@link CalendarUnit#MINUTE}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     * @see AndroidDatePickerType#TIME_NUMBER_PICKER_HH_mm
     * @see #datePickerType()
     * @see #NOT_AVAILABLE
     */
    private int minutePickerIndex(@NotNull InputHelperType helper) {
        switch (datePickerType()) {
            case TIME_NUMBER_PICKER_HH_mm:
                return 1;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    //region Builder
    /**
     * Builder class for {@link UnitNumberPickerWrapper}.
     */
    public static final class Builder {
        @NotNull private final UnitNumberPickerWrapper WRAPPER;

        Builder() {
            WRAPPER = new UnitNumberPickerWrapper();
        }

        /**
         * Set the {@link #pickerType} instance.
         * @param picker {@link AndroidDatePickerType} instance.
         * @return {@link Builder} instance.
         * @see #pickerType
         */
        @NotNull
        public Builder withDatePicker(@NotNull AndroidDatePickerType picker) {
            WRAPPER.pickerType = picker;
            return this;
        }

        /**
         * Set the {@link #pickerType} instance.
         * @param picker {@link DatePickerType} instance.
         * @return {@link Builder} instance.
         * @see #withDatePicker(AndroidDatePickerType)
         */
        @NotNull
        public Builder withDatePicker(@NotNull DatePickerType picker) {
            if (picker instanceof AndroidDatePickerType) {
                return withDatePicker((AndroidDatePickerType)picker);
            } else {
                return this;
            }
        }

        /**
         * Set the {@link #unit} instance.
         * @param unit {@link CalendarUnit} instance.
         * @return {@link Builder} instance.
         * @see ObjectUtil#requireNotNull(Object, String)
         * @see #unit
         */
        @NotNull
        public Builder withCalendarUnit(@NotNull CalendarUnit unit) {
            WRAPPER.unit = unit;
            return this;
        }

        /**
         * Get {@link #WRAPPER}.
         * @return {@link UnitNumberPickerWrapper} instance.
         * @see #WRAPPER
         */
        @NotNull
        public UnitNumberPickerWrapper build() {
            return WRAPPER;
        }
    }
    //endregion
}
