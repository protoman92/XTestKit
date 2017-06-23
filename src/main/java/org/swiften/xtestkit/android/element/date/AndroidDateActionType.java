package org.swiften.xtestkit.android.element.date;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.date.*;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides date-related capabilities. Only applicable to
 * system calendar/date pickers.
 */
public interface AndroidDateActionType extends CalendarDateActionType, NumberDatePickerType {
    /**
     * Override this method to provide default implementation.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateActionType#rxa_openPicker(DateProviderType, CalendarUnit)
     * @see AndroidDatePickerType#isCalendar()
     * @see AndroidDatePickerType#isNumberPicker()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_openPicker(@NotNull DateProviderType param,
                                             @NotNull CalendarUnit unit) {
        DatePickerType pickerType = param.datePickerType();

        if (pickerType instanceof AndroidDatePickerType) {
            AndroidDatePickerType apt = (AndroidDatePickerType)pickerType;

            if (apt.isCalendar()) {
                return CalendarDateActionType.super.rxa_openPicker(param, unit);
            } else if (apt.isNumberPicker()) {
                return NumberDatePickerType.super.rxa_openPicker(param, unit);
            }
        }

        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link DateProviderType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateActionType#rxa_select(DateProviderType, CalendarUnit)
     * @see AndroidDatePickerType#isCalendar()
     * @see AndroidDatePickerType#isNumberPicker()
     * @see DateProviderType#datePickerType()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_select(@NotNull DateProviderType param,
                                         @NotNull CalendarUnit unit) {
        DatePickerType pickerType = param.datePickerType();

        if (pickerType instanceof AndroidDatePickerType) {
            AndroidDatePickerType apt = (AndroidDatePickerType)pickerType;

            if (apt.isCalendar()) {
                return CalendarDateActionType.super.rxa_select(param, unit);
            } else if (apt.isNumberPicker()) {
                return NumberDatePickerType.super.rxa_select(param, unit);
            }
        }

        throw new RuntimeException(NOT_AVAILABLE);
    }
}
