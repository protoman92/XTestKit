package org.swiften.xtestkit.android.element.action.date;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.date.*;
import org.swiften.xtestkit.base.element.action.general.BaseActionType;
import org.swiften.xtestkit.base.element.action.swipe.SwipeRepeatComparisonType;
import org.swiften.xtestkit.base.element.action.swipe.SwipeRepeatType;
import org.swiften.xtestkit.base.element.action.swipe.SwipeType;
import org.swiften.xtestkit.base.element.locator.general.param.ByXPath;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.MobileSwipeType;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides date-related capabilities. Only applicable to
 * system calendar/date pickers.
 */
public interface AndroidDateActionType extends
    CalendarPickerActionType,
    MobileActionType<AndroidDriver<AndroidElement>>,
    BaseActionType<AndroidDriver<AndroidElement>>,
    MobileSwipeType<AndroidDriver<AndroidElement>>
{
    //region Actions
    /**
     * Override this method to provide default implementation.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rxa_openPicker(DateType, CalendarUnit)
     * @see #rx_openYearPicker(DateType)
     * @see #rx_openMonthPicker(DateType)
     * @see #rx_openDayPicker(DateType)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_openPicker(@NotNull DateType param,
                                             @NotNull CalendarUnit unit) {
        switch (unit) {
            case YEAR:
                return rx_openYearPicker(param);

            case MONTH:
                return rx_openMonthPicker(param);

            case DAY:
                return rx_openDayPicker(param);

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #rx_selectYear(DateType)
     * @see #rx_selectMonth(DateType)
     * @see #rx_selectDay(DateType)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_select(@NotNull DateType param,
                                         @NotNull CalendarUnit unit) {
        switch (unit) {
            case YEAR:
                return rx_selectYear(param);

            case MONTH:
                return rx_selectMonth(param);

            case DAY:
                return rx_selectDay(param);

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Select a component by scrolling until the component {@link String} is
     * visible.
     * @param PARAM {@link DateType} instance.
     * @param UNIT {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#toTrue(Object)
     * @see DatePickerType#stringFormat(CalendarUnit)
     * @see DatePickerType#targetItemXPath(CalendarUnit)
     * @see DateParam.Builder#withDate(Date)
     * @see DateParam.Builder#withDateType(DateType)
     * @see DateType#component(CalendarUnit)
     * @see DateType#datePickerType()
     * @see SwipeRepeatType#rx_execute()
     * @see XPath.Builder#containsText(String)
     * @see #getText(WebElement)
     * @see #string(DateType, CalendarUnit)
     * @see #rxa_click(WebElement)
     * @see #rxe_pickerView(DateType, CalendarUnit)
     */
    @NotNull
    default Flowable<Boolean> rxa_scrollAndSelect(@NotNull final DateType PARAM,
                                                  @NotNull final CalendarUnit UNIT) {
        final AndroidDateActionType THIS = this;
        final String CP_STRING = string(PARAM, UNIT);
        final int COMPONENT = PARAM.component(UNIT);
        String format = PARAM.datePickerType().stringFormat(UNIT);
        final SimpleDateFormat FORMATTER = new SimpleDateFormat(format);

        XPath xPath = XPath.builder(platform())
            .withXPath(PARAM.datePickerType().targetItemXPath(UNIT))
            .containsText(string(PARAM, UNIT))
            .build();

        /* We need a custom ByXPath because we want to limit the retry
         * count. Otherwise, the scroll action will take quite a long time as
         * we need to recursively scroll and check for the existence of the
         * element. Consider this trade-off between time and accuracy */
        final ByXPath QUERY = ByXPath.builder().withXPath(xPath).withRetries(1).build();

        /* This method is needed because sometimes Appium cannot correctly
         * detect the {@link WebElement} that contains the text we are looking
         * for - as a result, it will continue scrolling in the same direction
         * forever. With this method, even if the correct {@link WebElement}
         * is scrolled past, it will again come into focus (even several times
         * if needed), and eventually the element will be detected. */
        SwipeRepeatType repeater = new SwipeRepeatComparisonType() {
            @NotNull
            @Override
            public Flowable<Integer> rx_initialDifference(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> DateParam.builder().withDateType(PARAM).withDate(a))
                    .map(DateParam.Builder::build)
                    .map(a -> a.component(UNIT))
                    .map(a -> a - COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<?> rx_compareFirst(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> DateParam.builder().withDateType(PARAM).withDate(a))
                    .map(DateParam.Builder::build)
                    .map(a -> a.component(UNIT))
                    .filter(a -> a > COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<?> rx_compareLast(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> DateParam.builder().withDateType(PARAM).withDate(a))
                    .map(DateParam.Builder::build)
                    .map(a -> a.component(UNIT))
                    .filter(a -> a < COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rx_scrollViewChildItems() {
                return THIS.rx_listViewItems(PARAM, UNIT);
            }

            @NotNull
            @Override
            public Flowable<Double> rx_elementSwipeRatio() {
                return Flowable.just(0.5d);
            }

            @NotNull
            @Override
            public Flowable<Boolean> rx_shouldKeepSwiping() {
                return THIS.rxe_byXPath(QUERY)
                    /* Sometimes the driver will get the wrong element. In
                     * this case, keep scrolling so that in the next scroll,
                     * the element we are interested in gets more focus and
                     * is easier to detect. This is why we need to keep the
                     * scroll low in order to catch potential oddities like
                     * this */
                    .filter(a -> THIS.getText(a).equals(CP_STRING))
                    .flatMap(THIS::rxa_click)
                    .map(BooleanUtil::toTrue);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rx_scrollableViewToSwipe() {
                return THIS.rxe_pickerView(PARAM, UNIT);
            }

            @Override
            public void swipeOnce(@NotNull SwipeType param) {
                THIS.swipeOnce(param);
            }
        };

        return repeater.rx_execute();
    }

    /**
     * Open the year picker.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see AndroidDatePickerType#isCalendarMode()
     * @see BooleanUtil#toTrue(Object)
     * @see DateType#datePickerType()
     * @see #rxe_elementLabel(DateType, CalendarUnit)
     * @see #rxa_click(WebElement)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    default Flowable<Boolean> rx_openYearPicker(@NotNull DateType param) {
        DatePickerType pickerType = param.datePickerType();

        /* We only need to open the year picker if we are using a calendar
         * based picker. Otherwise, the picker appears directly on screen */
        if (pickerType instanceof AndroidDatePickerType) {
            if (((AndroidDatePickerType)pickerType).isCalendarMode()) {
                final AndroidDateActionType THIS = this;

                return rxe_elementLabel(param, CalendarUnit.YEAR)
                    .flatMap(THIS::rxa_click)
                    .map(BooleanUtil::toTrue);
            } else {
                return Flowable.just(true);
            }
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Open the month picker.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_openMonthPicker(@NotNull DateType param) {
        return Flowable.just(true);
    }

    /**
     * Open the day picker.
     * @param param {@link DateParam} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_openDayPicker(@NotNull DateType param) {
        return Flowable.just(true);
    }

    /**
     * Select year
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rxa_selectDate(DateType)
     * @see #rxa_scrollAndSelect(DateType, CalendarUnit)
     */
    @NotNull
    default Flowable<Boolean> rx_selectYear(@NotNull DateType param) {
        return rxa_scrollAndSelect(param, CalendarUnit.YEAR);
    }

    /**
     * Select month.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_selectMonth(@NotNull DateType param) {
        return Flowable.just(true);
    }

    /**
     * Select day.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see #rx_calibrateDate(DateType)
     * @see #rxa_scrollAndSelect(DateType, CalendarUnit)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    default Flowable<Boolean> rx_selectDay(@NotNull DateType param) {
        DatePickerType pickerType = param.datePickerType();

        if (pickerType instanceof AndroidDatePickerType) {
            switch ((AndroidDatePickerType)pickerType) {
                case VERTICAL_CALENDAR:
                    /* In this case, the day selection is a bit different. We
                     * need to scroll the list view and check content description
                     * for the date String. We also need to continually click
                     * on a day to snap the list view into position */
                    return rx_calibrateDate(param);

                case SPINNER:
                    return rxa_scrollAndSelect(param, CalendarUnit.DAY);

                default:
                    break;
            }
        }

        throw new RuntimeException(NOT_AVAILABLE);
    }
    //endregion

    //region Calendar WebElement
    /**
     * Get the list view items that corresponds to {@link CalendarUnit}. This
     * assumes the user is already in a picker view.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DatePickerType#pickerItemXPath(CalendarUnit)
     * @see #rxe_byXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rx_listViewItems(@NotNull DateType param,
                                                  @NotNull CalendarUnit unit) {
        XPath xPath = param.datePickerType().pickerItemXPath(unit);
        ByXPath query = ByXPath.builder().withXPath(xPath).build();
        return rxe_byXPath(query);
    }
    //endregion
}
