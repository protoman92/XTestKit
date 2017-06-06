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
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.xpath.Attribute;
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
 * {@link AndroidDatePickerType#hh_mm_TIMEPICKER}.
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
     * @param platform {@link PlatformType} instance.
     * @return {@link XPath} instance.
     * @see ChoiceInputType#choicePickerXP(PlatformType)
     * @see #androidChoicePickerXP()
     */
    @NotNull
    @Override
    public XPath choicePickerXP(@NotNull PlatformType platform) {
        return androidChoicePickerXP();
    }

    /**
     * Override this method to provide default implementation.
     * @param platform {@link PlatformType} instance.
     * @return {@link XPath} instance.
     * @see ChoiceInputType#choicePickerItemXP(PlatformType)
     * @see #androidChoicePickerItemXP()
     */
    @NotNull
    @Override
    public XPath choicePickerItemXP(@NotNull PlatformType platform) {
        return androidChoicePickerItemXP();
    }

    /**
     * Override this method to use default
     * {@link Platform#ANDROID} index.
     * @param platform {@link PlatformType} instance.
     * @return {@link Integer} value.
     * @see ChoiceInputType#scrollablePickerIndex(PlatformType)
     * @see #androidScrollablePickerIndex()
     */
    @Override
    public int scrollablePickerIndex(@NotNull PlatformType platform) {
        return androidScrollablePickerIndex();
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link XPath} instance.
     * @see AndroidNumericPickerInputType#androidChoicePickerParentXP()
     * @see AndroidDatePickerType#hh_mm_TIMEPICKER
     * @see AndroidView.ViewType#TIME_PICKER
     * @see Attribute#forClass(String)
     * @see BaseViewType#className()
     * @see Platform#ANDROID
     * @see XPath.Builder#addAttribute(Attribute)
     * @see #datePickerType()
     */
    @NotNull
    @Override
    public XPath androidChoicePickerParentXP() {
        switch (datePickerType()) {
            case hh_mm_TIMEPICKER:
                String cls = AndroidView.ViewType.TIME_PICKER.className();
                Attribute attribute = Attribute.forClass(cls);
                return XPath.builder().addAttribute(attribute).build();

            default:
                return AndroidNumericPickerInputType.super.androidChoicePickerParentXP();
        }
    }

    /**
     * Override this method to get the picker index for each {@link CalendarUnit}
     * instances, based on {@link #calendarUnit()}.
     * @return {@link Integer} value.
     * @see CalendarUnit#HOUR
     * @see CalendarUnit#MINUTE
     * @see #calendarUnit()
     * @see #hourPickerIndex()
     * @see #minutePickerIndex()
     * @see #NOT_AVAILABLE
     */
    @Override
    public int androidScrollablePickerIndex() {
        switch (calendarUnit()) {
            case HOUR:
                return hourPickerIndex();

            case MINUTE:
                return minutePickerIndex();

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the picker index that corresponds to {@link CalendarUnit#HOUR}.
     * @return {@link Integer} value.
     * @see AndroidDatePickerType#hh_mm_TIMEPICKER
     * @see #datePickerType()
     * @see #NOT_AVAILABLE
     */
    private int hourPickerIndex() {
        switch (datePickerType()) {
            case hh_mm_TIMEPICKER:
                return 0;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Get the picker index that corresponds to {@link CalendarUnit#MINUTE}.
     * @return {@link Integer} value.
     * @see AndroidDatePickerType#hh_mm_TIMEPICKER
     * @see #datePickerType()
     * @see #NOT_AVAILABLE
     */
    private int minutePickerIndex() {
        switch (datePickerType()) {
            case hh_mm_TIMEPICKER:
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
