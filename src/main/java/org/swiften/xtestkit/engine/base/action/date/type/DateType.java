package org.swiften.xtestkit.engine.base.action.date.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.locator.element.type.base.AttributeType;
import org.swiften.xtestkit.engine.base.type.RetryType;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides parameter properties for
 * {@link BaseDateActionType#rxSelectDate(DateType)}
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
}
