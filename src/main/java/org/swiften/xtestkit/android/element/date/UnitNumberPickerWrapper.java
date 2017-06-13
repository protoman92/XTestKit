package org.swiften.xtestkit.android.element.date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.android.AndroidView;
import org.swiften.xtestkit.android.model.AndroidNumericPickerInputType;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DatePickerType;
import org.swiften.xtestkit.base.element.date.DateType;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
import org.swiften.xtestkitcomponents.view.BaseViewType;
import org.swiften.xtestkitcomponents.xpath.Attribute;
import org.swiften.xtestkitcomponents.xpath.CompoundAttribute;
import org.swiften.xtestkitcomponents.xpath.XPath;

/**
 * Created by haipham on 2/6/17.
 */

/**
 * This class is used to wrap
 * {@link org.swiften.xtestkit.base.element.date.CalendarUnit} in order to
 * utilize
 * {@link org.swiften.xtestkit.android.element.choice.AndroidChoiceSelectorType}
 * when we select dates using
 * {@link AndroidDateActionType#rxa_selectDate(DateType)} with
 * {@link AndroidDatePickerType#HH_mm_TIME_PICKER}.
 */
public class UnitNumberPickerWrapper implements
    AndroidNumericPickerInputType,
    BaseErrorType, ChoiceInputType
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
    @SuppressWarnings("WeakerAccess")
    public AndroidDatePickerType datePickerType() {
        if (ObjectUtil.nonNull(pickerType)) {
            return pickerType;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get {@link #unit}.
     * @return {@link CalendarUnit} instance.
     * @see ObjectUtil#nonNull(Object)
     * @see #unit
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public CalendarUnit calendarUnit() {
        if (ObjectUtil.nonNull(unit)) {
            return unit;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
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
     * @see AndroidDatePickerType#HH_mm_TIME_PICKER
     * @see AndroidView.ViewType#TIME_PICKER
     * @see BaseViewType#className()
     * @see CompoundAttribute#forClass(String)
     * @see Platform#ANDROID
     * @see XPath.Builder#addAttribute(Attribute)
     * @see #datePickerType()
     */
    @NotNull
    @Override
    public XPath androidChoicePickerParentXP(@NotNull InputHelperType helper) {
        switch (datePickerType()) {
            case HH_mm_TIME_PICKER:
                String cls = AndroidView.ViewType.TIME_PICKER.className();
                CompoundAttribute attribute = CompoundAttribute.forClass(cls);
                return XPath.builder().addAttribute(attribute).build();

            default:
                return AndroidNumericPickerInputType.super.androidChoicePickerParentXP(helper);
        }
    }

    /**
     * Override this method to get the picker index for each {@link CalendarUnit}
     * instances, based on {@link #calendarUnit()}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     * @see CalendarUnit#HOUR
     * @see CalendarUnit#MINUTE
     * @see #calendarUnit()
     * @see #hourPickerIndex(InputHelperType)
     * @see #minutePickerIndex(InputHelperType)
     * @see #NOT_AVAILABLE
     */
    @Override
    public int androidScrollablePickerIndex(@NotNull InputHelperType helper) {
        switch (calendarUnit()) {
            case HOUR:
                return hourPickerIndex(helper);

            case MINUTE:
                return minutePickerIndex(helper);

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the picker index that corresponds to {@link CalendarUnit#HOUR}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     * @see AndroidDatePickerType#HH_mm_TIME_PICKER
     * @see #datePickerType()
     * @see #NOT_AVAILABLE
     */
    private int hourPickerIndex(@NotNull InputHelperType helper) {
        switch (datePickerType()) {
            case HH_mm_TIME_PICKER:
                return 0;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the picker index that corresponds to {@link CalendarUnit#MINUTE}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     * @see AndroidDatePickerType#HH_mm_TIME_PICKER
     * @see #datePickerType()
     * @see #NOT_AVAILABLE
     */
    private int minutePickerIndex(@NotNull InputHelperType helper) {
        switch (datePickerType()) {
            case HH_mm_TIME_PICKER:
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
