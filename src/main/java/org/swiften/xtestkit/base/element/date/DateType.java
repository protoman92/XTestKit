package org.swiften.xtestkit.base.element.date;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkitcomponents.property.base.AttributeType;
import org.swiften.xtestkitcomponents.common.RetryType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides parameter properties for
 * {@link DateActionType#rxa_selectDate(DateType)}
 */
public interface DateType extends RetryType {
    /**
     * Get the associated {@link Date} instance to select.
     * @return {@link Date} instance.
     */
    @NotNull Date date();

    /**
     * Get the associated {@link DatePickerType} instance to determine the
     * type of widget the app is using. This allows us to customize our date
     * selection tools to maximize accuracy.
     * @return {@link DatePickerType} instance.
     */
    @NotNull DatePickerType datePickerType();

    /**
     * Get the associated {@link CalendarUnit} for date/time selection.
     * @return {@link List} of {@link CalendarUnit}.
     */
    @NotNull List<CalendarUnit> units();

    /**
     * Get {@link Calendar} instance.
     * @return {@link Calendar} instance.
     * @see AttributeType#value()
     */
    @NotNull
    default Calendar calendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date());
        return calendar;
    }

    /**
     * Get {@link Calendar#DAY_OF_MONTH}.
     * @return {@link Integer} value.
     */
    default int dayOfMonth() {
        return calendar().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get {@link Calendar#DAY_OF_WEEK}.
     * @return {@link Integer} value.
     */
    default int dayOfWeek() {
        return calendar().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Get {@link Calendar#MONTH}.
     * @return {@link Integer} value.
     */
    default int month() {
        return calendar().get(Calendar.MONTH);
    }

    /**
     * Get {@link Calendar#YEAR}.
     * @return {@link Integer} value.
     */
    default int year() {
        return calendar().get(Calendar.YEAR);
    }

    /**
     * Get the {@link Calendar} component that corresponds to a
     * {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Integer} value.
     */
    @SuppressWarnings("MagicConstant")
    default int component(@NotNull CalendarUnit unit) {
        return calendar().get(unit.value());
    }

    /**
     * Get {@link String} representation of {@link #date()}.
     * @param format {@link String} format.
     * @return {@link String} value.
     * @see #date()
     */
    @NotNull
    default String dateString(@NotNull String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date());
    }
}
