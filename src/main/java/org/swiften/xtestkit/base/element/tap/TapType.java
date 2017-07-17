package org.swiften.xtestkit.base.element.tap;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.FlowableTransformer;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.functional.Tuple;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.javautilities.rx.HPReactives;
import org.swiften.javautilities.util.HPLog;
import org.swiften.xtestkit.base.element.property.ElementPropertyType;

/**
 * This interface provides methods to handle taps.
 */
public interface TapType<D extends WebDriver> extends ElementPropertyType {
    /**
     * Perform a tap action.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     */
    <P extends TapParamType & RetryProviderType> void tap(@NotNull P param);

    /**
     * Same as above, but uses a default {@link TapParam}.
     * @param x The tap's x coordinate, {@link Integer} value.
     * @param y The tap's y coordinate, {@link Integer} value.
     * @see #tap(TapParamType)
     */
    default void tap(int x, int y) {
        HPLog.printft("Tapping at x: %d, y: %d", x, y);
        tap(TapParam.builder().withX(x).withY(y).build());
    }

    /**
     * Same as above, but uses a default {@link Tuple}.
     * @param coordinates {@link Tuple} instance.
     */
    default void tap(@NotNull Tuple<Integer, Integer> coordinates) {
        tap(coordinates.A, coordinates.B);
    }

    /**
     * Same as above, but uses {@link Point}.
     * @param point {@link Point} instance.
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
     * @param <P> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     * @see #tap(TapParamType)
     */
    @NotNull
    default <P extends TapParamType & RetryProviderType> FlowableTransformer<P, Boolean> tapFn() {
        final TapType<?> THIS = this;

        return upstream -> upstream
            .compose(HPReactives.completableFn(THIS::tap))
            .map(HPBooleans::toTrue)
            .defaultIfEmpty(true);
    }

    /**
     * Same as above, but uses a default {@link TapParam}.
     * @return {@link FlowableTransformer} instance.
     * @see #tap(int, int)
     */
    @NotNull
    default FlowableTransformer<Tuple<Integer, Integer>, Boolean> tapXYFn() {
        final TapType<?> THIS = this;

        return upstream -> upstream
            .compose(HPReactives.completableFn(THIS::tap))
            .map(HPBooleans::toTrue)
            .defaultIfEmpty(true);
    }

    /**
     * Same as above, but uses {@link Point}.
     * @return {@link FlowableTransformer} instance.
     * @see #tapXYFn()
     */
    @NotNull
    default FlowableTransformer<Point, Boolean> tapPointFn() {
        final TapType<?> THIS = this;

        return upstream -> upstream
            .map(a -> Tuple.of(a.getX(), a.getY()))
            .compose(THIS.tapXYFn());
    }

    /**
     * Tap the middle {@link Point} of {@link WebElement}.
     * @return {@link FlowableTransformer} instance.
     * @see #tapMiddle(WebElement)
     */
    @NotNull
    default FlowableTransformer<WebElement, Boolean> tapMiddleFn() {
        final TapType<?> THIS = this;

        return upstream -> upstream
            .compose(HPReactives.completableFn(THIS::tapMiddle))
            .map(HPBooleans::toTrue)
            .defaultIfEmpty(true);
    }
}
