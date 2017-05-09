package org.swiften.xtestkit.mobile.android.element.action.date.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.element.action.date.CalendarElement;
import org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.type.DateType;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.base.param.ByXPath;
import org.swiften.xtestkit.base.param.SwipeGestureParam;
import org.swiften.xtestkit.base.param.TextParam;
import org.swiften.xtestkit.base.type.ClassContainerType;
import org.swiften.xtestkit.base.type.SwipeGestureType;
import org.swiften.xtestkit.mobile.android.AndroidView;
import org.swiften.xtestkit.mobile.android.element.property.type.AndroidElementInteractionType;
import org.swiften.xtestkit.mobile.android.type.DateViewContainerType;
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
    DateViewContainerType
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
     * Only applicable to {@link DateViewContainerType.DateViewType#CALENDAR}.
     * @param element The calendar list view {@link WebElement}.
     * @param direction A {@link Unidirection} instance.
     * @return A {@link Flowable} instance.
     * @see #rxSwipeOnce(SwipeGestureType)
     */
    @NotNull
    default Flowable<Boolean> rxScrollCalendarListView(
        @NotNull WebElement element,
        @NotNull Unidirection direction
    ) {
        Dimension dimension = element.getSize();
        Point location = element.getLocation();
        double height = dimension.getHeight();
        int startX = location.getX() + dimension.getWidth() / 2, endX = startX;
        int startY = 0, endY = 0;

        /* Do not perform a full vertical scroll from top-bottom or bottom-top
         * because we may overshoot. Rather, perform short swipes and
         * repeatedly check for the wanted component */
        double scrollRatio = 0.7d;

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
            .withEndX(endX)
            .withStartY(startY)
            .withEndY(endY)
            .build();

        return rxSwipeOnce(param);
    }

    /**
     * Scroll the year view to a new page or the previous page. Depending on
     * {@link org.swiften.xtestkit.mobile.android.type.DateViewContainerType.DateViewType},
     * different implementations may be required.
     * @param element The year {@link WebElement}.
     * @param direction A {@link Unidirection} instance.
     * @return A {@link Flowable} instance.
     * @see #rxScrollCalendarListView(WebElement, Unidirection)
     */
    @NotNull
    default Flowable<Boolean> rxScrollYearListView(
        @NotNull WebElement element,
        @NotNull Unidirection direction
    ) {
        switch (dateViewType()) {
            case CALENDAR:
                return rxScrollCalendarListView(element, direction);

            default:
                return Flowable.error(new Exception(UNKNOWN_DATE_VIEW_TYPE));
        }
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
     * Select a year by scrolling until the year is visible, assuming the
     * user is already in the year picker view.
     * @param PARAM A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see #rxCalendarListView()
     */
    @NotNull
    default Flowable<Boolean> rxSelectYear(final DateType PARAM) {
        final AndroidDateActionType THIS = this;
        final int YEAR = PARAM.component(CalendarElement.YEAR);
        String yearString = yearString(PARAM);

        /* We need a custom TextParam because we want to limit the retry
         * count. Otherwise, the scroll action will take quite a long time as
         * we need to recursively scroll and check for the existence of the
         * element. Consider this trade-off between time and accuracy */
        TextParam param = TextParam.builder()
            .withText(yearString)
            .withRetries(0)
            .build();

        /* Temporarily break the stream here to get the correct direction
         * to scroll. Otherwise the Flowable will get rather complicated. */
        final Unidirection DIRECTION = rxDisplayedYear()
            .map(a -> a > YEAR)
            .map(a -> a ? Unidirection.UP_DOWN : Unidirection.DOWN_UP)
            .blockingFirst();

        class ScrollAndCheck {
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Flowable<Boolean> scrollAndCheck() {
                return rxElementContainingText(param)
                    .flatMap(THIS::rxClick)
                    .onErrorResumeNext(rxYearListView()
                        .flatMap(a -> THIS.rxScrollYearListView(a, DIRECTION)))

                    /* Recursively repeat until the we reach the component
                     * we want */
                    .flatMap(a -> new ScrollAndCheck().scrollAndCheck());
            }
        }

        return new ScrollAndCheck().scrollAndCheck();
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
            .flatMap(a -> rxSelectYear(PARAM));
    }
    //endregion

    //region Calendar WebElement
    /**
     * Get the calendar list view. Applicable to {@link DateViewType#CALENDAR}.
     * @return A {@link Flowable} instance.
     * @see #rxElementOfClass(ClassContainerType)
     */
    @NotNull
    default Flowable<WebElement> rxCalendarListView() {
        return rxElementOfClass(AndroidView.ViewType.LIST_VIEW);
    }

    /**
     * Get the year list view. The implementations may change based on
     * {@link org.swiften.xtestkit.mobile.android.type.DateViewContainerType.DateViewType}.
     * @return A {@link Flowable} instance.
     * @see #rxCalendarListView()
     */
    @NotNull
    default Flowable<WebElement> rxYearListView() {
        return rxCalendarListView();
    }

    /**
     * Get the {@link WebElement} that corresponds to a {@link CalendarElement}.
     * @param element A {@link CalendarElement} instance.
     * @return A {@link Flowable} instance.
     * @see DateViewContainerType.DateViewType#viewId(CalendarElement)
     */
    @NotNull
    default Flowable<WebElement> rxDisplayElement(@NotNull CalendarElement element) {
        /* We need to use a custom XPath query because there are some views
         * that have similar IDs (e.g. if we search for date_picker_month, the
         * resulting WebElement may not be the one we are looking for */
        XPath xPath = newXPathBuilder()
            .ofClass(AndroidView.ViewType.TEXT_VIEW.className())
            .containsID(dateViewType().viewId(element))
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

        if (dateViewType().isCalendarType()) {
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
     * @see DateViewContainerType.DateViewType#stringFormat(CalendarElement)
     */
    @NotNull
    default String string(@NotNull DateType param,
                          @NotNull CalendarElement element) {
        String format = dateViewType().stringFormat(element);
        return new SimpleDateFormat(format).format(param.value());
    }

    /**
     * Get the day, as formatted using {@link DateViewType#dayFormat()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #string(DateType, CalendarElement)
     */
    @NotNull
    default String dayString(@NotNull DateType param) {
        return string(param, CalendarElement.DAY);
    }

    /**
     * Get the month, as formatted using {@link DateViewType#monthFormat()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #string(DateType, CalendarElement)
     */
    @NotNull
    default String monthString(@NotNull DateType param) {
        return string(param, CalendarElement.MONTH);
    }

    /**
     * Get the year, as formatted using {@link DateViewType#yearFormat()}.
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
        String format = dateViewType().stringFormat(element);
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
