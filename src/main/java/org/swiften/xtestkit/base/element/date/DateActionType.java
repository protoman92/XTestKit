package org.swiften.xtestkit.base.element.date;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.collection.Zip;
import org.swiften.javautilities.util.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.click.ClickActionType;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.swiften.xtestkit.base.element.locator.ByXPath;
import org.swiften.xtestkit.base.element.locator.LocatorType;
import org.swiften.xtestkit.base.element.property.ElementPropertyType;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
import org.swiften.xtestkitcomponents.xpath.XPath;

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
public interface DateActionType<D extends WebDriver> extends
    ClickActionType<D>,
    DateActionErrorType,
    ElementPropertyType,
    BaseErrorType,
    LocatorType<D>,
    SwipeType<D>
{
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
     * Select a component based on {@link CalendarUnit} and {@link DateType}.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxa_select(@NotNull DateType param, @NotNull CalendarUnit unit);

    /**
     * Check if {@link Date} is currently active. This assumes that the
     * user is in a calendar view.
     * @param PARAM {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see DateType#units()
     * @see ObjectUtil#nonNull(Object)
     * @see #valueString(DateType, CalendarUnit)
     * @see #rxe_containsText(String...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<Boolean> rxv_hasDate(@NotNull final DateType PARAM) {
        final DateActionType<?> THIS = this;
        List<CalendarUnit> units = PARAM.units();

        return Flowable
            .fromIterable(units)
            .map(a -> THIS.displayString(PARAM, a))
            .flatMap(THIS::rxe_containsText)
            .all(ObjectUtil::nonNull)
            .toFlowable();
    }

    /**
     * Select {@link Date}. This assumes that the user is in a calendar view.
     * @param PARAM {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#isTrue(boolean)
     * @see DateType#units()
     * @see ObjectUtil#nonNull(Object)
     * @see RxUtil#error(String)
     * @see #rxa_openPicker(DateType, CalendarUnit)
     * @see #rxa_select(DateType, CalendarUnit)
     * @see #rxv_hasDate(DateType)
     * @see #DATES_NOT_MATCHED
     */
    @NotNull
    default Flowable<Boolean> rxa_selectDate(@NotNull final DateType PARAM) {
        LogUtil.printft("Selecting %s", dateString(PARAM));

        final DateActionType<?> THIS = this;

        return Flowable
            .fromIterable(PARAM.units())
            .concatMap(a -> THIS.rxa_openPicker(PARAM, a)
                .flatMap(b -> THIS.rxa_select(PARAM, a)))
            .all(ObjectUtil::nonNull)
            .toFlowable()
            .flatMap(a -> THIS.rxv_hasDate(PARAM))
            .filter(BooleanUtil::isTrue)
            .switchIfEmpty(RxUtil.error(DATES_NOT_MATCHED));
    }

    /**
     * Get the list view that corresponds to {@link CalendarUnit}.
     * The implementations may change based on {@link DatePickerType}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateType#datePickerType()
     * @see DatePickerType#pickerViewXP(CalendarUnit)
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    default Flowable<WebElement> rxe_pickerView(@NotNull DateType param,
                                                @NotNull CalendarUnit unit) {
        XPath xpath = param.datePickerType().pickerViewXP(unit);
        return rxe_withXPath(xpath).firstElement().toFlowable();
    }

    /**
     * Get the {@link WebElement} that corresponds to {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateType#datePickerType()
     * @see DatePickerType#unitLabelViewXPath(CalendarUnit)
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    default Flowable<WebElement> rxe_elementLabel(@NotNull DateType param,
                                                  @NotNull CalendarUnit unit) {
        /* We need to use a custom XPath query because there are some views
         * that have similar IDs (e.g. if we search for date_picker_month, the
         * resulting WebElement may not be the one we are looking for */
        XPath xpath = param.datePickerType().unitLabelViewXPath(unit);
        ByXPath byXPath = ByXPath.builder().withXPath(xpath).build();
        return rxe_withXPath(xpath).firstElement().toFlowable();
    }

    /**
     * Get the {@link Integer} value that represents the {@link CalendarUnit}
     * as displayed by the relevant {@link WebElement}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see CalendarUnit#value()
     * @see DateType#datePickerType()
     * @see DatePickerType#valueStringFormat(CalendarUnit)
     * @see #rxe_elementLabel(DateType, CalendarUnit)
     */
    @NotNull
    @SuppressWarnings("MagicConstant")
    default Flowable<Integer> rxe_displayedUnit(@NotNull DateType param,
                                                @NotNull CalendarUnit unit) {
        final DateActionType<?> THIS = this;
        String format = param.datePickerType().valueStringFormat(unit);
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
     * @see Zip#of(Object, Object)
     * @see Zip#A
     * @see Zip#B
     */
    @NotNull
    @SuppressWarnings({"MagicConstant", "ConstantConditions"})
    default Flowable<Date> rxe_displayedDate(@NotNull final DateType PARAM) {
        final DateActionType<?> THIS = this;

        return Flowable.fromIterable(PARAM.units())
            .flatMap(a -> THIS.rxe_displayedUnit(PARAM, a).map(b -> Zip.of(a, b)))
            .reduce(Calendar.getInstance(), (a, b) -> {
                a.set(b.A.value(), b.B); return a;
            })
            .map(Calendar::getTime).toFlowable();
    }

    /**
     * Get {@link CalendarUnit}'s {@link String} representation of
     * {@link DateType#date()}.
     * This may be differennt from {@link #displayString(DateType, CalendarUnit)}
     * if the text that appears on screen is formatted different from how we
     * expect it to be.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see DateType#datePickerType()
     * @see DatePickerType#valueStringFormat(CalendarUnit)
     * @see CalendarUnit#value()
     */
    @NotNull
    default String valueString(@NotNull DateType param, @NotNull CalendarUnit unit) {
        Date date = param.date();
        String format = param.datePickerType().valueStringFormat(unit);
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * Get {@link CalendarUnit}'s {@link String} representation of
     * {@link DateType#date()}, as displayed on the screen. Most of the time,
     * this should be the same as {@link #valueString(DateType, CalendarUnit)},
     * but there might be select cases where it is not.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see #valueString(DateType, CalendarUnit)
     */
    @NotNull
    default String displayString(@NotNull DateType param, @NotNull CalendarUnit unit) {
        return valueString(param, unit);
    }

    /**
     * Get {@link String} representation of {@link DateType#date()}.
     * @param PARAM {@link DateType} instance.
     * @return {@link String} value.
     * @see DateType#units()
     * @see #valueString(DateType, CalendarUnit)
     */
    @NotNull
    default String dateString(@NotNull final DateType PARAM) {
        List<String> params = PARAM.units().stream()
            .map(a -> String.format("%s %s", a, valueString(PARAM, a)))
            .collect(Collectors.toList());

        return String.join(" ", params);
    }
}
