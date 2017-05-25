package org.swiften.xtestkit.ios.element.action.date;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.DateType;

/**
 * Created by haipham on 22/5/17.
 */
public interface IOSDateActionType extends
    BaseDateActionType<IOSDriver<IOSElement>>,
    IOSDatePickerContainerType
{
    @NotNull
    @Override
    default Flowable<WebElement> rx_listView(@NotNull CalendarUnit unit) {
        return Flowable.empty();
    }

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
        final String MONTH = monthString(param);

        return rx_listView(CalendarUnit.YEAR)
            .doOnNext(a -> a.sendKeys(MONTH))
            .map(BooleanUtil::toTrue);

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
}
