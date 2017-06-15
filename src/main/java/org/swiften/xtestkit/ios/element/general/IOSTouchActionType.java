package org.swiften.xtestkit.ios.element.general;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.swipe.SwipeParamType;
import org.swiften.xtestkit.mobile.element.action.general.MobileTouchActionType;
import org.swiften.xtestkit.mobile.Platform;

import java.time.Duration;

/**
 * Created by haipham on 24/5/17.
 */

/**
 * This interface provides touch actions for
 * {@link Platform#IOS}.
 */
public interface IOSTouchActionType extends MobileTouchActionType {
    /**
     * Override this method to implement swiping with relative positions.
     * @param driver {@link MobileDriver} instance.
     * @param param {@link SwipeParamType} instance.
     * @see SwipeParamType#startX()
     * @see SwipeParamType#startY()
     * @see SwipeParamType#endX()
     * @see SwipeParamType#endY()
     * @see SwipeParamType#duration()
     * @see TouchAction#press(int, int)
     * @see TouchAction#waitAction(Duration)
     * @see TouchAction#moveTo(int, int)
     * @see TouchAction#release()
     * @see TouchAction#perform()
     */
    default void swipe(@NotNull MobileDriver<?> driver, @NotNull SwipeParamType param) {
        int startX = param.startX(), startY = param.startY();
        int endX = param.endX() - startX, endY = param.endY() - startY;

        new TouchAction(driver)
            .press(startX, startY)
            .waitAction(Duration.ofMillis(param.duration()))
            .moveTo(endX, endY)
            .release()
            .perform();
    }
}
