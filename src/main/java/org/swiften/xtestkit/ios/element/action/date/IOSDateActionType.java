package org.swiften.xtestkit.ios.element.action.date;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.base.element.action.date.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.DateType;
import org.swiften.xtestkit.base.element.action.input.BaseInputActionType;

/**
 * Created by haipham on 22/5/17.
 */
public interface IOSDateActionType extends
    BaseDateActionType<IOSDriver<IOSElement>>,
    BaseInputActionType<IOSDriver<IOSElement>>,
    IOSDatePickerContainerType
{
    //region Action
    /**
     * Override this method to provide default implementation.
     * We do nothing here because the date picker is displayed by default.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_openPicker(CalendarUnit)
     */
    @NotNull
    default Flowable<Boolean> rx_openPicker(@NotNull CalendarUnit unit) {
        return Flowable.just(true);
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_select(DateType, CalendarUnit)
     * @see #string(DateType, CalendarUnit)
     * @see #rx_pickerView(CalendarUnit)
     * @see #rx_sendKeys(WebElement, String...)
     * @see BooleanUtil#toTrue(Object)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_select(@NotNull DateType param,
                                        @NotNull CalendarUnit unit) {
        final IOSDateActionType THIS = this;
        String value = string(param, unit);

        return rx_pickerView(unit)
            .flatMap(a -> THIS.rx_sendKeys(a, value))
            .map(BooleanUtil::toTrue);
    }
    //endregion

    //region Elements
    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_allCalendarElements()
     * @see #rx_elementLabel(CalendarUnit)
     * @see #rx_pickerView(CalendarUnit)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<WebElement> rx_allCalendarElements() {
        return Flowable.mergeArray(
            rx_elementLabel(CalendarUnit.DAY),
            rx_elementLabel(CalendarUnit.MONTH),
            rx_elementLabel(CalendarUnit.YEAR),
            rx_pickerView(CalendarUnit.DAY),
            rx_pickerView(CalendarUnit.MONTH),
            rx_pickerView(CalendarUnit.YEAR)
        );
    }
    //endregion
}
