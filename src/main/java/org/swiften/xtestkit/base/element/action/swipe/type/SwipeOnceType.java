package org.swiften.xtestkit.base.element.action.swipe.type;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.RepeatableType;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 5/11/17.
 */

/**
 * This interface provides methods to perform a single swipe action.
 */
public interface SwipeOnceType {
    /**
     * Perform a swipe action.
     * @param param A {@link SwipeGestureType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxSwipeOnce(@NotNull SwipeGestureType param);

    /**
     * Perform a repeated swipe action.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default <P extends RepeatableType & SwipeGestureType>
    Flowable<Boolean> rxSwipe(@NotNull P param) {
        final Flowable<Boolean> SWIPE = rxSwipeOnce(param);
        final int TIMES = param.times();
        final long DELAY = param.delay();
        final TimeUnit UNIT = param.timeUnit();

        class PerformSwipe {
            @NotNull
            private Flowable<Boolean> swipe(final int ITERATION) {
                if (ITERATION < TIMES) {
                    return SWIPE
                        .delay(DELAY, UNIT)
                        .flatMap(a -> new PerformSwipe().swipe(ITERATION + 1));
                }

                return Flowable.empty();
            }
        }

        return new PerformSwipe().swipe(0).defaultIfEmpty(true);
    }
}
