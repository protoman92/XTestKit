package org.swiften.xtestkit.base.model;

/**
 * Created by haipham on 5/13/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.xpath.XPath;

/**
 * This interface provides methods to help interact with input-related
 * {@link org.openqa.selenium.WebElement}.
 */
public interface InputType {
    /**
     * Get the {@link XPath} instance for the input view
     * {@link org.openqa.selenium.WebElement} that corresponds to
     * {@link PlatformType}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link XPath} value.
     */
    @NotNull XPath inputViewXP(@NotNull InputHelperType helper);
}
