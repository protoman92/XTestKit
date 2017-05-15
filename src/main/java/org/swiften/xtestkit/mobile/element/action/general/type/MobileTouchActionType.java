package org.swiften.xtestkit.mobile.element.action.general.type;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.tap.type.TapType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;

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
     * @param driver A {@link MobileDriver} instance.
     * @param param A {@link SwipeType} instance.
     * @see TouchAction#press(int, int)
     * @see TouchAction#waitAction(Duration)
     * @see TouchAction#moveTo(int, int)
     * @see TouchAction#release()
     * @see TouchAction#perform()
     */
    default void swipe(@NotNull MobileDriver<?> driver, @NotNull SwipeType param) {
        new TouchAction(driver)
            .press(param.startX(), param.startY())
            .waitAction(Duration.ofMillis(param.duration()))
            .moveTo(param.endX(), param.endY())
            .release()
            .perform();
    }

    /**
     * Perform a tap action.
     * @param driver A {@link MobileDriver} instance.
     * @param param A {@link SwipeType} instance.
     * @see TouchAction#release()
     * @see TouchAction#perform()
     */
    default void tap(@NotNull MobileDriver<?> driver, @NotNull TapType param) {
        new TouchAction(driver).press(param.x(), param.y()).release().perform();
    }
}
