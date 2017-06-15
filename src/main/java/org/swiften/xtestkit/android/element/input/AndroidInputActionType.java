package org.swiften.xtestkit.android.element.input;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.input.InputActionType;
import org.swiften.xtestkit.base.type.DriverProviderType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides methods to handle input actions for
 * {@link Platform#ANDROID}.
 */
public interface AndroidInputActionType extends
    AndroidKeyboardCodeType,
    InputActionType<AndroidDriver<AndroidElement>>,
    DriverProviderType<AndroidDriver<AndroidElement>>
{
    /**
     * Override this method to provide default implementation.
     * @see InputActionType#toggleNextInput(WebElement)
     * @see AndroidDriver#pressKeyCode(int)
     */
    @Override
    default void toggleNextInput(@NotNull WebElement element) {
        driver().pressKeyCode(KEYCODE_ENTER);
    }

    /**
     * Override this method to provide default implementation.
     * @see InputActionType#endInput(WebElement)
     * @see AndroidDriver#pressKeyCode(int)
     */
    @Override
    default void endInput(@NotNull WebElement element) {
        driver().pressKeyCode(KEYCODE_ENTER);
    }
}
