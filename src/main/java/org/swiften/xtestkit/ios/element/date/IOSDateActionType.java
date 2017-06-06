package org.swiften.xtestkit.ios.element.date;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.date.DateUtil;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.xtestkit.base.element.date.DateActionType;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DatePickerType;
import org.swiften.xtestkit.base.element.date.DateType;
import org.swiften.xtestkit.base.element.input.BaseInputActionType;
import org.swiften.javautilities.localizer.LocalizerContainerType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by haipham on 22/5/17.
 */
public interface IOSDateActionType extends
    DateActionType<IOSDriver<IOSElement>>,
    BaseInputActionType<IOSDriver<IOSElement>>,
    LocalizerContainerType
{
    /**
     * Override this method to provide default implementation.
     * We do nothing here because the date picker is displayed by default.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateActionType#rxa_openPicker(DateType, CalendarUnit)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_openPicker(@NotNull DateType param,
                                             @NotNull CalendarUnit unit) {
        return Flowable.just(true);
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateActionType#rxa_select(DateType, CalendarUnit)
     * @see BooleanUtil#toTrue(Object)
     * @see #valueString(DateType, CalendarUnit)
     * @see #rxa_type(WebElement, String...)
     * @see #rxe_pickerView(DateType, CalendarUnit)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_select(@NotNull DateType param,
                                         @NotNull CalendarUnit unit) {
        final IOSDateActionType THIS = this;
        String value = valueString(param, unit);

        return rxe_pickerView(param, unit)
            .flatMap(a -> THIS.rxa_type(a, value))
            .map(BooleanUtil::toTrue);
    }

    /**
     * Override this method to take care of a few special cases whereby
     * {@link String} format with {@link SimpleDateFormat} is not enough.
     * For example, if {@link DateType#datePickerType()} is
     * {@link IOSDatePickerType#MMMd_h_mm_a}, the month-day {@link String} will
     * be "Today" if the {@link Date} is the current day.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see DateActionType#valueString(DateType, CalendarUnit)
     * @see CalendarUnit#DAY
     * @see CalendarUnit#isDay()
     * @see CalendarUnit#isMonth()
     * @see CalendarUnit#value()
     * @see DateType#date()
     * @see DateType#datePickerType()
     * @see DateUtil#sameAs(Date, Date, int)
     * @see LocalizerType#localize(String)
     * @see IOSDatePickerType#MMMd_h_mm_a
     * @see #localizer()
     */
    @NotNull
    @Override
    default String displayString(@NotNull DateType param, @NotNull CalendarUnit unit) {
        DatePickerType picker = param.datePickerType();
        DatePickerType target = IOSDatePickerType.MMMd_h_mm_a;

        if (picker.equals(target) && (unit.isMonth() || unit.isDay())) {
            LocalizerType localizer = localizer();
            Date now = Calendar.getInstance().getTime();
            Date date = param.date();

            if (DateUtil.sameAs(now, date, CalendarUnit.DAY.value())) {
                return localizer.localize("date_title_today");
            }
        }

        return DateActionType.super.displayString(param, unit);
    }
}
