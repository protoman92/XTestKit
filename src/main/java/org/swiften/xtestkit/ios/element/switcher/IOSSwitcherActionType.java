package org.swiften.xtestkit.ios.element.switcher;

/**
 * Created by haipham on 6/10/17.
 */

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.switcher.SwitcherActionType;

/**
 * This interface provides methods to handle switcher
 * {@link org.openqa.selenium.WebElement} for
 * {@link org.swiften.xtestkit.mobile.Platform#IOS}.
 */
public interface IOSSwitcherActionType extends SwitcherActionType {
    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     * @return {@link String} value.
     * @see SwitcherActionType#switcherValue(WebElement)
     * @see WebElement#getAttribute(String)
     */
    @NotNull
    @Override
    default String switcherValue(@NotNull WebElement element) {
        return element.getAttribute("value");
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link String} value.
     * @see SwitcherActionType#switcherOnValue()
     */
    @NotNull
    @Override
    default String switcherOffValue() {
        return "0";
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link String} value.
     * @see SwitcherActionType#switcherOffValue()
     */
    @NotNull
    @Override
    default String switcherOnValue() {
        return "1";
    }
}
