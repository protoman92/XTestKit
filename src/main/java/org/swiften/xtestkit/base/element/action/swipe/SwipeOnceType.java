package org.swiften.xtestkit.base.element.action.swipe;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.BaseErrorType;
import org.swiften.xtestkit.base.type.RepeatType;

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
     * @param param {@link SwipeType} instance.
     */
    default void swipeOnce(@NotNull SwipeType param) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Perform a swipe action.
     * @param PARAM {@link SwipeType} instance.
     * @return {@link Flowable} instance.
     * @see #swipeOnce(SwipeType)
     */
    @NotNull
    default Flowable<Boolean> rxa_swipeOnce(@NotNull final SwipeType PARAM) {
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
     * @see #rxa_swipeOnce(SwipeType)
     */
    @NotNull
    default <P extends RepeatType & SwipeType> Flowable<Boolean> rxa_swipe(@NotNull final P PARAM) {
        final SwipeOnceType THIS = this;
        final int TIMES = PARAM.times();
        final long DELAY = PARAM.delay();
        final TimeUnit UNIT = PARAM.timeUnit();

        return Flowable
            .range(0, TIMES)
            .concatMap(a -> THIS.rxa_swipeOnce(PARAM).delay(DELAY, UNIT));
    }
}
