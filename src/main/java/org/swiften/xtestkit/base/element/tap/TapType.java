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
import org.swiften.xtestkit.base.element.property.ElementPropertyType;
import org.swiften.xtestkitcomponents.common.RetryType;

/**
 * This interface provides methods to handle taps.
 */
public interface TapType<D extends WebDriver> extends ElementPropertyType {
    /**
     * Perform a tap action.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     */
    <P extends TapParamType & RetryType> void tap(@NotNull P param);

    /**
     * Same as above, but uses a default {@link TapParam}.
     * @param x The tap's x coordinate, {@link Integer} value.
     * @param y The tap's y coordinate, {@link Integer} value.
     * @see TapParam.Builder#withX(int)
     * @see TapParam.Builder#withY(int)
     * @see #tap(TapParamType)
     */
    default void tap(int x, int y) {
        LogUtil.printft("Tapping at x: %d, y: %d", x, y);
        tap(TapParam.builder().withX(x).withY(y).build());
    }

    /**
     * Same as above, but uses {@link Point}.
     * @param point {@link Point} instance.
     * @see Point#getX()
     * @see Point#getY()
     * @see #tap(int, int)
     */
    default void tap(@NotNull Point point) {
        tap(point.getX(), point.getY());
    }

    /**
     * Tap the middle {@link Point} of {@link WebElement}.
     * @param element {@link WebElement} instance.
     * @see #middleCoordinate(WebElement)
     * @see #tap(Point)
     */
    default void tapMiddle(@NotNull WebElement element) {
        tap(middleCoordinate(element));
    }

    /**
     * Perform a tap action.
     * @param PARAM {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #tap(TapParamType)
     */
    @NotNull
    default <P extends TapParamType & RetryType> Flowable<Boolean>
    rxa_tap(@NotNull final P PARAM) {
        final TapType<?> THIS = this;

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
        final TapType<?> THIS = this;

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
     * Tap the middle {@link Point} of {@link WebElement}.
     * @param ELEMENT {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see #tapMiddle(WebElement)
     */
    @NotNull
    default Flowable<Boolean> rxa_tapMiddle(@NotNull final WebElement ELEMENT) {
        final TapType<?> THIS = this;

        return Completable
            .fromAction(() -> THIS.tapMiddle(ELEMENT))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
}
