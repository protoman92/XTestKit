package org.swiften.xtestkit.base.element.action.type;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.type.DateType;

import static org.mockito.Mockito.mock;

/**
 * Created by haipham on 22/5/17.
 */
public interface TestDateActionType extends BaseDateActionType<WebDriver> {
    @NotNull
    @Override
    default DatePickerType datePickerType() {
        return mock(DatePickerType.class);
    }

    @NotNull
    @Override
    default Flowable<Boolean> rx_openYearPicker() {
        return Flowable.empty();
    }

    @NotNull
    @Override
    default Flowable<Boolean> rx_openMonthPicker() {
        return Flowable.empty();
    }

    @NotNull
    @Override
    default Flowable<Boolean> rx_openDayPicker() {
        return Flowable.empty();
    }

    @NotNull
    @Override
    default Flowable<Boolean> rx_selectYear(@NotNull DateType param) {
        return Flowable.empty();
    }

    @NotNull
    @Override
    default Flowable<Boolean> rx_selectMonth(@NotNull DateType param) {
        return Flowable.empty();
    }

    @NotNull
    @Override
    default Flowable<Boolean> rx_selectDay(@NotNull DateType param) {
        return Flowable.empty();
    }

    @NotNull
    @Override
    default Flowable<Integer> rx_displayedComponent(@NotNull CalendarUnit unit) {
        return Flowable.empty();
    }

    @NotNull
    @Override
    default Flowable<WebElement> rx_element(@NotNull CalendarUnit unit) {
        return Flowable.empty();
    }

    @NotNull
    @Override
    default String string(@NotNull DateType param, @NotNull CalendarUnit unit) {
        return "";
    }
}
