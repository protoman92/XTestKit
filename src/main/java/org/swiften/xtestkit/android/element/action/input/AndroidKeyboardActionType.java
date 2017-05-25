package org.swiften.xtestkit.android.element.action.input;

/**
 * Created by haipham on 5/15/17.
 */

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Keys;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.base.element.action.general.BaseActionType;
import org.swiften.xtestkit.base.type.RepeatType;
import org.swiften.xtestkit.android.AndroidInstance;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.type.ADBHandlerContainerType;
import org.swiften.xtestkit.android.type.AndroidInstanceContainerType;
import org.swiften.xtestkit.android.type.DeviceUIDType;
import org.swiften.xtestkit.mobile.element.action.keyboard.MobileKeyboardActionType;

/**
 * This interface provides methods to work with Android keyboards.
 */
public interface AndroidKeyboardActionType extends
    ADBHandlerContainerType,
    AndroidInstanceContainerType,
    BaseActionType<AndroidDriver<AndroidElement>>,
    MobileKeyboardActionType<AndroidDriver<AndroidElement>>
{
    @Override
    default void hideKeyboard() {
        driver().getKeyboard().pressKey(Keys.RETURN);
    }

    /**
     * Dismiss the keyboard if it is open. We first need to check whether the
     * keyboard is present with
     * {@link ADBHandler#rx_checkKeyboardOpen(DeviceUIDType)},
     * and then call {@link #rx_navigateBack(RepeatType)}.
     * @return {@link Flowable} instance.
     * @see #rx_navigateBack(RepeatType)
     * @see ADBHandler#rx_checkKeyboardOpen(DeviceUIDType)
     */
    @NotNull
    default Flowable<Boolean> rx_hideKeyboard() {
        AndroidInstance instance = androidInstance();

        return adbHandler().rx_checkKeyboardOpen(instance)
            .filter(BooleanUtil::isTrue)
            .flatMap(a -> rx_navigateBackOnce())
            .defaultIfEmpty(true);
    }
}
