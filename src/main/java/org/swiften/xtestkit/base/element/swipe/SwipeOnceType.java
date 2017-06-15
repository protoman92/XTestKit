package org.swiften.xtestkit.base.element.swipe;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
import org.swiften.xtestkitcomponents.common.RepeatType;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 5/11/17.
 */

/**
 * This interface provides methods to perform a single swipe action.
 */
public interface SwipeOnceType extends BaseErrorType {
    /**
     * Perform a swipe action.
     * @param param {@link SwipeParamType} instance.
     */
    default void swipeOnce(@NotNull SwipeParamType param) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Perform a swipe action.
     * @param PARAM {@link SwipeParamType} instance.
     * @return {@link Flowable} instance.
     * @see #swipeOnce(SwipeParamType)
     */
    @NotNull
    default Flowable<Boolean> rxa_swipeOnce(@NotNull final SwipeParamType PARAM) {
        return Completable
            .fromAction(() -> this.swipeOnce(PARAM))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Perform a repeated swipe action.
     * @param PARAM {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#isTrue(boolean)
     * @see P#delay()
     * @see P#times()
     * @see P#timeUnit()
     * @see #rxa_swipeOnce(SwipeParamType)
     */
    @NotNull
    default <P extends RepeatType & SwipeParamType> Flowable<Boolean> rxa_swipe(@NotNull final P PARAM) {
        final SwipeOnceType THIS = this;
        final int TIMES = PARAM.times();
        final long DELAY = PARAM.delay();
        final TimeUnit UNIT = PARAM.timeUnit();

        return Flowable.range(0, TIMES)
            .concatMap(a -> THIS.rxa_swipeOnce(PARAM).delay(DELAY, UNIT))
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }
}
