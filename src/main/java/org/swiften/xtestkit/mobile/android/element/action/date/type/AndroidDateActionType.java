package org.swiften.xtestkit.mobile.android.element.action.date.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.action.date.CalendarElement;
import org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.type.DateType;
import org.swiften.xtestkit.base.element.action.general.type.BaseActionType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeRepeatComparisonType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeRepeatType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.element.locator.general.param.ByXPath;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;
import org.swiften.xtestkit.mobile.android.AndroidView;
import org.swiften.xtestkit.mobile.android.element.property.type.AndroidElementPropertyType;
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
    CalendarPickerActionType,
    BaseClickActionType,
    MobileActionType<AndroidDriver<AndroidElement>>,
    BaseActionType<AndroidDriver<AndroidElement>>,
    MobileSwipeType<AndroidDriver<AndroidElement>>,
    AndroidElementPropertyType,
    DatePickerContainerType
{
    //region Validation
    /**
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see BaseDateActionType#rxHasDate(DateType)
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Flowable<Boolean> rxHasDate(@NotNull DateType param) {
        return Flowable
            .concatArray(
                rxElementContainingText(dayString(param)),
                rxElementContainingText(monthString(param)),
                rxElementContainingText(yearString(param))
            )
            .all(ObjectUtil::nonNull)
            .toFlowable();
    }
    //endregion

    //region Actions
    /**
     * Open the year picker.
     * @return A {@link Flowable} instance.
     * @see #rxDatePickerYear()
     */
    @NotNull
    default Flowable<Boolean> rxOpenYearPicker() {
        return rxDatePickerYear().flatMap(this::rxClick).map(a -> true);
    }

    /**
     * Open the month picker.
     * @return A {@link Flowable} instance.
     */
    default Flowable<Boolean> rxOpenMonthPicker() {
        return Flowable.just(true);
    }

    /**
     * Open the day picker.
     * @return A {@link Flowable} instance.
     */
    default Flowable<Boolean> rxOpenDayPicker() {
        return Flowable.just(true);
    }

    /**
     * Get the list view items that corresponds to a {@link CalendarElement}.
     * This assumes the user is already in a picker view.
     * @param element A {@link CalendarElement} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rxListViewItems(@NotNull CalendarElement element) {
        String id = datePickerViewType().listViewItemId(element);
        String cls = AndroidView.ViewType.TEXT_VIEW.className();
        XPath xPath = newXPathBuilder().containsID(id).ofClass(cls).build();

        ByXPath byXPath = ByXPath.builder()
            .withXPath(xPath)
            .withError(NO_SUCH_ELEMENT)
            .build();

        return rxElementsByXPath(byXPath);
    }

    /**
     * Select a component by scrolling until the component {@link String} is
     * visible. We can supply a custom {@link XPath} here in case the element
     * being searched for require a non-standard query.
     * @param PARAM A {@link DateType} instance.
     * @param ELEMENT A {@link CalendarElement} instance.
     * @param xPath A custom {@link XPath} instance.
     * @param SCROLL_RATIO A dampening ratio for vertical scroll.
     * @return A {@link Flowable} instance.
     * @see #rxListView(CalendarElement)
     * @see SwipeRepeatType#rxRepeatSwipe()
     */
    @NotNull
    default Flowable<Boolean> rxScrollAndSelectComponent(
        @NotNull final DateType PARAM,
        @NotNull final CalendarElement ELEMENT,
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
        SwipeRepeatComparisonType repeater = new SwipeRepeatComparisonType() {
            @NotNull
            @Override
            public Flowable<Integer> rxInitialDifference(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> ((DateType) () -> a))
                    .map(a -> a.component(ELEMENT))
                    .map(a -> a - COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<?> rxCompareFirst(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> ((DateType) () -> a))
                    .map(a -> a.component(ELEMENT))
                    .filter(a -> a > COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<?> rxCompareLast(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> ((DateType) () -> a))
                    .map(a -> a.component(ELEMENT))
                    .filter(a -> a < COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rxScrollViewChildItems() {
                return THIS.rxListViewItems(ELEMENT);
            }

            @Override
            public double elementSwipeRatio() {
                return SCROLL_RATIO;
            }

            @NotNull
            @Override
            public Flowable<Boolean> rxShouldKeepSwiping() {
                return THIS.rxElementsByXPath(BY_XPATH)
                    /* Sometimes the driver will get the wrong element. In
                     * this case, keep scrolling so that in the next scroll,
                     * the element we are interested in gets more focus and
                     * is easier to detect. This is why we need to keep the
                     * scroll low in order to catch potential oddities like
                     * this */
                    .filter(a -> THIS.getText(a).equals(CP_STRING))
                    .flatMap(THIS::rxClick)
                    .map(BooleanUtil::toTrue);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rxScrollableViewToSwipe() {
                return THIS.rxListView(ELEMENT);
            }

            @Override
            public void swipeOnce(@NotNull SwipeType param) {
                THIS.swipeOnce(param);
            }
        };

        return repeater.rxRepeatSwipe();
    }

    /**
     * Same as above, but uses a default {@link XPath} instance and a default
     * scroll ratio.
     * @param param A {@link DateType} instance.
     * @param element A {@link CalendarElement} instance.
     * @return A {@link Flowable} instance.
     * @see #rxScrollAndSelectComponent(DateType, CalendarElement, XPath, double)
     */
    @NotNull
    default Flowable<Boolean> rxScrollAndSelectComponent(
        @NotNull DateType param,
        @NotNull CalendarElement element
    ) {
        XPath xPath = XPath.builder(platform())
            .containsID(datePickerViewType().pickerViewId(element))
            .containsText(string(param, element))
            .build();

        return rxScrollAndSelectComponent(param, element, xPath, 0.5d);
    }

    /**
     * Select a year {@link String} by scrolling until it becomes visible.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see BaseDateActionType#rxSelectDate(DateType)
     * @see #rxScrollAndSelectComponent(DateType, CalendarElement)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxSelectYear(@NotNull DateType param) {
        return rxScrollAndSelectComponent(param, CalendarElement.YEAR);
    }

    /**
     * Select a month {@link String}. To be implemented when necessary.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see BaseDateActionType#rxSelectMonth(DateType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxSelectMonth(@NotNull DateType param) {
        return Flowable.just(true);
    }

    /**
     * Select a day.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see BaseDateActionType#rxSelectDay(DateType)
     * @see #rxCalibrateDate(DateType)
     * @see #rxScrollAndSelectComponent(DateType, CalendarElement)
     */
    @NotNull
    default Flowable<Boolean> rxSelectDay(@NotNull DateType param) {
        switch (datePickerViewType()) {
            case CALENDAR:
                /* In this case, the day selection is a bit different. We
                 * need to scroll the list view and check content description
                 * for the date String. We also need to continually click
                 * on a day to snap the list view into position */
                return rxCalibrateDate(param);

            case SPINNER:
                return rxScrollAndSelectComponent(param, CalendarElement.DAY);

            default:
                return RxUtil.error(UNKNOWN_DATE_VIEW_TYPE);
        }
    }

    /**
     * @param PARAM A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see BaseDateActionType#rxSelectDate(DateType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxSelectDate(@NotNull final DateType PARAM) {
        return rxOpenYearPicker()
            .flatMap(a -> rxSelectYear(PARAM))
            .flatMap(a -> rxOpenMonthPicker())
            .flatMap(a -> rxSelectMonth(PARAM))
            .flatMap(a -> rxOpenDayPicker())
            .flatMap(a -> rxSelectDay(PARAM))
            .flatMap(a -> rxHasDate(PARAM))
            .filter(BooleanUtil::isTrue)
            .switchIfEmpty(RxUtil.error(DATES_NOT_MATCHED));
    }
    //endregion

    //region Calendar WebElement
    /**
     * Get the list view that corresponds to a {@link CalendarElement}.
     * The implementations may change based on
     * {@link DatePickerType}.
     * @param element A {@link CalendarElement} instance.
     * @return A {@link Flowable} instance.
     * @see #rxCalendarListView()
     */
    @NotNull
    default Flowable<WebElement> rxListView(@NotNull CalendarElement element) {
        return rxCalendarListView();
    }

    /**
     * Get the {@link WebElement} that corresponds to a {@link CalendarElement}.
     * @param element A {@link CalendarElement} instance.
     * @return A {@link Flowable} instance.
     * @see DatePickerType#displayViewId(CalendarElement)
     */
    @NotNull
    default Flowable<WebElement> rxDisplayElement(@NotNull CalendarElement element) {
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

        return rxElementByXPath(param);
    }

    /**
     * Get the date picker header.
     * @return A {@link Flowable} instance.
     * @see #rxElementContainingID(String)
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerHeader() {
        return rxElementContainingID("date_picker_header");
    }

    /**
     * Get the date picker day label.
     * @return A {@link Flowable} instance.
     * @see #rxDisplayElement(CalendarElement)
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerDay() {
        return rxDisplayElement(CalendarElement.DAY);
    }

    /**
     * Get the date picker month label.
     * @return A {@link Flowable} instance.
     * @see #rxDisplayElement(CalendarElement)
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerMonth() {
        return rxDisplayElement(CalendarElement.DAY);
    }

    /**
     * Get the date picker year label.
     * @return A {@link Flowable} instance.
     * @see #rxDisplayElement(CalendarElement)
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerYear() {
        return rxDisplayElement(CalendarElement.YEAR);
    }

    /**
     * @return A {@link Flowable} instance.
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
     * Get a {@link CalendarElement}'s {@link String} representation of
     * {@link DateType#value()}.
     * @param param A {@link DateType} instance.
     * @param element A {@link CalendarElement} instance.
     * @return A {@link String} value.
     * @see DatePickerType#stringFormat(CalendarElement)
     */
    @NotNull
    default String string(@NotNull DateType param,
                          @NotNull CalendarElement element) {
        String format = datePickerViewType().stringFormat(element);
        return new SimpleDateFormat(format).format(param.value());
    }

    /**
     * Get the day, as formatted using {@link DatePickerType#dayFormat()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #string(DateType, CalendarElement)
     */
    @NotNull
    default String dayString(@NotNull DateType param) {
        return string(param, CalendarElement.DAY);
    }

    /**
     * Get the month, as formatted using {@link DatePickerType#monthFormat()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #string(DateType, CalendarElement)
     */
    @NotNull
    default String monthString(@NotNull DateType param) {
        return string(param, CalendarElement.MONTH);
    }

    /**
     * Get the year, as formatted using {@link DatePickerType#yearFormat()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #string(DateType, CalendarElement)
     */
    @NotNull
    default String yearString(@NotNull DateType param) {
        return string(param, CalendarElement.YEAR);
    }
    //endregion

    //region Displayed Date
    /**
     * Get the {@link Integer} value that represents the {@link CalendarElement}
     * as displayed by the relevant {@link WebElement}. For e.g., if we are
     * searching for {@link CalendarElement#DAY}, we need to look at
     * {@link #rxDatePickerDay()}.
     * @param element A {@link CalendarElement} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    @SuppressWarnings("MagicConstant")
    default Flowable<Integer> rxDisplayedComponent(@NotNull CalendarElement element) {
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
     * @return An {@link Flowable} value.
     * @see #rxDisplayedComponent(CalendarElement)
     */
    @NotNull
    default Flowable<Integer> rxDisplayedDay() {
        return rxDisplayedComponent(CalendarElement.DAY);
    }

    /**
     * Get the month as displayed by the {@link WebElement} emitted by
     * {@link #rxDatePickerMonth()}.
     * @return A {@link Flowable} instance.
     * @see #rxDisplayedComponent(CalendarElement)
     */
    @NotNull
    default Flowable<Integer> rxDisplayedMonth() {
        return rxDisplayedComponent(CalendarElement.MONTH);
    }

    /**
     * Get the year as displayed by the {@link WebElement} emitted by
     * {@link #rxDatePickerYear()}.
     * @return A {@link Flowable} instance.
     * @see #rxDisplayedComponent(CalendarElement)
     */
    @NotNull
    default Flowable<Integer> rxDisplayedYear() {
        return rxDisplayedComponent(CalendarElement.YEAR);
    }

    /**
     * Get the {@link Date} as displayed by the date picker.
     * @return A {@link Flowable} instance.
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
                calendar.set(CalendarElement.DAY.value(), day);
                calendar.set(CalendarElement.MONTH.value(), month);
                calendar.set(CalendarElement.YEAR.value(), year);
                return calendar;
            }).map(Calendar::getTime);
    }
    //endregion
}
