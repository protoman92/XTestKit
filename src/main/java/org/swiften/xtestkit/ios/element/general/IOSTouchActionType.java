package org.swiften.xtestkit.ios.element.general;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.swiften.xtestkit.mobile.element.action.general.MobileTouchActionType;

import java.time.Duration;

/**
 * Created by haipham on 24/5/17.
 */

/**
 * This interface provides touch actions for
 * {@link org.swiften.xtestkit.mobile.Platform#IOS}.
 */
public interface IOSTouchActionType extends MobileTouchActionType {
    /**
     * Override this method to implement swiping with relative positions.
     * @param driver {@link MobileDriver} instance.
     * @param param {@link SwipeType} instance.
     * @see SwipeType#startX()
     * @see SwipeType#startY()
     * @see SwipeType#endX()
     * @see SwipeType#endY()
     * @see SwipeType#duration()
     * @see TouchAction#press(int, int)
     * @see TouchAction#waitAction(Duration)
     * @see TouchAction#moveTo(int, int)
     * @see TouchAction#release()
     * @see TouchAction#perform()
     */
    default void swipe(@NotNull MobileDriver<?> driver, @NotNull SwipeType param) {
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
