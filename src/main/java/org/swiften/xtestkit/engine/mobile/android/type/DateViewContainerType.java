package org.swiften.xtestkit.engine.mobile.android.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides date view properties for
 * {@link org.swiften.xtestkit.engine.mobile.android.AndroidEngine}. This
 * helps us hide the implementation for
 * {@link org.swiften.xtestkit.engine.mobile.android.action.date.type.AndroidDateActionType}.
 */
public interface DateViewContainerType {
    /**
     * Represents the available types of calendar views.
     */
    enum DateViewType {
        CALENDAR,
        SPINNER;

        /**
         * Get the format the day is formatted in.
         * @return A {@link String} value.
         */
        @NotNull
        public String dayFormat() {
            return "d";
        }

        /**
         * Get the format the month is formatted in.
         * @return A {@link String} value.
         */
        @NotNull
        public String monthFormat() {
            return "MMM";
        }

        /**
         * Get the format the year is formatted in.
         * @return A {@link String} value.
         */
        @NotNull
        public String yearFormat() {
            return "YYYY";
        }
    }

    /**
     * Get the associated {@link DateViewType} instance.
     * @return A {@link DateViewType} instance.
     */
    @NotNull
    default DateViewType dateViewType() {
        return DateViewType.CALENDAR;
    }
}
