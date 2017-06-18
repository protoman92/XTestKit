package org.swiften.xtestkit.base.element.popup;

/**
 * Created by haipham on 6/19/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.xpath.XPath;

/**
 * This interface describes different types of popups, and provides ways to
 * locate {@link org.openqa.selenium.WebElement}, e.g. to dismiss the popup.
 */
public interface PopupType {
    /**
     * Get {@link XPath} to check for presence of popup on-screen.
     * @param helper {@link InputHelperType} instance.
     * @return {@link XPath} instance.
     */
    @NotNull XPath presenceXP(@NotNull InputHelperType helper);

    /**
     * Get {@link XPath} to check for {@link org.openqa.selenium.WebElement}
     * that, once clicked, dismisses the current popup.
     * @param helper {@link InputHelperType} instance.
     * @return {@link XPath} instance.
     */
    @NotNull XPath dismissXP(@NotNull InputHelperType helper);

    /**
     * Check if a popup is applicable to {@link PlatformType}.
     * @param platform {@link PlatformType} instance.
     * @return {@link Boolean} value.
     */
    boolean applicableTo(@NotNull PlatformType platform);
}
