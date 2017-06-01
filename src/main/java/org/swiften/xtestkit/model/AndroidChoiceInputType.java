package org.swiften.xtestkit.model;

/**
 * Created by haipham on 25/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.param.ByXPath;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.mobile.Platform;

/**
 * This interface provides choice-related methods for
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}. It improves upon
 * {@link ChoiceInputType} by providing default implementations that can be
 * used in a variety of situations.
 */
public interface AndroidChoiceInputType extends ChoiceInputType {
    /**
     * Get the index associated with the {@link org.openqa.selenium.WebElement}
     * with which we are selecting a value for the current input.
     * @return {@link Integer} value.
     */
    default int androidScrollablePickerIndex() {
        return 0;
    };

    /**
     * Get a {@link ByXPath} instance that will be used to query for the
     * choice picker item.
     * @param selected The selected {@link String} choice.
     */
    @NotNull
    XPath androidTargetChoiceItemXPath(@NotNull String selected);

    /**
     * Get the scroll view picker {@link XPath} for {@link Platform#ANDROID}.
     * @return {@link XPath} instance.
     */
    @NotNull XPath androidChoicePickerXPath();

    /**
     * Get the scroll view picker item {@link XPath} for {@link Platform#ANDROID}.
     * @return {@link XPath} instance.
     */
    @NotNull XPath androidChoicePickerItemXPath();
}
