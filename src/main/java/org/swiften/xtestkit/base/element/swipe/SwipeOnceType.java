package org.swiften.xtestkit.base.element.swipe;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.javautilities.protocol.RepeatProviderType;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 5/11/17.
 */

/**
 * This interface provides methods to perform a single swipe action.
 */
public interface SwipeOnceType extends ErrorProviderType {
    /**
     * Perform a swipe action.
     * @param param {@link SwipeParamType} instance.
     */
    void swipeOnce(@NotNull SwipeParamType param);

    /**
     * Perform a swipe action.
     * @param PARAM {@link SwipeParamType} instance.
     * @return {@link Flowable} instance.
     * @see #swipeOnce(SwipeParamType)
     */
    @NotNull
    default Flowable<Boolean> rxa_swipeOnce(@NotNull final SwipeParamType PARAM) {
        final SwipeOnceType THIS = this;

        return Completable
            .fromAction(() -> THIS.swipeOnce(PARAM))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Perform a repeated swipe action.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#isTrue(boolean)
     * @see P#delay()
     * @see P#times()
     * @see P#timeUnit()
     * @see #rxa_swipeOnce(SwipeParamType)
     */
    @NotNull
    default <P extends
        RepeatProviderType &
        SwipeParamType> Flowable<Boolean> rxa_swipe(@NotNull P param) {
        int times = param.times();
        long delay = param.delay();
        TimeUnit unit = param.timeUnit();

        return rxa_swipeOnce(param)
            .delay(delay, unit)
            .repeat(times)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }
}
