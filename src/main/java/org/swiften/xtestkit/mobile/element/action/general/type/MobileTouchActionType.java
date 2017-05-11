package org.swiften.xtestkit.mobile.element.action.general.type;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import org.jetbrains.annotations.NotNull;
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
     */
    default void swipe(@NotNull MobileDriver<?> driver,
                       @NotNull SwipeType param) {
        new TouchAction(driver)
            .press(param.startX(), param.startY())
            .waitAction(Duration.ofMillis(param.duration()))
            .moveTo(param.endX(), param.endY())
            .release()
            .perform();
    }
}
