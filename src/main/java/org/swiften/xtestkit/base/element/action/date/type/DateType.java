package org.swiften.xtestkit.base.element.action.date.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.CalendarElement;
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
     * Get a {@link Calendar} instance.
     * @return A {@link Calendar} instance.
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
     * @return An {@link Integer} value.
     */
    default int dayOfMonth() {
        return calendar().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get {@link Calendar#DAY_OF_WEEK}.
     * @return An {@link Integer} value.
     */
    default int dayOfWeek() {
        return calendar().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Get {@link Calendar#MONTH}.
     * @return An {@link Integer} value.
     */
    default int month() {
        return calendar().get(Calendar.MONTH);
    }

    /**
     * Get {@link Calendar#YEAR}.
     * @return An {@link Integer} value.
     */
    default int year() {
        return calendar().get(Calendar.YEAR);
    }

    /**
     * Get the {@link Calendar} component that corresponds to a
     * {@link CalendarElement}.
     * @param element A {@link CalendarElement} instance.
     * @return An {@link Integer} value.
     */
    @SuppressWarnings("MagicConstant")
    default int component(@NotNull CalendarElement element) {
        return calendar().get(element.value());
    }

    /**
     * Get a {@link String} representation of {@link #value()}.
     * @param format A {@link String} format.
     * @return A {@link String} value.
     * @see #value()
     */
    @NotNull
    default String dateString(@NotNull String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(value());
    }
}
