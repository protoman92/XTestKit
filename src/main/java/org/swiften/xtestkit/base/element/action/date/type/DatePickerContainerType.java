package org.swiften.xtestkit.base.element.action.date.type;

/**
 * Created by haipham on 22/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.element.locator.general.xpath.type.NewXPathBuilderType;
import org.swiften.xtestkit.base.type.BaseErrorType;
import org.swiften.xtestkit.mobile.android.element.action.date.type.AndroidDatePickerContainerType;

/**
 * This interface provides date picker view properties for
 * {@link org.swiften.xtestkit.base.Engine} subclasses.
 */
@FunctionalInterface
public interface DatePickerContainerType {
    /**
     * This interface provides methods to query date picker subviews.
     */
    interface DatePickerType extends BaseErrorType, NewXPathBuilderType {
        /**
         * Get the {@link String} format for a particular {@link CalendarUnit}.
         * @param element {@link CalendarUnit} instance.
         * @return {@link String} value.
         */
        @NotNull
        String stringFormat(@NotNull CalendarUnit element);

        /**
         * Get the display view {@link XPath} that corresponds to
         * {@link CalendarUnit}.
         * @param element {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         */
        @NotNull
        XPath displayViewXPath(@NotNull CalendarUnit element);

        /**
         * Get the picker view {@link XPath} that corresponds to
         * {@link CalendarUnit}.
         * @param element {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         */
        @NotNull
        XPath pickerViewXPath(@NotNull CalendarUnit element);

        /**
         * Get the list view's item {@link XPath} that corresponds to a
         * {@link CalendarUnit}.
         * @param element {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         */
        @NotNull
        XPath listViewItemXPath(@NotNull CalendarUnit element);
    }

    /**
     * Get the associated {@link DatePickerType} instance.
     * @return {@link DatePickerType} instance.
     */
    @NotNull
    DatePickerType datePickerType();
}
