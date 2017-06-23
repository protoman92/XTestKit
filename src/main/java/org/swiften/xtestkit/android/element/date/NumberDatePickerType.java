package org.swiften.xtestkit.android.element.date;

/**
 * Created by haipham on 6/2/17.
 */

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.element.choice.AndroidChoiceSelectorType;
import org.swiften.xtestkit.base.element.choice.ChoiceParam;
import org.swiften.xtestkit.base.element.choice.ChoiceType;
import org.swiften.xtestkit.base.element.date.DateActionType;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DatePickerType;
import org.swiften.xtestkit.base.element.date.DateProviderType;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * This interface provides common methods to handle date/time selection for
 * {@link Platform#ANDROID}.
 * This specifically caters to {@link AndroidDatePickerType#isNumberPicker()}.
 */
public interface NumberDatePickerType extends
    DateActionType<AndroidDriver<AndroidElement>>,
    AndroidChoiceSelectorType
{
    /**
     * Override this method to provide default implementation.
     * @param param {@link DateProviderType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_openPicker(@NotNull DateProviderType param,
                                             @NotNull CalendarUnit unit) {
        return Flowable.just(true);
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link DateProviderType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateActionType#rxa_select(DateProviderType, CalendarUnit)
     * @see ChoiceParam.Builder#withInput(ChoiceInputType)
     * @see ChoiceParam.Builder#withGeneralMode()
     * @see ChoiceParam.Builder#withSelectedChoice(String)
     * @see DateProviderType#datePickerType()
     * @see UnitNumberPickerWrapper.Builder#withCalendarUnit(CalendarUnit)
     * @see UnitNumberPickerWrapper.Builder#withDatePicker(DatePickerType)
     * @see #displayString(DateProviderType, CalendarUnit)
     * @see #rxa_selectChoice(ChoiceType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_select(@NotNull DateProviderType param,
                                         @NotNull CalendarUnit unit) {
        UnitNumberPickerWrapper wrapper = UnitNumberPickerWrapper.builder()
            .withCalendarUnit(unit)
            .withDatePicker(param.datePickerType())
            .build();

        String selected = displayString(param, unit);

        return rxa_selectChoice(ChoiceParam.builder()
            .withInput(wrapper)
            .withGeneralMode()
            .withSelectedChoice(selected)
            .build());
    }
}
