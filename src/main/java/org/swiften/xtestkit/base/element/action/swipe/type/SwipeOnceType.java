package org.swiften.xtestkit.base.element.action.swipe.type;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
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
     * @param param A {@link SwipeType} instance.
     */
    default void swipeOnce(@NotNull SwipeType param) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Perform a swipe action.
     * @param PARAM A {@link SwipeType} instance.
     * @return A {@link Flowable} instance.
     * @see #swipeOnce(SwipeType)
     */
    @NotNull
    default Flowable<Boolean> rx_swipeOnce(@NotNull final SwipeType PARAM) {
        return Completable
            .fromAction(() -> this.swipeOnce(PARAM))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Perform a repeated swipe action.
     * @param PARAM A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rx_swipeOnce(SwipeType)
     */
    @NotNull
    default <P extends RepeatType & SwipeType>
    Flowable<Boolean> rx_swipe(@NotNull final P PARAM) {
        final int TIMES = PARAM.times();
        final long DELAY = PARAM.delay();
        final TimeUnit UNIT = PARAM.timeUnit();

        class Swipe {
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Flowable<Boolean> swipe(final int ITERATION) {
                if (ITERATION < TIMES) {
                    return rx_swipeOnce(PARAM)
                        .delay(DELAY, UNIT, Schedulers.trampoline())
                        .flatMap(a -> new Swipe().swipe(ITERATION + 1));
                } else {
                    return Flowable.just(true);
                }
            }
        }

        return new Swipe().swipe(0);
    }
}
