package org.swiften.xtestkit.mobile.android.element.action.date.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.type.DateType;
import org.swiften.xtestkit.base.element.action.general.type.BaseActionType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.element.locator.general.param.ByXPath;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.type.MobileSwipeType;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides date-related capabilities. Only applicable to
 * system calendar/date pickers.
 */
public interface AndroidDateActionType extends
    AndroidDateActionErrorType,
    AndroidDatePickerContainerType,
    CalendarPickerActionType,
    MobileActionType<AndroidDriver<AndroidElement>>,
    BaseActionType<AndroidDriver<AndroidElement>>,
    MobileSwipeType<AndroidDriver<AndroidElement>>
{
    //region Actions
    /**
     * Open the year picker.
     * @return {@link Flowable} instance.
     * @see #rx_datePickerYear()
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_openYearPicker() {
        return rx_datePickerYear().flatMap(this::rx_click).map(BooleanUtil::toTrue);
    }

    /**
     * Open the month picker.
     * @return {@link Flowable} instance.
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_openMonthPicker() {
        return Flowable.just(true);
    }

    /**
     * Open the day picker.
     * @return {@link Flowable} instance.
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_openDayPicker() {
        return Flowable.just(true);
    }

    /**
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_selectDate(DateType)
     * @see #rx_scrollAndSelect(DateType, CalendarUnit)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_selectYear(@NotNull DateType param) {
        return rx_scrollAndSelect(param, CalendarUnit.YEAR);
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
     * @see #rx_calibrateDate(DateType)
     * @see #rx_scrollAndSelect(DateType, CalendarUnit)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_selectDay(@NotNull DateType param) {
        switch (datePickerType()) {
            case CALENDAR:
                /* In this case, the day selection is a bit different. We
                 * need to scroll the list view and check content description
                 * for the date String. We also need to continually click
                 * on a day to snap the list view into position */
                return rx_calibrateDate(param);

            case SPINNER:
                return rx_scrollAndSelect(param, CalendarUnit.DAY);

            default:
                return RxUtil.error(UNKNOWN_DATE_VIEW_TYPE);
        }
    }
    //endregion

    //region Calendar WebElement
    /**
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_allCalendarElements()
     * @see #rx_datePickerDay()
     * @see #rx_datePickerMonth()
     * @see #rx_datePickerYear()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rx_allCalendarElements() {
        Collection<Flowable<WebElement>> streams = new ArrayList<>();

        Collections.addAll(streams,
            rx_datePickerDay(),
            rx_datePickerMonth(),
            rx_datePickerYear()
        );

        return Flowable.concat(streams);
    }
    //endregion
}
