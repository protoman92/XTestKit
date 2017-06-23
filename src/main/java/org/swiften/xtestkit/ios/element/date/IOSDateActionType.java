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
import org.swiften.xtestkit.base.element.date.DateProviderType;
import org.swiften.xtestkit.base.element.input.InputActionType;
import org.swiften.javautilities.localizer.LocalizerProviderType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by haipham on 22/5/17.
 */
public interface IOSDateActionType extends
    DateActionType<IOSDriver<IOSElement>>,
    InputActionType<IOSDriver<IOSElement>>,
    LocalizerProviderType
{
    /**
     * Override this method to provide default implementation.
     * We do nothing here because the date picker is displayed by default.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateActionType#rxa_openPicker(DateProviderType, CalendarUnit)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_openPicker(@NotNull DateProviderType param,
                                             @NotNull CalendarUnit unit) {
        return Flowable.just(true);
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link DateProviderType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateActionType#rxa_select(DateProviderType, CalendarUnit)
     * @see BooleanUtil#toTrue(Object)
     * @see #valueString(DateProviderType, CalendarUnit)
     * @see #rxa_type(WebElement, String...)
     * @see #rxe_pickerView(DateProviderType, CalendarUnit)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_select(@NotNull DateProviderType param,
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
     * For example, if {@link DateProviderType#datePickerType()} is
     * {@link IOSDatePickerType#PICKER_WHEEL_MMMd_h_mm_a}, the month-day {@link String} will
     * be "Today" if the {@link Date} is the current day.
     * @param param {@link DateProviderType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     * @see DateActionType#valueString(DateProviderType, CalendarUnit)
     * @see CalendarUnit#DAY
     * @see CalendarUnit#isDay()
     * @see CalendarUnit#isMonth()
     * @see CalendarUnit#value()
     * @see DateProviderType#date()
     * @see DateProviderType#datePickerType()
     * @see DateUtil#sameAs(Date, Date, int)
     * @see LocalizerType#localize(String)
     * @see IOSDatePickerType#PICKER_WHEEL_MMMd_h_mm_a
     * @see #localizer()
     */
    @NotNull
    @Override
    default String displayString(@NotNull DateProviderType param, @NotNull CalendarUnit unit) {
        DatePickerType picker = param.datePickerType();
        DatePickerType target = IOSDatePickerType.PICKER_WHEEL_MMMd_h_mm_a;

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
