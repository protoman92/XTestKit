package org.swiften.xtestkit.base.element.action.date;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.property.type.base.AttributeType;

import java.util.Calendar;

/**
 * Use this enum with
 * {@link org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType}
 * for better code reuse.
 */
public enum CalendarUnit implements AttributeType<Integer> {
    DAY,
    MONTH,
    YEAR;

    /**
     * Get the {@link Calendar} constant that corresponds to this
     * {@link CalendarUnit}.
     * @return {@link Integer} instance.
     */
    @NotNull
    public Integer value() {
        switch (this) {
            case DAY:
                return Calendar.DAY_OF_MONTH;

            case MONTH:
                return Calendar.MONTH;

            case YEAR:
                return Calendar.YEAR;

            default:
                return 0;
        }
    }
}
