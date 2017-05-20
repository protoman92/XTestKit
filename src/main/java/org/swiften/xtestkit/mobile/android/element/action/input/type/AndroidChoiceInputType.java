package org.swiften.xtestkit.mobile.android.element.action.input.type;

/**
 * Created by haipham on 18/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.input.type.ChoiceInputType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;

/**
 * This interface provides methods to work with
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}'s choice-based input
 * {@link org.openqa.selenium.WebElement}.
 */
public interface AndroidChoiceInputType extends AndroidInputType, ChoiceInputType {
    /**
     * Get the scroll view picker {@link XPath} for
     * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
     * @return {@link XPath} value.
     */
    @NotNull
    XPath androidScrollViewPickerXPath();

    /**
     * Get the item {@link XPath} for each item found in the
     * {@link org.openqa.selenium.WebElement} whose {@link XPath} is the
     * {@link #androidScrollViewPickerXPath()}.
     * @return {@link XPath} value.
     * @see #androidScrollViewPickerXPath()
     */
    @NotNull
    XPath androidScrollViewItemXPath();
}
