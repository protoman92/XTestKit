package org.swiften.xtestkit.mobile.ios.element.action.date.type;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.type.DateType;

import javax.xml.ws.WebEndpoint;

/**
 * Created by haipham on 22/5/17.
 */
public interface IOSDateActionType extends
    BaseDateActionType<IOSDriver<IOSElement>>,
    IOSDatePickerContainerType
{
    //region Action
    /**
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_openYearPicker()
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_openYearPicker() {
        return Flowable.just(true);
    }

    /**
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_openMonthPicker()
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_openMonthPicker() {
        return Flowable.just(true);
    }

    /**
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_openDayPicker()
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_openDayPicker() {
        return Flowable.just(true);
    }

    /**
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_selectYear(DateType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_selectYear(@NotNull DateType param) {
        return Flowable.just(true);
    }

    /**
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_selectMonth(DateType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_selectMonth(@NotNull DateType param) {
        return Flowable.just(true);
    }

    /**
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_selectDay(DateType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_selectDay(@NotNull DateType param) {
        return Flowable.just(true);
    }
    //endregion

    //region Element
    /**
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_displayedComponent(CalendarUnit)
     */
    @NotNull
    @Override
    default Flowable<Integer> rx_displayedComponent(@NotNull CalendarUnit unit) {
        return Flowable.just(0);
    }

    @NotNull
    @Override
    default Flowable<WebElement> rx_element(@NotNull CalendarUnit unit) {
        return Flowable.empty();
    }
    //endregion

    /**
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see BaseDateActionType#string(DateType, CalendarUnit)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    default String string(@NotNull DateType param, @NotNull CalendarUnit unit) {
        return "";
    }
}
