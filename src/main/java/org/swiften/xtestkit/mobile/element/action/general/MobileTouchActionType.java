package org.swiften.xtestkit.mobile.element.action.general;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.swipe.SwipeParamType;
import org.swiften.xtestkit.base.element.tap.TapParamType;

import java.time.Duration;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * Since convenient touch actions are depeciated for {@link MobileDriver}, we
 * use a custom touch action element to perform common actions.
 */
public interface MobileTouchActionType {
    /**
     * Perform a swipe action.
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
        new TouchAction(driver)
            .press(param.startX(), param.startY())
            .waitAction(Duration.ofMillis(param.duration()))
            .moveTo(param.endX(), param.endY())
            .release()
            .perform();
    }

    /**
     * Perform a tap action.
     * @param driver {@link MobileDriver} instance.
     * @param param {@link SwipeParamType} instance.
     * @see TouchAction#release()
     * @see TouchAction#perform()
     */
    default void tap(@NotNull MobileDriver<?> driver, @NotNull TapParamType param) {
        new TouchAction(driver).press(param.x(), param.y()).release().perform();
    }
}
