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
     * @see #rxe_containsText(String...)
     * @see #dayString(DateType)
     * @see #monthString(DateType)
     * @see #yearString(DateType)
     * @see ObjectUtil#nonNull(Object)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<Boolean> rx_hasDate(@NotNull DateType param) {
        return Maybe
            .mergeArray(
                rxe_containsText(dayString(param)).firstElement(),
                rxe_containsText(monthString(param)).firstElement(),
                rxe_containsText(yearString(param)).firstElement()
            )
            .all(ObjectUtil::nonNull)
            .toFlowable();
    }
    //endregion

    //region Actions
    /**
     * Open the picker view that corresponds to {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_openPicker(@NotNull CalendarUnit unit);

    /**
     * Select {@link Date}. This assumes that the user is in a calendar view.
     * @param PARAM {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see #rx_openPicker(CalendarUnit)
     * @see #rx_select(DateType, CalendarUnit)
     * @see #rx_hasDate(DateType)
     * @see BooleanUtil#isTrue(boolean)
     * @see #DATES_NOT_MATCHED
     */
    @NotNull
    default Flowable<Boolean> rx_selectDate(@NotNull final DateType PARAM) {
        LogUtil.printfThread("Selecting %s", dateString(PARAM));

        final BaseDateActionType<?> THIS = this;

        return Flowable
            .fromArray(CalendarUnit.YEAR, CalendarUnit.MONTH, CalendarUnit.DAY)
            .concatMap(a -> THIS.rx_openPicker(a).flatMap(b -> THIS.rx_select(PARAM, a)))
            .all(ObjectUtil::nonNull)
            .toFlowable()
            .flatMap(a -> THIS.rx_hasDate(PARAM))
            .filter(BooleanUtil::isTrue)
            .switchIfEmpty(RxUtil.error(DATES_NOT_MATCHED));
    }

    /**
     * Select a component based on {@link CalendarUnit} and {@link DateType}.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_select(@NotNull DateType param,
                                @NotNull CalendarUnit unit);
    //endregion

    //region Elements
    /**
     * Get the list view that corresponds to {@link CalendarUnit}.
     * The implementations may change based on
     * {@link org.swiften.xtestkit.base.element.action.date.DatePickerContainerType.DatePickerType}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #datePickerType()
     * @see DatePickerType#pickerViewXPath(CalendarUnit)
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    default Flowable<WebElement> rx_pickerView(@NotNull CalendarUnit unit) {
        XPath xPath = datePickerType().pickerViewXPath(unit);
        return rxe_withXPath(xPath).firstElement().toFlowable();
    }

    /**
     * Get the {@link WebElement} that corresponds to {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #datePickerType()
     * @see DatePickerContainerType.DatePickerType#unitLabelViewXPath(CalendarUnit)
     * @see #rxe_byXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rx_elementLabel(@NotNull CalendarUnit unit) {
        /* We need to use a custom XPath query because there are some views
         * that have similar IDs (e.g. if we search for date_picker_month, the
         * resulting WebElement may not be the one we are looking for */
        XPath xPath = datePickerType().unitLabelViewXPath(unit);
        ByXPath param = ByXPath.builder().withXPath(xPath).build();
        return rxe_byXPath(param).firstElement().toFlowable();
    }

    /**
     * Get all calendar {@link WebElement}. This assumes that the user is in a
     * calendar view.
     * @return {@link Flowable} instance.
     */
    @NotNull Flowable<WebElement> rx_allCalendarElements();

    /**
     * Get the {@link Integer} value that represents the {@link CalendarUnit}
     * as displayed by the relevant {@link WebElement}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #datePickerType()
     * @see DatePickerType#stringFormat(CalendarUnit)
     * @see #rx_elementLabel(CalendarUnit)
     */
    @NotNull
    @SuppressWarnings("MagicConstant")
    default Flowable<Integer> rx_displayedUnit(@NotNull CalendarUnit unit) {
        final BaseDateActionType<?> THIS = this;
        String format = datePickerType().stringFormat(unit);
        final SimpleDateFormat FORMATTER = new SimpleDateFormat(format);
        final Integer CALENDAR_CONSTANT = unit.value();

        return rx_elementLabel(unit)
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
     * Get the {@link Date} as displayed by the date picker.
     * @return {@link Flowable} instance.
     * @see #rx_displayedUnit(CalendarUnit)
     */
    @NotNull
    @SuppressWarnings("MagicConstant")
    default Flowable<Date> rx_displayedDate() {
        return Flowable.zip(
            rx_displayedUnit(CalendarUnit.DAY),
            rx_displayedUnit(CalendarUnit.MONTH),
            rx_displayedUnit(CalendarUnit.YEAR),
            (day, month, year) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(CalendarUnit.DAY.value(), day);
                calendar.set(CalendarUnit.MONTH.value(), month);
                calendar.set(CalendarUnit.YEAR.value(), year);
                return calendar;
            })
            .map(Calendar::getTime);
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
        Date date = param.value();
        String format = datePickerType().stringFormat(unit);
        return new SimpleDateFormat(format).format(date);
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
