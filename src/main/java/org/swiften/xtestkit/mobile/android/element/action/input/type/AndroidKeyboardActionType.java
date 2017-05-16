package org.swiften.xtestkit.mobile.android.element.action.input.type;

/**
 * Created by haipham on 5/15/17.
 */

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Keys;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.action.general.type.BaseActionType;
import org.swiften.xtestkit.base.type.RepeatType;
import org.swiften.xtestkit.mobile.android.AndroidInstance;
import org.swiften.xtestkit.mobile.android.adb.ADBHandler;
import org.swiften.xtestkit.mobile.android.type.ADBHandlerContainerType;
import org.swiften.xtestkit.mobile.android.type.AndroidInstanceContainerType;
import org.swiften.xtestkit.mobile.android.type.DeviceUIDType;
import org.swiften.xtestkit.mobile.element.action.keyboard.type.MobileKeyboardActionType;

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
     * {@link ADBHandler#rxCheckKeyboardOpen(DeviceUIDType)},
     * and then call {@link #rxNavigateBack(RepeatType)}.
     * @return A {@link Flowable} instance.
     * @see #rxNavigateBack(RepeatType)
     * @see ADBHandler#rxCheckKeyboardOpen(DeviceUIDType)
     */
    @NotNull
    default Flowable<Boolean> rxHideKeyboard() {
        AndroidInstance instance = androidInstance();

        return adbHandler().rxCheckKeyboardOpen(instance)
            .filter(BooleanUtil::isTrue)
            .flatMap(a -> rxNavigateBackOnce())
            .defaultIfEmpty(true);
    }
}
