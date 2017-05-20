package org.swiften.xtestkit.base.element.action.date.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.property.type.base.AttributeType;
import org.swiften.xtestkit.base.type.RetryType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides parameter properties for
 * {@link BaseDateActionType#rx_selectDate(DateType)}
 */
public interface DateType extends AttributeType<Date>, RetryType {
    /**
     * Get {@link Calendar} instance.
     * @return {@link Calendar} instance.
     * @see AttributeType#value()
     */
    @NotNull
    default Calendar calendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value());
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
     * @param element {@link CalendarUnit} instance.
     * @return {@link Integer} value.
     */
    @SuppressWarnings("MagicConstant")
    default int component(@NotNull CalendarUnit element) {
        return calendar().get(element.value());
    }

    /**
     * Get {@link String} representation of {@link #value()}.
     * @param format {@link String} format.
     * @return {@link String} value.
     * @see #value()
     */
    @NotNull
    default String dateString(@NotNull String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(value());
    }
}
