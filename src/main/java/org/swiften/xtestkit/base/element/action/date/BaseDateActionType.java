package org.swiften.xtestkit.base.element.action.date;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.collection.Zip;
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
import java.util.List;
import java.util.stream.Collectors;

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
    BaseSwipeType<D>
{
    //region Validation
    /**
     * Check if {@link Date} is currently active. This assumes that the
     * user is in a calendar view.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see DateType#calendarUnits()
     * @see ObjectUtil#nonNull(Object)
     * @see #string(DateType, CalendarUnit)
     * @see #rxe_containsText(String...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<Boolean> rxv_hasDate(@NotNull DateType param) {
        final BaseDateActionType<?> THIS = this;
        List<CalendarUnit> units = param.calendarUnits();

        return Flowable
            .fromIterable(units)
            .map(a -> THIS.string(param, a))
            .flatMap(THIS::rxe_containsText)
            .all(ObjectUtil::nonNull)
            .toFlowable();
    }
    //endregion

    //region Actions
    /**
     * Open the picker view that corresponds to {@link CalendarUnit}.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxa_openPicker(@NotNull DateType param,
                                     @NotNull CalendarUnit unit);

    /**
     * Select {@link Date}. This assumes that the user is in a calendar view.
     * @param PARAM {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#isTrue(boolean)
     * @see DateType#calendarUnits()
     * @see ObjectUtil#nonNull(Object)
     * @see #rxa_openPicker(DateType, CalendarUnit)
     * @see #rxa_select(DateType, CalendarUnit)
     * @see #rxv_hasDate(DateType)
     * @see RxUtil#error(String)
     * @see #DATES_NOT_MATCHED
     */
    @NotNull
    default Flowable<Boolean> rxa_selectDate(@NotNull final DateType PARAM) {
        LogUtil.printfThread("Selecting %s", dateString(PARAM));

        final BaseDateActionType<?> THIS = this;

        return Flowable
            .fromIterable(PARAM.calendarUnits())
            .concatMap(a -> THIS.rxa_openPicker(PARAM, a)
                .flatMap(b -> THIS.rxa_select(PARAM, a)))
            .all(ObjectUtil::nonNull)
            .toFlowable()
            .flatMap(a -> THIS.rxv_hasDate(PARAM))
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
    Flowable<Boolean> rxa_select(@NotNull DateType param, @NotNull CalendarUnit unit);
    //endregion

    //region Elements
    /**
     * Get the list view that corresponds to {@link CalendarUnit}.
     * The implementations may change based on {@link DatePickerType}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateType#datePickerType()
     * @see DatePickerType#pickerViewXPath(CalendarUnit)
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    default Flowable<WebElement> rxe_pickerView(@NotNull DateType param,
                                                @NotNull CalendarUnit unit) {
        XPath xPath = param.datePickerType().pickerViewXPath(unit);
        return rxe_withXPath(xPath).firstElement().toFlowable();
    }

    /**
     * Get the {@link WebElement} that corresponds to {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateType#datePickerType()
     * @see DatePickerType#unitLabelViewXPath(CalendarUnit)
     * @see #rxe_byXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rxe_elementLabel(@NotNull DateType param,
                                                  @NotNull CalendarUnit unit) {
        /* We need to use a custom XPath query because there are some views
         * that have similar IDs (e.g. if we search for date_picker_month, the
         * resulting WebElement may not be the one we are looking for */
        XPath xPath = param.datePickerType().unitLabelViewXPath(unit);
        ByXPath byXPath = ByXPath.builder().withXPath(xPath).build();
        return rxe_byXPath(byXPath).firstElement().toFlowable();
    }

    /**
     * Get the {@link Integer} value that represents the {@link CalendarUnit}
     * as displayed by the relevant {@link WebElement}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateType#datePickerType()
     * @see DatePickerType#stringFormat(CalendarUnit)
     * @see #rxe_elementLabel(DateType, CalendarUnit)
     */
    @NotNull
    @SuppressWarnings("MagicConstant")
    default Flowable<Integer> rxe_displayedUnit(@NotNull DateType param,
                                                @NotNull CalendarUnit unit) {
        final BaseDateActionType<?> THIS = this;
        String format = param.datePickerType().stringFormat(unit);
        final SimpleDateFormat FORMATTER = new SimpleDateFormat(format);
        final Integer CALENDAR_CONSTANT = unit.value();

        return rxe_elementLabel(param, unit)
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
     * @see #rxe_displayedUnit(DateType, CalendarUnit)
     */
    @NotNull
    @SuppressWarnings({"MagicConstant", "ConstantConditions"})
    default Flowable<Date> rxe_displayedDate(@NotNull final DateType PARAM) {
        final BaseDateActionType<?> THIS = this;

        return Flowable.fromIterable(PARAM.calendarUnits())
            .flatMap(a -> THIS.rxe_displayedUnit(PARAM, a).map(b -> new Zip<>(a, b)))
            .reduce(Calendar.getInstance(), (a, b) -> {
                a.set(b.A.value(), b.B); return a;
            })
            .map(Calendar::getTime)
            .toFlowable();
    }
    //endregion

    /**
     * Get {@link CalendarUnit}'s {@link String} representation of
     * {@link DateType#date()}.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see DateType#datePickerType()
     * @see DatePickerType#stringFormat(CalendarUnit)
     * @see CalendarUnit#value()
     */
    @NotNull
    default String string(@NotNull DateType param, @NotNull CalendarUnit unit) {
        Date date = param.date();
        String format = param.datePickerType().stringFormat(unit);
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * Get {@link String} representation of {@link DateType#date()}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #string(DateType, CalendarUnit)
     */
    @NotNull
    default String dateString(@NotNull DateType param) {
        List<String> params = param.calendarUnits().stream()
            .map(a -> String.format("%s %s", a, string(param, a)))
            .collect(Collectors.toList());

        return String.join(" ", params);
    }
}
