package org.swiften.xtestkit.android.element.action.date;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.date.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.DateType;
import org.swiften.xtestkit.base.element.action.general.BaseActionType;
import org.swiften.xtestkit.base.element.action.swipe.SwipeRepeatComparisonType;
import org.swiften.xtestkit.base.element.action.swipe.SwipeRepeatType;
import org.swiften.xtestkit.base.element.action.swipe.SwipeType;
import org.swiften.xtestkit.base.element.locator.general.param.ByXPath;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.MobileSwipeType;

import java.text.SimpleDateFormat;

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
     * Override this method to provide default implementation.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #rx_openYearPicker()
     * @see #rx_openMonthPicker()
     * @see #rx_openDayPicker()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_openPicker(@NotNull CalendarUnit unit) {
        switch (unit) {
            case YEAR:
                return rx_openYearPicker();

            case MONTH:
                return rx_openMonthPicker();

            case DAY:
                return rx_openDayPicker();

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
    default Flowable<Boolean> rx_select(@NotNull DateType param,
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
     * visible. We can supply a custom {@link XPath} here in case the element
     * being searched for require a non-standard query.
     * @param PARAM {@link DateType} instance.
     * @param UNIT {@link CalendarUnit} instance.
     * @param targetXpath A custom {@link XPath} instance. This instance will
     *                    be used to look for the {@link WebElement} whose text
     *                    is the component text we are interested in.
     * @param SCROLL_RATIO A dampening ratio for vertical scroll.
     * @return {@link Flowable} instance.
     * @see #rx_pickerView(CalendarUnit)
     * @see SwipeRepeatType#rx_execute()
     */
    @NotNull
    default Flowable<Boolean> rx_scrollAndSelect(
        @NotNull final DateType PARAM,
        @NotNull final CalendarUnit UNIT,
        @NotNull XPath targetXpath,
        final double SCROLL_RATIO
    ) {
        final AndroidDateActionType THIS = this;
        final String CP_STRING = string(PARAM, UNIT);
        final int COMPONENT = PARAM.component(UNIT);
        String format = datePickerType().stringFormat(UNIT);
        final SimpleDateFormat FORMATTER = new SimpleDateFormat(format);

        /* We need a custom ByXPath because we want to limit the retry
         * count. Otherwise, the scroll action will take quite a long time as
         * we need to recursively scroll and check for the existence of the
         * element. Consider this trade-off between time and accuracy */
        final ByXPath QUERY = ByXPath.builder().withXPath(targetXpath).withRetries(1).build();

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
                    .map(a -> ((DateType) () -> a))
                    .map(a -> a.component(UNIT))
                    .map(a -> a - COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<?> rx_compareFirst(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> ((DateType) () -> a))
                    .map(a -> a.component(UNIT))
                    .filter(a -> a > COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<?> rx_compareLast(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> ((DateType) () -> a))
                    .map(a -> a.component(UNIT))
                    .filter(a -> a < COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rx_scrollViewChildItems() {
                return THIS.rx_listViewItems(UNIT);
            }

            @NotNull
            @Override
            public Flowable<Double> rx_elementSwipeRatio() {
                return Flowable.just(SCROLL_RATIO);
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
                return THIS.rx_pickerView(UNIT);
            }

            @Override
            public void swipeOnce(@NotNull SwipeType param) {
                THIS.swipeOnce(param);
            }
        };

        return repeater.rx_execute();
    }

    /**
     * Same as above, but uses a default {@link XPath} instance and a default
     * scroll ratio.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #rx_scrollAndSelect(DateType, CalendarUnit, XPath, double)
     */
    @NotNull
    default Flowable<Boolean> rx_scrollAndSelect(@NotNull DateType param,
                                                 @NotNull CalendarUnit unit) {
        XPath xPath = XPath.builder(platform())
            .withXPath(datePickerType().targetItemXPath(unit))
            .containsText(string(param, unit))
            .build();

        return rx_scrollAndSelect(param, unit, xPath, 0.5d);
    }

    /**
     * Open the year picker.
     * @return {@link Flowable} instance.
     * @see #rx_elementLabel(CalendarUnit)
     * @see #rxa_click(WebElement)
     * @see BooleanUtil#toTrue(Object)
     */
    @NotNull
    default Flowable<Boolean> rx_openYearPicker() {
        final AndroidDateActionType THIS = this;

        return rx_elementLabel(CalendarUnit.YEAR)
            .flatMap(THIS::rxa_click)
            .map(BooleanUtil::toTrue);
    }

    /**
     * Open the month picker.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_openMonthPicker() {
        return Flowable.just(true);
    }

    /**
     * Open the day picker.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_openDayPicker() {
        return Flowable.just(true);
    }

    /**
     * Select year
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_selectDate(DateType)
     * @see #rx_scrollAndSelect(DateType, CalendarUnit)
     */
    @NotNull
    default Flowable<Boolean> rx_selectYear(@NotNull DateType param) {
        return rx_scrollAndSelect(param, CalendarUnit.YEAR);
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
     * @see #rx_scrollAndSelect(DateType, CalendarUnit)
     */
    @NotNull
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
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_allCalendarElements()
     * @see #rx_elementLabel(CalendarUnit)
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Flowable<WebElement> rx_allCalendarElements() {
        return Flowable.mergeArray(
            rx_elementLabel(CalendarUnit.DAY),
            rx_elementLabel(CalendarUnit.MONTH),
            rx_elementLabel(CalendarUnit.YEAR)
        );
    }

    /**
     * Get the list view items that corresponds to {@link CalendarUnit}.
     * This assumes the user is already in a picker view.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DatePickerType#pickerItemXPath(CalendarUnit)
     * @see #rxe_byXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rx_listViewItems(@NotNull CalendarUnit unit) {
        XPath xPath = datePickerType().pickerItemXPath(unit);
        ByXPath query = ByXPath.builder().withXPath(xPath).build();
        return rxe_byXPath(query);
    }
    //endregion
}
