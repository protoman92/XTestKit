package org.swiften.xtestkit.mobile.android.element.action.date.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.element.action.date.CalendarElement;
import org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.type.DateType;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.locator.xpath.Attribute;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.base.param.ByXPath;
import org.swiften.xtestkit.base.param.SwipeGestureParam;
import org.swiften.xtestkit.base.type.ClassContainerType;
import org.swiften.xtestkit.base.type.SwipeGestureType;
import org.swiften.xtestkit.mobile.android.AndroidView;
import org.swiften.xtestkit.mobile.android.element.property.type.AndroidElementInteractionType;
import org.swiften.xtestkit.mobile.android.type.DatePickerViewContainerType;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileActionType;

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
    MobileActionType<AndroidDriver<AndroidElement>>,
    BaseDateActionType<AndroidDriver<AndroidElement>>,
    AndroidElementInteractionType,
    DatePickerViewContainerType
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
     * Scroll the calendar list view to a new page or the previous page.
     * Only applicable to {@link DatePickerViewType#CALENDAR}.
     * @param element The calendar list view {@link WebElement}.
     * @param direction A {@link Unidirection} instance.
     * @return A {@link Flowable} instance.
     * @see #rxSwipeOnce(SwipeGestureType)
     */
    @NotNull
    default Flowable<Boolean> rxScrollListView(
        @NotNull WebElement element,
        @NotNull Unidirection direction
    ) {
        Dimension dimension = element.getSize();
        Point location = element.getLocation();
        double height = dimension.getHeight();
        int startX = location.getX() + dimension.getWidth() / 2;
        int startY = 0, endY = 0;

        /* Do not perform a full vertical scroll from top-bottom or bottom-top
         * because we may overshoot. Rather, perform short swipes and
         * repeatedly check for the wanted component */
        double scrollRatio = 0.5d;

        /* Depending on the swipe direction, we need to have different
         * startY and endY values. The direction corresponds to whether the
         * year being searched is after or before the current selected year */
        switch (direction) {
            case UP_DOWN:
                endY = (int)(location.getY() + height);
                startY = (int)(endY - height * scrollRatio);
                break;

            case DOWN_UP:
                endY = location.getY();
                startY = (int)(endY + height * scrollRatio);
                break;

            default:
                break;
        }

        SwipeGestureType param = SwipeGestureParam.builder()
            .withStartX(startX)
            .withEndX(startX)
            .withStartY(startY)
            .withEndY(endY)
            .build();

        return rxSwipeOnce(param);
    }

    /**
     * Open the year picker.
     * @return A {@link Flowable} instance.
     * @see #rxDatePickerYear()
     */
    @NotNull
    default Flowable<Boolean> rxOpenYearPicker() {
        return rxDatePickerYear().flatMap(this::rxClick);
    }

    /**
     * Select a component by scrolling until the component {@link String} is
     * visible.
     * @param PARAM A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see #rxListView(CalendarElement)
     * @see #rxScrollListView(WebElement, Unidirection)
     */
    @NotNull
    default Flowable<Boolean> rxSelectComponent(
        @NotNull final DateType PARAM,
        @NotNull final CalendarElement ELEMENT
    ) {
        final AndroidDateActionType THIS = this;
        final int COMPONENT = PARAM.component(ELEMENT);
        final String CP_STRING = string(PARAM, ELEMENT);

        XPath xPath = XPath.builder(platform())
            .containsID(datePickerViewType().pickerViewId(ELEMENT))
            .containsText(CP_STRING)
            .build();

        /* We need a custom ByXPath because we want to limit the retry
         * count. Otherwise, the scroll action will take quite a long time as
         * we need to recursively scroll and check for the existence of the
         * element. Consider this trade-off between time and accuracy */
        final ByXPath BY_XPATH = ByXPath.builder()
            .withXPath(xPath)
            .withError(NO_SUCH_ELEMENT)
            .withRetryCount(0)
            .build();

        /* Keep scrolling and checking until the component comes into focus */
        class ScrollAndCheck {
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Flowable<Boolean> repeat(@NotNull final Unidirection DIRECTION) {
                return THIS.rxElementsByXPath(BY_XPATH)
                    /* Sometimes the driver will get the wrong element. In
                     * this case, keep scrolling so that in the next scroll,
                     * the element we are interested in gets more focus and
                     * is easier to detect. This is why we need to keep the
                     * scroll low in order to catch potential oddities like
                     * this */
                    .filter(a -> THIS.getText(a).equals(CP_STRING))
                    .flatMap(THIS::rxClick)
                    .switchIfEmpty(Flowable.error(new Exception()))
                    .onErrorResumeNext(rxListView(ELEMENT)
                        .flatMap(a -> THIS.rxScrollListView(a, DIRECTION))
                        /* Recursively repeat until the we reach the component
                         * we want */
                        .flatMap(a -> new ScrollAndCheck().repeat(DIRECTION))
                    );
            }
        }

        /* We first need to determine the scroll direction */
        return rxDisplayedComponent(ELEMENT)
            .map(a -> a > COMPONENT)
            .map(a -> a ? Unidirection.UP_DOWN : Unidirection.DOWN_UP)
            .flatMap(new ScrollAndCheck()::repeat);
    }

    /**
     * Select a day if the app is using {@link DatePickerViewType#CALENDAR}.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxSelectDayForCalendarType(@NotNull DateType param) {
        final AndroidDateActionType THIS = this;
        String format = "dd MMMM YYYY";
        final String DATE = param.dateString(format);
        final int MONTH = param.month();

        /* Weirdly enough, the individual view element that contains the day
         * values use content description to store the day */
        Attribute contentDesc = Attribute.withSingleAttribute("content-desc");

        /* Construct a ByXPath with the date String we are trying to find */
        XPath xPathOfInterest = XPath.builder(platform())
            .appendAttribute(contentDesc, (XPath.ContainsString) () -> DATE)
            .build();

        final ByXPath BY_XPATH_OF_INTEREST = ByXPath.builder()
            .withXPath(xPathOfInterest)
            .withRetryCount(0)
            .withError(NO_SUCH_ELEMENT)
            .build();

        /* Construct a default ByXPath that is confirmed to be found. If the
         * element of interest is not found, we search for this and click
         * it to snap the calendar view into place */
        XPath defaultXPath = XPath.builder(platform())
            .appendAttribute(contentDesc, (XPath.ContainsString) () -> "28")
            .build();

        final ByXPath DEFAULT_BY_XPATH = ByXPath.builder()
            .withXPath(defaultXPath)
            .withError(NO_SUCH_ELEMENT)
            .build();

        class ScrollAndClick {
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Flowable<Boolean> repeat(@NotNull Unidirection DIRECTION) {
                return THIS.rxElementByXPath(DEFAULT_BY_XPATH)
                    .flatMap(THIS::rxClick)
                    .flatMap(a -> THIS.rxElementsByXPath(BY_XPATH_OF_INTEREST)
                        .flatMap(THIS::rxClick)
                        .onErrorResumeNext(rxCalendarListView()
                            .flatMap(b -> THIS.rxScrollListView(b, DIRECTION))
                            .flatMap(b -> new ScrollAndClick().repeat(DIRECTION))));
            }
        }

        return rxDisplayedMonth()
            .map(a -> a > MONTH)
            .map(a -> a ? Unidirection.UP_DOWN : Unidirection.DOWN_UP)
            .flatMap(new ScrollAndClick()::repeat);
    }

    /**
     * Select a year {@link String} by scrolling until it becomes visible.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see #rxSelectComponent(DateType, CalendarElement)
     */
    @NotNull
    default Flowable<Boolean> rxSelectYear(@NotNull DateType param) {
        return rxSelectComponent(param, CalendarElement.YEAR);
    }

    /**
     * Select a month {@link String}. To be implemented when necessary.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxSelectMonth(@NotNull DateType param) {
        return Flowable.just(true);
    }

    /**
     * Select a day.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see #rxSelectDayForCalendarType(DateType)
     * @see #rxSelectComponent(DateType, CalendarElement)
     */
    @NotNull
    default Flowable<Boolean> rxSelectDay(@NotNull DateType param) {
        switch (datePickerViewType()) {
            case CALENDAR:
                /* In this case, the day selection is a bit different. We
                 * need to scroll the list view and check content description
                 * for the date String. We also need to continually click
                 * on a day to snap the list view into position */
                return rxSelectDayForCalendarType(param);

            case SPINNER:
                return rxSelectComponent(param, CalendarElement.DAY);

            default:
                return Flowable.error(new Exception(UNKNOWN_DATE_VIEW_TYPE));
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
            .flatMap(a -> rxSelectMonth(PARAM));
    }
    //endregion

    //region Calendar WebElement
    /**
     * Get the calendar list view. Applicable to {@link DatePickerViewType#CALENDAR}.
     * @return A {@link Flowable} instance.
     * @see #rxElementOfClass(ClassContainerType)
     */
    @NotNull
    default Flowable<WebElement> rxCalendarListView() {
        return rxElementOfClass(AndroidView.ViewType.LIST_VIEW);
    }

    /**
     * Get the list view that corresponds to a {@link CalendarElement}.
     * The implementations may change based on
     * {@link DatePickerViewType}.
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
     * @see DatePickerViewType#displayViewId(CalendarElement)
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
     * @see DatePickerViewType#stringFormat(CalendarElement)
     */
    @NotNull
    default String string(@NotNull DateType param,
                          @NotNull CalendarElement element) {
        String format = datePickerViewType().stringFormat(element);
        return new SimpleDateFormat(format).format(param.value());
    }

    /**
     * Get the day, as formatted using {@link DatePickerViewType#dayFormat()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #string(DateType, CalendarElement)
     */
    @NotNull
    default String dayString(@NotNull DateType param) {
        return string(param, CalendarElement.DAY);
    }

    /**
     * Get the month, as formatted using {@link DatePickerViewType#monthFormat()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #string(DateType, CalendarElement)
     */
    @NotNull
    default String monthString(@NotNull DateType param) {
        return string(param, CalendarElement.MONTH);
    }

    /**
     * Get the year, as formatted using {@link DatePickerViewType#yearFormat()}.
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
     * @see #rxDisplayedDay()
     * @see #rxDisplayedMonth()
     * @see #rxDisplayedYear()
     */
    @NotNull
    @SuppressWarnings("MagicConstant")
    default Flowable<Date> rxDisplayedDate() {
        return Flowable
            .combineLatest(
                rxDisplayedDay(),
                rxDisplayedMonth(),
                rxDisplayedYear(),
                (day, month, year) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(CalendarElement.DAY.value(), day);
                    calendar.set(CalendarElement.MONTH.value(), month);
                    calendar.set(CalendarElement.YEAR.value(), year);
                    return calendar;
                })
            .map(Calendar::getTime);
    }
    //endregion
}
