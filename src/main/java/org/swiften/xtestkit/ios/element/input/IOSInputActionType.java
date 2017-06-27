package org.swiften.xtestkit.ios.element.input;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.util.LogUtil;
import org.swiften.xtestkit.base.element.input.InputActionType;

/**
 * Created by haipham on 27/6/17.
 */

/**
 * This interface provides custom input action for
 * {@link org.swiften.xtestkit.mobile.Platform#IOS}.
 */
public interface IOSInputActionType extends InputActionType<IOSDriver<IOSElement>> {
    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     * @param text A varargs of {@link String} values.
     * @see InputActionType#sendValue(WebElement, String)
     */
    @Override
    default void sendValue(@NotNull WebElement element, @NotNull String text) {
        LogUtil.printft("Sending '%s' to %s", text, element);

        if (element instanceof IOSElement) {
            ((IOSElement) element).setValue(text);
        } else {
            InputActionType.super.sendValue(element, text);
        }
    }
}
