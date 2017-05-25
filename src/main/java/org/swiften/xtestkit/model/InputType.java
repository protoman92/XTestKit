package org.swiften.xtestkit.model;

/**
 * Created by haipham on 5/13/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.PlatformType;

/**
 * This interface provides methods to help interact with input-related
 * {@link org.openqa.selenium.WebElement}.
 */
public interface InputType {
    /**
     * Get the {@link XPath} instance for the input view
     * {@link org.openqa.selenium.WebElement} that corresponds to
     * {@link PlatformType}.
     * @param platform {@link PlatformType} instance.
     * @return {@link XPath} value.
     */
    @NotNull
    XPath inputViewXPath(@NotNull PlatformType platform);
}
