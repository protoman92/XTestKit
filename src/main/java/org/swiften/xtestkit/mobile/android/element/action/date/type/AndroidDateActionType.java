package org.swiften.xtestkit.mobile.android.element.action.date.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.element.action.date.CalendarElement;
import org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.type.DateType;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.base.param.ByXPath;
import org.swiften.xtestkit.base.type.ClassContainerType;
import org.swiften.xtestkit.mobile.android.AndroidView;
import org.swiften.xtestkit.mobile.android.element.property.type.AndroidElementInteractionType;
import org.swiften.xtestkit.mobile.android.type.DateViewContainerType;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by haipham on 5/8/17.
 */
public interface AndroidDateActionType extends
    BaseDateActionType<AndroidDriver<AndroidElement>>,
    AndroidElementInteractionType,
    DateViewContainerType
{
    /**
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see BaseDateActionType#rxSelectDate(DateType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxSelectDate(@NotNull DateType param) {
        return rxDisplayedDate()
            .doOnNext(LogUtil::println)
            .map(a -> true);
    }

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

    //region Calendar WebElement.
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
     * Get the calendar list view. Applicable to {@link DateViewType#CALENDAR}.
     * @return A {@link Flowable} instance.
     * @see #rxElementOfClass(ClassContainerType)
     */
    @NotNull
    default Flowable<WebElement> rxCalendarListView() {
        return rxElementOfClass(AndroidView.ViewType.LIST_VIEW);
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
    default Flowable<Integer> rxDisplayedElement(@NotNull CalendarElement element) {
        String format = dateViewType().stringFormat(element);
        final SimpleDateFormat FORMATTER = new SimpleDateFormat(format);
        final Integer CALENDAR_CONSTANT = element.value();

        return rxDisplayElement(element)
            .map(this::getText)
            .doOnNext(LogUtil::println)
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
     * @see #rxDisplayedElement(CalendarElement)
     */
    @NotNull
    default Flowable<Integer> rxDisplayedDay() {
        return rxDisplayedElement(CalendarElement.DAY);
    }

    /**
     * Get the month as displayed by the {@link WebElement} emitted by
     * {@link #rxDatePickerMonth()}.
     * @return A {@link Flowable} instance.
     * @see #rxDisplayedElement(CalendarElement)
     */
    @NotNull
    default Flowable<Integer> rxDisplayedMonth() {
        return rxDisplayedElement(CalendarElement.MONTH);
    }

    /**
     * Get the year as displayed by the {@link WebElement} emitted by
     * {@link #rxDatePickerYear()}.
     * @return A {@link Flowable} instance.
     * @see #rxDisplayedElement(CalendarElement)
     */
    @NotNull
    default Flowable<Integer> rxDisplayedYear() {
        return rxDisplayedElement(CalendarElement.YEAR);
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
