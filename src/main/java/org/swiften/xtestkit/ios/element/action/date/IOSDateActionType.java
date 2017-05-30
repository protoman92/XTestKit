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
    BaseInputActionType<IOSDriver<IOSElement>>
{
    //region Action
    /**
     * Override this method to provide default implementation.
     * We do nothing here because the date picker is displayed by default.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rxa_openPicker(DateType, CalendarUnit)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_openPicker(@NotNull DateType param,
                                             @NotNull CalendarUnit unit) {
        return Flowable.just(true);
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rxa_select(DateType, CalendarUnit)
     * @see BooleanUtil#toTrue(Object)
     * @see #string(DateType, CalendarUnit)
     * @see #rxe_pickerView(DateType, CalendarUnit)
     * @see #rx_type(WebElement, String...)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_select(@NotNull DateType param,
                                         @NotNull CalendarUnit unit) {
        final IOSDateActionType THIS = this;
        String value = string(param, unit);

        return rxe_pickerView(param, unit)
            .flatMap(a -> THIS.rx_type(a, value))
            .map(BooleanUtil::toTrue);
    }
    //endregion
}
