package org.swiften.xtestkit.mobile.element.action.keyboard;

/**
 * Created by haipham on 5/15/17.
 */

import io.appium.java_client.MobileDriver;
import org.swiften.xtestkit.base.element.input.KeyboardActionType;
import org.swiften.xtestkit.base.type.DriverProviderType;

/**
 * This interface provides methods to handle keyboard actions in mobile
 * apps.
 * @param <D> Generics parameter that extends {@link MobileDriver}.
 */
public interface MobileKeyboardActionType<D extends MobileDriver> extends
    KeyboardActionType<D>,
    DriverProviderType<D>
{
    /**
     * Override this method to provide default implementation.
     * @see KeyboardActionType#hideKeyboard()
     */
    @Override
    default void hideKeyboard() {
        driver().hideKeyboard();
    }
}
