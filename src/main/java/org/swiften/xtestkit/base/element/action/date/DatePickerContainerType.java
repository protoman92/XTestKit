package org.swiften.xtestkit.base.element.action.date;

/**
 * Created by haipham on 22/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.BaseErrorType;

/**
 * This interface provides date picker view properties for
 * {@link org.swiften.xtestkit.base.Engine} subclasses.
 */
@FunctionalInterface
public interface DatePickerContainerType {
    /**
     * This interface provides methods to query date picker subviews.
     */
    interface DatePickerType extends BaseErrorType {
        /**
         * Get the {@link String} format for a particular {@link CalendarUnit}.
         * @param unit {@link CalendarUnit} instance.
         * @return {@link String} value.
         */
        @NotNull
        String stringFormat(@NotNull CalendarUnit unit);

        /**
         * Get the picker list view {@link XPath} that corresponds to
         * {@link CalendarUnit}.
         * @param unit {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         */
        @NotNull
        XPath pickerViewXPath(@NotNull CalendarUnit unit);

        /**
         * Get the display view {@link XPath} that corresponds to
         * {@link CalendarUnit}.
         * @param unit {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         */
        @NotNull
        XPath unitLabelViewXPath(@NotNull CalendarUnit unit);

        /**
         * Get the target item view {@link XPath} that corresponds to
         * {@link CalendarUnit}. This view should be the one displaying
         * the component we are interested in.
         * @param unit {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         */
        @NotNull
        XPath targetItemXPath(@NotNull CalendarUnit unit);

        /**
         * Get the list view's item {@link XPath} that corresponds to a
         * {@link CalendarUnit}.
         * @param unit {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         */
        @NotNull
        XPath pickerItemXPath(@NotNull CalendarUnit unit);
    }

    /**
     * Get the associated {@link DatePickerType} instance.
     * @return {@link DatePickerType} instance.
     */
    @NotNull
    DatePickerType datePickerType();
}