package org.swiften.xtestkit.mobile.element.action.swipe.type;

import io.appium.java_client.MobileDriver;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.swipe.type.BaseSwipeType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileTouchActionType;

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
     * @param PARAM A {@link SwipeType} instance.
     * @return A {@link Flowable} instance.
     * @see #driver()
     * @see #touchAction()
     * @see BaseSwipeType#rxSwipeOnce(SwipeType)
     * @see MobileTouchActionType#swipe(MobileDriver, SwipeType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxSwipeOnce(@NotNull final SwipeType PARAM) {
        final MobileDriver<?> DRIVER = driver();
        final MobileTouchActionType TOUCH_ACTION = touchAction();

        return Completable
            .fromAction(() -> TOUCH_ACTION.swipe(DRIVER, PARAM))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
}