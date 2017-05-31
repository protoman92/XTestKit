package org.swiften.xtestkit.base.element.action.date;

/**
 * Created by haipham on 5/30/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;

/**
 * This interface provides methods to query date picker subviews.
 */
public interface DatePickerType {
    /**
     * Get the {@link String} format for a particular {@link CalendarUnit}.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link String} value.
     */
    @NotNull
    String valueStringFormat(@NotNull CalendarUnit unit);

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