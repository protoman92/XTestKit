package org.swiften.xtestkit.mobile.element.action.swipe;

import io.appium.java_client.MobileDriver;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.swipe.type.BaseSwipeType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeGestureType;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileTouchActionType;

/**
 * Created by haipham on 5/11/17.
 */
public interface MobileSwipeType<D extends MobileDriver> extends
    BaseSwipeType<D>, MobileActionType<D>
{
    /**
     * @param PARAM A {@link SwipeGestureType} instance.
     * @return A {@link Flowable} instance.
     * @see BaseSwipeType#rxSwipeOnce(SwipeGestureType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxSwipeOnce(@NotNull final SwipeGestureType PARAM) {
        final MobileDriver<?> DRIVER = driver();
        final MobileTouchActionType TOUCH_ACTION = touchAction();

        return Completable
            .fromAction(() -> TOUCH_ACTION.swipe(DRIVER, PARAM))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
}
