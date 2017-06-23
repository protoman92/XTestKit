package org.swiften.xtestkit.android.element.input;

/**
 * Created by haipham on 5/15/17.
 */

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Keys;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.base.element.general.ActionType;
import org.swiften.javautilities.protocol.RepeatProviderType;
import org.swiften.xtestkit.android.AndroidInstance;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.type.ADBHandlerProviderType;
import org.swiften.xtestkit.android.type.AndroidInstanceProviderType;
import org.swiften.xtestkit.android.type.DeviceUIDProviderType;
import org.swiften.xtestkit.mobile.element.action.keyboard.MobileKeyboardActionType;

/**
 * This interface provides methods to work with Android keyboards.
 */
public interface AndroidKeyboardActionType extends
    ADBHandlerProviderType,
    AndroidInstanceProviderType,
    ActionType<AndroidDriver<AndroidElement>>,
    MobileKeyboardActionType<AndroidDriver<AndroidElement>>
{
    @Override
    default void hideKeyboard() {
        driver().getKeyboard().pressKey(Keys.RETURN);
    }

    /**
     * Dismiss the keyboard if it is open. We first need to check whether the
     * keyboard is present with
     * {@link ADBHandler#rxe_keyboardOpen(DeviceUIDProviderType)},
     * and then call {@link #rxa_navigateBack(RepeatProviderType)}.
     * @return {@link Flowable} instance.
     * @see #rxa_navigateBack(RepeatProviderType)
     * @see ADBHandler#rxe_keyboardOpen(DeviceUIDProviderType)
     */
    @NotNull
    default Flowable<Boolean> rxa_hideKeyboard() {
        AndroidInstance instance = androidInstance();

        return adbHandler().rxe_keyboardOpen(instance)
            .filter(BooleanUtil::isTrue)
            .flatMap(a -> rxa_navigateBackOnce())
            .defaultIfEmpty(true);
    }
}
