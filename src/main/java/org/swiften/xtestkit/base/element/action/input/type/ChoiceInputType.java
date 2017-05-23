package org.swiften.xtestkit.base.element.action.input.type;

/**
 * Created by haipham on 18/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.PlatformType;

/**
 * This interface provides methods to help interact with choice-based input
 * {@link org.openqa.selenium.WebElement}.
 */
public interface ChoiceInputType extends InputType {
    /**
     * Get the scroll view picker {@link XPath} for
     * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
     * @param platform {@link PlatformType} instance.
     * @return {@link XPath} value.
     */
    @NotNull
    XPath choicePickerScrollViewXPath(@NotNull PlatformType platform);

    /**
     * Get the item {@link XPath} for each item found in the
     * {@link org.openqa.selenium.WebElement} whose {@link XPath} is the
     * {@link #choicePickerScrollViewXPath(PlatformType)}.
     * @param platform {@link PlatformType} instance.
     * @return {@link XPath} value.
     * @see #choicePickerScrollViewXPath(PlatformType)
     */
    @NotNull
    XPath choicePickerScrollViewItemXPath(@NotNull PlatformType platform);
}
