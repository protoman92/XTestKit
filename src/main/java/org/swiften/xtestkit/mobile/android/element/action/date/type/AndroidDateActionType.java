package org.swiften.xtestkit.mobile.android.element.action.date.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
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
import org.swiften.xtestkit.mobile.android.AndroidView;
import org.swiften.xtestkit.mobile.android.type.DatePickerContainerType;
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
    DatePickerContainerType
{
    //region Validation
    /**
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_hasDate(DateType)
     * @see #rx_containsText(String...)
     * @see ObjectUtil#nonNull(Object)
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Flowable<Boolean> rx_hasDate(@NotNull DateType param) {
        return Maybe
            .mergeArray(
                rx_containsText(dayString(param)).firstElement(),
                rx_containsText(monthString(param)).firstElement(),
                rx_containsText(yearString(param)).firstElement()
            )
            .all(ObjectUtil::nonNull)
            .toFlowable();
    }
    //endregion

    //region Actions
    /**
     * Open the year picker.
     * @return {@link Flowable} instance.
     * @see #rxDatePickerYear()
     */
    @NotNull
    default Flowable<Boolean> rxOpenYearPicker() {
        return rxDatePickerYear().flatMap(this::rx_click).map(a -> true);
    }

    /**
     * Open the month picker.
     * @return {@link Flowable} instance.
     */
    default Flowable<Boolean> rxOpenMonthPicker() {
        return Flowable.just(true);
    }

    /**
     * Open the day picker.
     * @return {@link Flowable} instance.
     */
    default Flowable<Boolean> rxOpenDayPicker() {
        return Flowable.just(true);
    }

    /**
     * Get the list view items that corresponds to {@link CalendarUnit}.
     * This assumes the user is already in a picker view.
     * @param element {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #rx_byXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rxListViewItems(@NotNull CalendarUnit element) {
        String id = datePickerViewType().listViewItemId(element);
        String cls = AndroidView.ViewType.TEXT_VIEW.className();
        XPath xPath = newXPathBuilder().containsID(id).ofClass(cls).build();

        ByXPath byXPath = ByXPath.builder()
            .withXPath(xPath)
            .withError(NO_SUCH_ELEMENT)
            .build();

        return rx_byXPath(byXPath);
    }

    /**
     * Select a component by scrolling until the component {@link String} is
     * visible. We can supply a custom {@link XPath} here in case the element
     * being searched for require a non-standard query.
     * @param PARAM {@link DateType} instance.
     * @param ELEMENT {@link CalendarUnit} instance.
     * @param xPath A custom {@link XPath} instance.
     * @param SCROLL_RATIO A dampening ratio for vertical scroll.
     * @return {@link Flowable} instance.
     * @see #rxListView(CalendarUnit)
     * @see SwipeRepeatType#rx_repeatSwipe()
     */
    @NotNull
    default Flowable<Boolean> rxScrollAndSelectComponent(
        @NotNull final DateType PARAM,
        @NotNull final CalendarUnit ELEMENT,
        @NotNull XPath xPath,
        final double SCROLL_RATIO
    ) {
        final AndroidDateActionType THIS = this;
        final String CP_STRING = string(PARAM, ELEMENT);
        final int COMPONENT = PARAM.component(ELEMENT);
        String format = datePickerViewType().stringFormat(ELEMENT);
        final SimpleDateFormat FORMATTER = new SimpleDateFormat(format);

        /* We need a custom ByXPath because we want to limit the retry
         * count. Otherwise, the scroll action will take quite a long time as
         * we need to recursively scroll and check for the existence of the
         * element. Consider this trade-off between time and accuracy */
        final ByXPath BY_XPATH = ByXPath.builder()
            .withXPath(xPath)
            .withError(NO_SUCH_ELEMENT)
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
                    .map(a -> a.component(ELEMENT))
                    .map(a -> a - COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<?> rx_compareFirst(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> ((DateType) () -> a))
                    .map(a -> a.component(ELEMENT))
                    .filter(a -> a > COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<?> rx_compareLast(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> ((DateType) () -> a))
                    .map(a -> a.component(ELEMENT))
                    .filter(a -> a < COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rx_scrollViewChildItems() {
                return THIS.rxListViewItems(ELEMENT);
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
                return THIS.rxListView(ELEMENT);
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
     * @param element {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #rxScrollAndSelectComponent(DateType, CalendarUnit, XPath, double)
     */
    @NotNull
    default Flowable<Boolean> rxScrollAndSelectComponent(
        @NotNull DateType param,
        @NotNull CalendarUnit element
    ) {
        XPath xPath = XPath.builder(platform())
            .containsID(datePickerViewType().pickerViewId(element))
            .containsText(string(param, element))
            .build();

        return rxScrollAndSelectComponent(param, element, xPath, 0.5d);
    }

    /**
     * Select a year {@link String} by scrolling until it becomes visible.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_selectDate(DateType)
     * @see #rxScrollAndSelectComponent(DateType, CalendarUnit)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_selectYear(@NotNull DateType param) {
        return rxScrollAndSelectComponent(param, CalendarUnit.YEAR);
    }

    /**
     * Select a month {@link String}. To be implemented when necessary.
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
     * Select a day.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_selectDay(DateType)
     * @see #rxCalibrateDate(DateType)
     * @see #rxScrollAndSelectComponent(DateType, CalendarUnit)
     */
    @NotNull
    default Flowable<Boolean> rx_selectDay(@NotNull DateType param) {
        switch (datePickerViewType()) {
            case CALENDAR:
                /* In this case, the day selection is a bit different. We
                 * need to scroll the list view and check content description
                 * for the date String. We also need to continually click
                 * on a day to snap the list view into position */
                return rxCalibrateDate(param);

            case SPINNER:
                return rxScrollAndSelectComponent(param, CalendarUnit.DAY);

            default:
                return RxUtil.error(UNKNOWN_DATE_VIEW_TYPE);
        }
    }

    /**
     * @param PARAM {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rx_selectDate(DateType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_selectDate(@NotNull final DateType PARAM) {
        LogUtil.printfThread("Selecting date %s", dateString(PARAM));

        return rxOpenYearPicker()
            .flatMap(a -> rx_selectYear(PARAM))
            .flatMap(a -> rxOpenMonthPicker())
            .flatMap(a -> rx_selectMonth(PARAM))
            .flatMap(a -> rxOpenDayPicker())
            .flatMap(a -> rx_selectDay(PARAM))
            .flatMap(a -> rx_hasDate(PARAM))
            .filter(BooleanUtil::isTrue)
            .switchIfEmpty(RxUtil.error(DATES_NOT_MATCHED));
    }
    //endregion

    //region Calendar WebElement
    /**
     * Get the list view that corresponds to {@link CalendarUnit}.
     * The implementations may change based on
     * {@link DatePickerType}.
     * @param element {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #rxCalendarListView()
     */
    @NotNull
    default Flowable<WebElement> rxListView(@NotNull CalendarUnit element) {
        return rxCalendarListView();
    }

    /**
     * Get the {@link WebElement} that corresponds to {@link CalendarUnit}.
     * @param element {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DatePickerType#displayViewId(CalendarUnit)
     */
    @NotNull
    default Flowable<WebElement> rxDisplayElement(@NotNull CalendarUnit element) {
        /* We need to use a custom XPath query because there are some views
         * that have similar IDs (e.g. if we search for date_picker_month, the
         * resulting WebElement may not be the one we are looking for */
        XPath xPath = newXPathBuilder()
            .ofClass(AndroidView.ViewType.TEXT_VIEW.className())
            .containsID(datePickerViewType().displayViewId(element))
            .build();

        ByXPath param = ByXPath.builder()
            .withXPath(xPath)
            .withError(NO_SUCH_ELEMENT)
            .build();

        return rx_byXPath(param).firstElement().toFlowable();
    }

    /**
     * Get the date picker header.
     * @return {@link Flowable} instance.
     * @see #rx_containsID(String...)
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerHeader() {
        return rx_containsID("date_picker_header").firstElement().toFlowable();
    }

    /**
     * Get the date picker day label.
     * @return {@link Flowable} instance.
     * @see #rxDisplayElement(CalendarUnit)
     * @see CalendarUnit#DAY
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerDay() {
        return rxDisplayElement(CalendarUnit.DAY);
    }

    /**
     * Get the date picker month label.
     * @return {@link Flowable} instance.
     * @see #rxDisplayElement(CalendarUnit)
     * @see CalendarUnit#MONTH
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerMonth() {
        return rxDisplayElement(CalendarUnit.MONTH);
    }

    /**
     * Get the date picker year label.
     * @return {@link Flowable} instance.
     * @see #rxDisplayElement(CalendarUnit)
     * @see CalendarUnit#YEAR
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerYear() {
        return rxDisplayElement(CalendarUnit.YEAR);
    }

    /**
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rxAllCalendarElements()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxAllCalendarElements() {
        Collection<Flowable<WebElement>> streams = new ArrayList<>();

        Collections.addAll(streams,
            rxDatePickerDay(),
            rxDatePickerMonth(),
            rxDatePickerYear()
        );

        if (datePickerViewType().isCalendarType()) {
            Collections.addAll(streams, rxCalendarListView());
        }

        return Flowable.concat(streams);
    }
    //endregion

    //region String from DateType
    /**
     * Get {@link CalendarUnit}'s {@link String} representation of
     * {@link DateType#value()}.
     * @param param {@link DateType} instance.
     * @param element {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see DatePickerType#stringFormat(CalendarUnit)
     */
    @NotNull
    default String string(@NotNull DateType param,
                          @NotNull CalendarUnit element) {
        String format = datePickerViewType().stringFormat(element);
        return new SimpleDateFormat(format).format(param.value());
    }

    /**
     * Get the day, as formatted using {@link DatePickerType#dayFormat()}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #string(DateType, CalendarUnit)
     */
    @NotNull
    @Override
    default String dayString(@NotNull DateType param) {
        return string(param, CalendarUnit.DAY);
    }

    /**
     * Get the month, as formatted using {@link DatePickerType#monthFormat()}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #string(DateType, CalendarUnit)
     */
    @NotNull
    @Override
    default String monthString(@NotNull DateType param) {
        return string(param, CalendarUnit.MONTH);
    }

    /**
     * Get the year, as formatted using {@link DatePickerType#yearFormat()}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #string(DateType, CalendarUnit)
     */
    @NotNull
    @Override
    default String yearString(@NotNull DateType param) {
        return string(param, CalendarUnit.YEAR);
    }
    //endregion

    //region Displayed Date
    /**
     * Get the {@link Integer} value that represents the {@link CalendarUnit}
     * as displayed by the relevant {@link WebElement}. For e.g., if we are
     * searching for {@link CalendarUnit#DAY}, we need to look at
     * {@link #rxDatePickerDay()}.
     * @param element {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    @SuppressWarnings("MagicConstant")
    default Flowable<Integer> rxDisplayedComponent(@NotNull CalendarUnit element) {
        String format = datePickerViewType().stringFormat(element);
        final SimpleDateFormat FORMATTER = new SimpleDateFormat(format);
        final Integer CALENDAR_CONSTANT = element.value();

        return rxDisplayElement(element)
            .map(this::getText)
            .map(FORMATTER::parse)
            .map(a -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(a);
                return calendar;
            })
            .map(a -> a.get(CALENDAR_CONSTANT));
    }

    /**
     * Get the day as displayed by the {@link WebElement} emitted by
     * {@link #rxDatePickerDay()}.
     * @return {@link Flowable} value.
     * @see #rxDisplayedComponent(CalendarUnit)
     */
    @NotNull
    default Flowable<Integer> rxDisplayedDay() {
        return rxDisplayedComponent(CalendarUnit.DAY);
    }

    /**
     * Get the month as displayed by the {@link WebElement} emitted by
     * {@link #rxDatePickerMonth()}.
     * @return {@link Flowable} instance.
     * @see #rxDisplayedComponent(CalendarUnit)
     */
    @NotNull
    default Flowable<Integer> rxDisplayedMonth() {
        return rxDisplayedComponent(CalendarUnit.MONTH);
    }

    /**
     * Get the year as displayed by the {@link WebElement} emitted by
     * {@link #rxDatePickerYear()}.
     * @return {@link Flowable} instance.
     * @see #rxDisplayedComponent(CalendarUnit)
     */
    @NotNull
    default Flowable<Integer> rxDisplayedYear() {
        return rxDisplayedComponent(CalendarUnit.YEAR);
    }

    /**
     * Get the {@link Date} as displayed by the date picker.
     * @return {@link Flowable} instance.
     * @see BaseDateActionType#rxDisplayedDate()
     * @see #rxDisplayedDay()
     * @see #rxDisplayedMonth()
     * @see #rxDisplayedYear()
     */
    @NotNull
    @Override
    @SuppressWarnings("MagicConstant")
    default Flowable<Date> rxDisplayedDate() {
        return Flowable.zip(
            rxDisplayedDay(),
            rxDisplayedMonth(),
            rxDisplayedYear(),
            (day, month, year) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(CalendarUnit.DAY.value(), day);
                calendar.set(CalendarUnit.MONTH.value(), month);
                calendar.set(CalendarUnit.YEAR.value(), year);
                return calendar;
            }).map(Calendar::getTime);
    }
    //endregion
}
