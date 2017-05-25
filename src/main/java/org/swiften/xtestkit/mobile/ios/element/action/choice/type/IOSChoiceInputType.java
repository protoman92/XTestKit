package org.swiften.xtestkit.mobile.ios.element.action.choice.type;

/**
 * Created by haipham on 25/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.Platform;

/**
 * This interface provides choice-related methods for
 * {@link org.swiften.xtestkit.mobile.Platform#IOS}.
 */
public interface IOSChoiceInputType {
    /**
     * Get the index associated with the {@link org.openqa.selenium.WebElement}
     * with which we are selecting a value for the current input.
     * @return {@link Integer} value.
     */
    int iOSScrollablePickerIndex();

    /**
     * Get the scroll view picker {@link XPath} for {@link Platform#IOS}.
     * @return {@link XPath} instance.
     */
    @NotNull XPath iOSScrollViewPickerXPath();
}
