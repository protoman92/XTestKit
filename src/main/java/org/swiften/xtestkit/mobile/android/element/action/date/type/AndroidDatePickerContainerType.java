package org.swiften.xtestkit.mobile.android.element.action.date.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.type.DatePickerContainerType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.android.AndroidView;

/**
 * This interface provides date picker view properties for
 * {@link org.swiften.xtestkit.mobile.android.AndroidEngine}.
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
