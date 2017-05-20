package org.swiften.xtestkit.base.element.action.tap.type;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.action.tap.param.TapParam;
import org.swiften.xtestkit.base.type.BaseErrorType;
import org.swiften.xtestkit.base.type.RetryType;

/**
 * This interface provides methods to handle taps.
 */
public interface BaseTapType<D extends WebDriver> extends BaseErrorType {
    /**
     * Perform a tap action.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     */
    default <P extends TapType & RetryType> void tap(@NotNull P param) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Same as above, but uses a default {@link TapParam}.
     * @param x The tap's x coordinate, {@link Integer} value.
     * @param y The tap's y coordinate, {@link Integer} value.
     * @see #tap(TapType)
     */
    default void tap(int x, int y) {
        tap(TapParam.builder().withX(x).withY(y).build());
    }

    /**
     * Perform a tap action.
     * @param PARAM {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #tap(TapType)
     */
    @NotNull
    default <P extends TapType & RetryType>
    Flowable<Boolean> rx_tap(@NotNull final P PARAM) {
        final BaseTapType<?> THIS = this;

        return Completable
            .fromAction(() -> THIS.tap(PARAM))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Same as above, but uses a default {@link TapParam}.
     * @param X The tap's x coordinate, {@link Integer} value.
     * @param Y The tap's y coordinate, {@link Integer} value.
     * @return {@link Flowable} instance.
     * @see #tap(int, int)
     */
    @NotNull
    default Flowable<Boolean> rx_tap(final int X, final int Y) {
        LogUtil.printfThread(
            "Tapping at x: %d, y: %d, for %s",
            X, Y, this
        );

        final BaseTapType<?> THIS = this;

        return Completable
            .fromAction(() -> THIS.tap(X, Y))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
}
