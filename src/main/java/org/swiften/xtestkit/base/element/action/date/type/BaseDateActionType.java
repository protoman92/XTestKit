package org.swiften.xtestkit.base.element.action.date.type;

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
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.type.BaseErrorType;
import sun.plugin2.os.windows.FLASHWINFO;

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
    BaseDateErrorType,
    BaseErrorType,
    BaseLocatorType<D>
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
        LogUtil.printfThread("Selecting date %s", dateString(PARAM));

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
    //endregion

    //region Elements
    /**
     * Get the {@link WebElement} that corresponds to {@link CalendarUnit}.
     * @param element {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rx_element(@NotNull CalendarUnit element);

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
     */
    @NotNull
    @SuppressWarnings("MagicConstant")
    Flowable<Integer> rx_displayedComponent(@NotNull CalendarUnit element);

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
     * @param element {@link CalendarUnit} instance.
     * @return {@link String} value.
     */
    @NotNull
    String string(@NotNull DateType param, @NotNull CalendarUnit element);

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
