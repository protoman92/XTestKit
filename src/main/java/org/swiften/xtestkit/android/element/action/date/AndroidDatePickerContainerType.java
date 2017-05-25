package org.swiften.xtestkit.android.element.action.date;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.DatePickerContainerType;

/**
 * This interface provides date picker view properties for
 * {@link org.swiften.xtestkit.android.AndroidEngine}.
 */
public interface AndroidDatePickerContainerType extends DatePickerContainerType {
    /**
     * Get the associated {@link AndroidDatePickerType} instance.
     * @return {@link AndroidDatePickerType} instance.
     */
    @NotNull
    @Override
    default AndroidDatePickerType datePickerType() {
        return AndroidDatePickerType.CALENDAR;
    }
}
