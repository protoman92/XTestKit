package org.swiften.xtestkit.mobile.element.action.swipe;

import io.appium.java_client.MobileDriver;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.swiften.xtestkit.base.element.swipe.SwipeParamType;
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
    SwipeType<D>, MobileActionType<D>
{
    /**
     * @param PARAM {@link SwipeParamType} instance.
     * @see #driver()
     * @see #touchAction()
     * @see SwipeType#swipeOnce(SwipeParamType)
     * @see MobileTouchActionType#swipe(MobileDriver, SwipeParamType)
     */
    @Override
    default void swipeOnce(@NotNull final SwipeParamType PARAM) {
        LogUtil.printft("Swiping with %s", PARAM);
        final MobileDriver<?> DRIVER = driver();
        final MobileTouchActionType action = touchAction();
        action.swipe(DRIVER, PARAM);
    }
}
