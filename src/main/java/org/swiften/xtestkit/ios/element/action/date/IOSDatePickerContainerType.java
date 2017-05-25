package org.swiften.xtestkit.ios.element.action.date;

/**
 * Created by haipham on 22/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.DatePickerContainerType;

/**
 * This interface provides date picker properties for
 * {@link org.swiften.xtestkit.ios.IOSEngine}
 */
public interface IOSDatePickerContainerType extends DatePickerContainerType {
    /**
     * @return {@link DatePickerContainerType.DatePickerType} instance.
     * @see DatePickerContainerType#datePickerType()
     */
    @NotNull
    default IOSDatePickerType datePickerType() {
        return IOSDatePickerType.BASIC;
    }
}
