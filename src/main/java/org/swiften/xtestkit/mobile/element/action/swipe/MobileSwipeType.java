package org.swiften.xtestkit.mobile.element.action.swipe;

import io.appium.java_client.MobileDriver;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.swipe.BaseSwipeType;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.general.MobileTouchActionType;

/**
 * Created by haipham on 5/11/17.
 */

/**
 * This interface provides methods to handle mobile swipe actions.
 * @param <D> Generics parameter that extends {@link MobileDriver}.
 */
public interface MobileSwipeType<D extends MobileDriver> extends
    BaseSwipeType<D>, MobileActionType<D>
{
    /**
     * @param PARAM {@link SwipeType} instance.
     * @see #driver()
     * @see #touchAction()
     * @see BaseSwipeType#swipeOnce(SwipeType)
     * @see MobileTouchActionType#swipe(MobileDriver, SwipeType)
     */
    @Override
    default void swipeOnce(@NotNull final SwipeType PARAM) {
        LogUtil.printft("Swiping with %s", PARAM);
        final MobileDriver<?> DRIVER = driver();
        final MobileTouchActionType action = touchAction();
        action.swipe(DRIVER, PARAM);
    }
}
