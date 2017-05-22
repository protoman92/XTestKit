package org.swiften.xtestkit.mobile.android.element.action.date.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.type.DateType;
import org.swiften.xtestkit.base.element.action.general.type.BaseActionType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeRepeatComparisonType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeRepeatType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.element.locator.general.param.ByXPath;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;
import org.swiften.xtestkit.base.element.property.type.BaseElementPropertyType;
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
    BaseElementPropertyType,
    CalendarPickerActionType,
    BaseClickActionType,
    MobileActionType<AndroidDriver<AndroidElement>>,
    BaseActionType<AndroidDriver<AndroidElement>>,
    MobileSwipeType<AndroidDriver<AndroidElement>>,
    AndroidDatePickerContainerType
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
     * Get the list view items that corresponds to {@link CalendarUnit}.
     * This assumes the user is already in a picker view.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see org.swiften.xtestkit.base.element.action.date.type.DatePickerContainerType.DatePickerType#listViewItemXPath(CalendarUnit)
     * @see #rx_byXPath(ByXPath)
     * @see #NO_SUCH_ELEMENT
     */
    @NotNull
    default Flowable<WebElement> rx_listViewItems(@NotNull CalendarUnit unit) {
        XPath xPath = datePickerType().listViewItemXPath(unit);
        ByXPath byXPath = ByXPath.builder().withXPath(xPath).build();
        return rx_byXPath(byXPath);
    }

    /**
     * Select a component by scrolling until the component {@link String} is
     * visible. We can supply a custom {@link XPath} here in case the element
     * being searched for require a non-standard query.
     * @param PARAM {@link DateType} instance.
     * @param UNIT {@link CalendarUnit} instance.
     * @param xPath A custom {@link XPath} instance.
     * @param SCROLL_RATIO A dampening ratio for vertical scroll.
     * @return {@link Flowable} instance.
     * @see #rx_listView(CalendarUnit)
     * @see SwipeRepeatType#rx_repeatSwipe()
     */
    @NotNull
    default Flowable<Boolean> rx_scrollAndSelect(
        @NotNull final DateType PARAM,
        @NotNull final CalendarUnit UNIT,
        @NotNull XPath xPath,
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
        final ByXPath BY_XPATH = ByXPath.builder()
            .withXPath(xPath)
            .withRetryCount(1)
            .build();

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
                return THIS.rx_byXPath(BY_XPATH)
                    /* Sometimes the driver will get the wrong element. In
                     * this case, keep scrolling so that in the next scroll,
                     * the element we are interested in gets more focus and
                     * is easier to detect. This is why we need to keep the
                     * scroll low in order to catch potential oddities like
                     * this */
                    .filter(a -> THIS.getText(a).equals(CP_STRING))
                    .flatMap(THIS::rx_click)
                    .map(BooleanUtil::toTrue);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rx_scrollableViewToSwipe() {
                return THIS.rx_listView(UNIT);
            }

            @Override
            public void swipeOnce(@NotNull SwipeType param) {
                THIS.swipeOnce(param);
            }
        };

        return repeater.rx_repeatSwipe();
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
            .withXPath(datePickerType().pickerViewXPath(unit))
            .containsText(string(param, unit))
            .build();

        return rx_scrollAndSelect(param, unit, xPath, 0.5d);
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
     * Get the list view that corresponds to {@link CalendarUnit}.
     * The implementations may change based on
     * {@link AndroidDatePickerType}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #rx_calendarListView()
     */
    @NotNull
    default Flowable<WebElement> rx_listView(@NotNull CalendarUnit unit) {
        return rx_calendarListView();
    }

    /**
     * Get the {@link WebElement} that corresponds to {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see AndroidDatePickerType#displayViewXPath(CalendarUnit)
     */
    @NotNull
    @Override
    default Flowable<WebElement> rx_element(@NotNull CalendarUnit unit) {
        /* We need to use a custom XPath query because there are some views
         * that have similar IDs (e.g. if we search for date_picker_month, the
         * resulting WebElement may not be the one we are looking for */
        XPath xPath = datePickerType().displayViewXPath(unit);
        ByXPath param = ByXPath.builder().withXPath(xPath).build();
        return rx_byXPath(param).firstElement().toFlowable();
    }

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

    //region Displayed Date
    /**
     * Get the {@link Integer} value that represents the {@link CalendarUnit}
     * as displayed by the relevant {@link WebElement}. For e.g., if we are
     * searching for {@link CalendarUnit#DAY}, we need to look at
     * {@link #rx_datePickerDay()}.
     * @param element {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    @Override
    @SuppressWarnings("MagicConstant")
    default Flowable<Integer> rx_displayedComponent(@NotNull CalendarUnit element) {
        final AndroidDateActionType THIS = this;
        String format = datePickerType().stringFormat(element);
        final SimpleDateFormat FORMATTER = new SimpleDateFormat(format);
        final Integer CALENDAR_CONSTANT = element.value();

        return rx_element(element)
            .map(THIS::getText)
            .map(FORMATTER::parse)
            .map(a -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(a);
                return calendar;
            })
            .map(a -> a.get(CALENDAR_CONSTANT));
    }
    //endregion

    //region String from DateType
    /**
     * Get {@link CalendarUnit}'s {@link String} representation of
     * {@link DateType#value()}.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see AndroidDatePickerType#stringFormat(CalendarUnit)
     */
    @NotNull
    @Override
    default String string(@NotNull DateType param, @NotNull CalendarUnit unit) {
        String format = datePickerType().stringFormat(unit);
        return new SimpleDateFormat(format).format(param.value());
    }
    //endregion
}
