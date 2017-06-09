package org.swiften.xtestkit.base.element.tap;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.property.BaseElementPropertyType;
import org.swiften.xtestkitcomponents.common.RetryType;

/**
 * This interface provides methods to handle taps.
 */
public interface BaseTapType<D extends WebDriver> extends BaseElementPropertyType {
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
    default <P extends TapType & RetryType> Flowable<Boolean> rxa_tap(@NotNull final P PARAM) {
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
    default Flowable<Boolean> rxa_tap(final int X, final int Y) {
        LogUtil.printft("Tapping at x: %d, y: %d", X, Y);
        final BaseTapType<?> THIS = this;

        return Completable
            .fromAction(() -> THIS.tap(X, Y))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Same as above, but uses {@link Point}.
     * @param point {@link Point} instance.
     * @return {@link Flowable} instance.
     * @see Point#getX()
     * @see Point#getY()
     * @see #rxa_tap(int, int)
     */
    @NotNull
    default Flowable<Boolean> rxa_tap(@NotNull Point point) {
        int x = point.getX(), y = point.getY();
        return rxa_tap(x, y);
    }

    /**
     * Tap the middle of {@link WebElement}.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see #middleCoordinate(WebElement)
     * @see #rxa_tap(Point)
     */
    @NotNull
    default Flowable<Boolean> rxa_tapMiddle(@NotNull WebElement element) {
        Point coordinate = middleCoordinate(element);
        return rxa_tap(coordinate);
    }
}
