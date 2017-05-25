package org.swiften.xtestkit.base.element.action.date;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.action.swipe.BaseSwipeType;
import org.swiften.xtestkit.base.element.action.swipe.SwipeRepeatComparisonType;
import org.swiften.xtestkit.base.element.action.swipe.SwipeRepeatType;
import org.swiften.xtestkit.base.element.action.swipe.SwipeType;
import org.swiften.xtestkit.base.element.locator.general.param.ByXPath;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.element.property.BaseElementPropertyType;
import org.swiften.xtestkit.base.type.BaseErrorType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This interface provides date-related actions, such as selecting a date/
 * month/year. However, the specific implementation is left to the individual
 * platforms.
 *
 * These methods assume that the user is in a calendar view.
 */
public interface BaseDateActionType<D extends WebDriver> extends
    BaseClickActionType,
    BaseDateErrorType,
    BaseElementPropertyType,
    BaseErrorType,
    BaseLocatorType<D>,
    BaseSwipeType<D>,
    DatePickerContainerType
{
    //region Validation
    /**
     * Check if {@link Date} is currently active. This assumes that the
     * user is in a calendar view.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see #rx_containsText(String...)
     * @see ObjectUtil#nonNull(Object)
     */
    @NotNull
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
     */
    @NotNull
    Flowable<Boolean> rx_openYearPicker();

    /**
     * Open the month picker.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_openMonthPicker();

    /**
     * Open the day picker.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_openDayPicker();

    /**
     * Select {@link Date}. This assumes that the user is in a calendar
     * view.
     * @param PARAM {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see #rx_selectYear(DateType)
     * @see #rx_selectMonth(DateType)
     * @see #rx_selectDay(DateType)
     * @see #rx_hasDate(DateType)
     * @see #DATES_NOT_MATCHED
     */
    @NotNull
    default Flowable<Boolean> rx_selectDate(@NotNull final DateType PARAM) {
        LogUtil.printfThread("Selecting %s", dateString(PARAM));

        return rx_openYearPicker()
            .flatMap(a -> rx_selectYear(PARAM))
            .flatMap(a -> rx_openMonthPicker())
            .flatMap(a -> rx_selectMonth(PARAM))
            .flatMap(a -> rx_openDayPicker())
            .flatMap(a -> rx_selectDay(PARAM))
            .flatMap(a -> rx_hasDate(PARAM))
            .filter(BooleanUtil::isTrue)
            .switchIfEmpty(RxUtil.error(DATES_NOT_MATCHED));
    }

    /**
     * Select a year based on the {@link Date} from {@link DateType} instance.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_selectYear(@NotNull DateType param);

    /**
     * Select a month based on the {@link Date} from {@link DateType} instance.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_selectMonth(@NotNull DateType param);

    /**
     * Select a day based on the {@link Date} from {@link DateType} instance.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_selectDay(@NotNull DateType param);

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
     * @see #rx_listView(CalendarUnit)
     * @see SwipeRepeatType#rx_execute()
     */
    @NotNull
    default Flowable<Boolean> rx_scrollAndSelect(
        @NotNull final DateType PARAM,
        @NotNull final CalendarUnit UNIT,
        @NotNull XPath targetXpath,
        final double SCROLL_RATIO
    ) {
        final BaseDateActionType<?> THIS = this;
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
                return THIS.rx_byXPath(QUERY)
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
            .withXPath(datePickerType().targetListViewItemXPath(unit))
            .containsText(string(param, unit))
            .build();

        return rx_scrollAndSelect(param, unit, xPath, 0.5d);
    }
    //endregion

    //region Elements
    /**
     * Get the {@link WebElement} that corresponds to {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #datePickerType()
     * @see DatePickerContainerType.DatePickerType#componentDisplayViewXPath(CalendarUnit)
     * @see #rx_byXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rx_element(@NotNull CalendarUnit unit) {
        /* We need to use a custom XPath query because there are some views
         * that have similar IDs (e.g. if we search for date_picker_month, the
         * resulting WebElement may not be the one we are looking for */
        XPath xPath = datePickerType().componentDisplayViewXPath(unit);
        ByXPath param = ByXPath.builder().withXPath(xPath).build();
        return rx_byXPath(param).firstElement().toFlowable();
    }

    /**
     * Get the list view that corresponds to {@link CalendarUnit}.
     * The implementations may change based on
     * {@link DatePickerContainerType.DatePickerType}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #datePickerType()
     * @see DatePickerContainerType.DatePickerType#pickerListViewXPath(CalendarUnit)
     * @see #rx_withXPath(XPath...)
     */
    @NotNull
    default Flowable<WebElement> rx_listView(@NotNull CalendarUnit unit) {
        XPath xPath = datePickerType().pickerListViewXPath(unit);
        return rx_withXPath(xPath).firstElement().toFlowable();
    }

    /**
     * Get the list view items that corresponds to {@link CalendarUnit}.
     * This assumes the user is already in a picker view.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DatePickerContainerType.DatePickerType#pickerListViewItemXPath(CalendarUnit)
     * @see #rx_byXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rx_listViewItems(@NotNull CalendarUnit unit) {
        XPath xPath = datePickerType().pickerListViewItemXPath(unit);
        ByXPath byXPath = ByXPath.builder().withXPath(xPath).build();
        return rx_byXPath(byXPath);
    }

    /**
     * Get the date picker day label.
     * @return {@link Flowable} instance.
     * @see #rx_element(CalendarUnit)
     * @see CalendarUnit#DAY
     */
    @NotNull
    default Flowable<WebElement> rx_datePickerDay() {
        return rx_element(CalendarUnit.DAY);
    }

    /**
     * Get the date picker month label.
     * @return {@link Flowable} instance.
     * @see #rx_element(CalendarUnit)
     * @see CalendarUnit#MONTH
     */
    @NotNull
    default Flowable<WebElement> rx_datePickerMonth() {
        return rx_element(CalendarUnit.MONTH);
    }

    /**
     * Get the date picker year label.
     * @return {@link Flowable} instance.
     * @see #rx_element(CalendarUnit)
     * @see CalendarUnit#YEAR
     */
    @NotNull
    default Flowable<WebElement> rx_datePickerYear() {
        return rx_element(CalendarUnit.YEAR);
    }

    /**
     * Get all calendar {@link WebElement}. This assumes that the user is in a
     * calendar view.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rx_allCalendarElements() {
        return RxUtil.error(NOT_AVAILABLE);
    }

    /**
     * Get the {@link Integer} value that represents the {@link CalendarUnit}
     * as displayed by the relevant {@link WebElement}. For e.g., if we are
     * searching for {@link CalendarUnit#DAY}, we need to look at
     * {@link #rx_datePickerDay()}.
     * @param element {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #datePickerType()
     * @see DatePickerType#stringFormat(CalendarUnit)
     * @see #rx_element(CalendarUnit)
     */
    @NotNull
    @SuppressWarnings("MagicConstant")
    default Flowable<Integer> rx_displayedComponent(@NotNull CalendarUnit element) {
        final BaseDateActionType<?> THIS = this;
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

    /**
     * Get the day as displayed by the {@link WebElement} emitted by
     * {@link #rx_datePickerDay()}.
     * @return {@link Flowable} value.
     * @see #rx_displayedComponent(CalendarUnit)
     * @see CalendarUnit#DAY
     */
    @NotNull
    default Flowable<Integer> rx_displayedDay() {
        return rx_displayedComponent(CalendarUnit.DAY);
    }

    /**
     * Get the month as displayed by the {@link WebElement} emitted by
     * {@link #rx_datePickerMonth()}.
     * @return {@link Flowable} instance.
     * @see #rx_displayedComponent(CalendarUnit)
     * @see CalendarUnit#MONTH
     */
    @NotNull
    default Flowable<Integer> rx_displayedMonth() {
        return rx_displayedComponent(CalendarUnit.MONTH);
    }

    /**
     * Get the year as displayed by the {@link WebElement} emitted by
     * {@link #rx_datePickerYear()}.
     * @return {@link Flowable} instance.
     * @see #rx_displayedComponent(CalendarUnit)
     * @see CalendarUnit#YEAR
     */
    @NotNull
    default Flowable<Integer> rx_displayedYear() {
        return rx_displayedComponent(CalendarUnit.YEAR);
    }

    /**
     * Get the {@link Date} as displayed by the date picker.
     * @return {@link Flowable} instance.
     * @see #rx_displayedDay()
     * @see #rx_displayedMonth()
     * @see #rx_displayedYear()
     */
    @NotNull
    @SuppressWarnings("MagicConstant")
    default Flowable<Date> rx_displayedDate() {
        return Flowable.zip(
            rx_displayedDay(),
            rx_displayedMonth(),
            rx_displayedYear(),
            (day, month, year) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(CalendarUnit.DAY.value(), day);
                calendar.set(CalendarUnit.MONTH.value(), month);
                calendar.set(CalendarUnit.YEAR.value(), year);
                return calendar;
            }).map(Calendar::getTime);
    }
    //endregion

    /**
     * Get {@link CalendarUnit}'s {@link String} representation of
     * {@link DateType#value()}.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see #datePickerType()
     * @see DatePickerContainerType.DatePickerType#stringFormat(CalendarUnit)
     * @see CalendarUnit#value()
     */
    @NotNull
    default String string(@NotNull DateType param, @NotNull CalendarUnit unit) {
        String format = datePickerType().stringFormat(unit);
        return new SimpleDateFormat(format).format(param.value());
    }

    /**
     * Get the day {@link String}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #string(DateType, CalendarUnit)
     */
    @NotNull
    default String dayString(@NotNull DateType param) {
        return string(param, CalendarUnit.DAY);
    }

    /**
     * Get the month {@link String}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #string(DateType, CalendarUnit)
     */
    @NotNull
    default String monthString(@NotNull DateType param) {
        return string(param, CalendarUnit.MONTH);
    }

    /**
     * Get the year {@link String}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #string(DateType, CalendarUnit)
     */
    @NotNull
    default String yearString(@NotNull DateType param) {
        return string(param, CalendarUnit.YEAR);
    }

    /**
     * Get {@link String} representation of {@link DateType#value()}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #dateString(DateType)
     * @see #monthString(DateType)
     * @see #yearString(DateType)
     */
    @NotNull
    default String dateString(@NotNull DateType param) {
        return String.format(
            "Day: %s, Month: %s, Year: %s",
            dayString(param),
            monthString(param),
            yearString(param)
        );
    }
}
