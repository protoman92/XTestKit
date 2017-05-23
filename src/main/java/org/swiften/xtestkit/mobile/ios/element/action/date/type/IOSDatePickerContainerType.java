package org.swiften.xtestkit.mobile.ios.element.action.date.type;

/**
 * Created by haipham on 22/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.type.DatePickerContainerType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.Platform;

/**
 * This interface provides date picker properties for
 * {@link org.swiften.xtestkit.mobile.ios.IOSEngine}
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
