package org.swiften.xtestkit.mobile.android.element.action.input.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.input.type.BaseInputActionType;
import org.swiften.xtestkit.base.type.DriverContainerType;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides methods to handle input actions for
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
 */
public interface AndroidInputActionType extends
    AndroidKeyboardCodeType,
    BaseInputActionType<AndroidDriver<AndroidElement>>,
    DriverContainerType<AndroidDriver<AndroidElement>>
{
    /**
     * Override this method to provide default implementation.
     * @see BaseInputActionType#toggleNextInput()
     * @see AndroidDriver#pressKeyCode(int)
     */
    @Override
    default void toggleNextInput() {
        driver().pressKeyCode(KEYCODE_TAB);
    }

    /**
     * Override this method to provide default implementation.
     * @see BaseInputActionType#toggleDoneInput()
     * @see AndroidDriver#pressKeyCode(int)
     */
    @Override
    default void toggleDoneInput() {
        driver().pressKeyCode(KEYCODE_TAB);
    }
}