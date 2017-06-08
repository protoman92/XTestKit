package org.swiften.xtestkit.base.element.swipe;

/**
 * Created by haipham on 5/11/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkitcomponents.direction.Unidirection;

/**
 * This interface provides methods to repeatedly scroll a scrollable view so
 * long as a condition is satisfied.
 */
public interface MultiSwipeType extends SwipeOnceType {
    /**
     * Get the swipe ratio that is used to dampen the swipe gesture in order
     * to avoid a full unidirectional swipe.
     * @return {@link Flowable} instance.
     */
    @NotNull Flowable<Double> rxe_elementSwipeRatio();

    /**
     * Check whether the swipe action should be repeated, e.g. when we are
     * searching for an element in a list view, we can use {@link Flowable}
     * that emits true as long as the element is not found yet.
     * @return {@link Flowable} instance.
     */
    @NotNull Flowable<Boolean> rxv_shouldKeepSwiping();

    /**
     * Get the {@link WebElement} to swipe.
     * @return {@link Flowable} instance.
     */
    @NotNull Flowable<WebElement> rxe_scrollableViewToSwipe();

    /**
     * Get the {@link Unidirection} to swipe towards.
     * @return {@link Flowable} instance.
     */
    @NotNull Flowable<Unidirection> rxe_swipeDirection();

    /**
     * Repeat a scroll while a condition is satisfied.
     * @return {@link Flowable} instance.
     * @see #rxv_shouldKeepSwiping()
     * @see #rxe_scrollableViewToSwipe()
     * @see #rxe_swipeDirection()
     * @see #rxa_swipeElement(WebElement, Unidirection, double)
     * @see #rxa_performAction()
     * @see RxUtil#error()
     */
    @NotNull
    default Flowable<Boolean> rxa_swipeRecursively() {
        final MultiSwipeType THIS = this;

        return rxv_shouldKeepSwiping()
            .switchIfEmpty(RxUtil.error())
            .onErrorResumeNext(Flowable
                .zip(
                    rxe_scrollableViewToSwipe(),
                    rxe_swipeDirection(),
                    rxe_elementSwipeRatio(),
                    THIS::rxa_swipeElement
                )
                .flatMap(a -> a)
                .flatMap(a -> THIS.rxa_swipeRecursively())
            );
    }

    /**
     * Repeat a scroll while a condition is satisfied.
     * @return {@link Flowable} instance.
     * @see #rxa_swipeRecursively()
     */
    @NotNull
    default Flowable<Boolean> rxa_performAction() {
        return rxa_swipeRecursively();
    }

    /**
     * Scroll the picker list view to a new page or the previous page.
     * @param element The calendar list view {@link WebElement}.
     * @param direction {@link Unidirection} instance.
     * @param scrollRatio A dampening ratio for a vertical scroll.
     * @return {@link Flowable} instance.
     * @see Dimension#getHeight()
     * @see Dimension#getWidth()
     * @see Point#getX()
     * @see Point#getY()
     * @see SwipeParam.Builder#withStartX(int)
     * @see SwipeParam.Builder#withStartY(int)
     * @see SwipeParam.Builder#withEndX(int)
     * @see SwipeParam.Builder#withEndY(int)
     * @see Unidirection#DOWN_UP
     * @see Unidirection#NONE
     * @see Unidirection#UP_DOWN
     * @see WebElement#getLocation()
     * @see WebElement#getSize()
     * @see #rxa_swipeOnce(SwipeType)
     */
    @NotNull
    default Flowable<Boolean> rxa_swipeElement(@NotNull WebElement element,
                                               @NotNull Unidirection direction,
                                               double scrollRatio) {
        Dimension dimension = element.getSize();
        Point location = element.getLocation();
        double height = dimension.getHeight();
        int startX = 0, startY = 0, endX = 0, endY = 0;

        /* The direction corresponds to whether the year being searched is
         * after or before the current selected year */
        switch (direction) {
            case UP_DOWN:
                endY = (int)(location.getY() + height);
                startY = (int)(endY - height * scrollRatio);
                startX = location.getX() + dimension.getWidth() / 2;
                endX = startX;
                break;

            case DOWN_UP:
                endY = location.getY();
                startY = (int)(endY + height * scrollRatio);
                startX = location.getX() + dimension.getWidth() / 2;
                endX = startX;
                break;

            case NONE:
                return Flowable.just(true);

            default:
                break;
        }

        return rxa_swipeOnce(SwipeParam.builder()
            .withStartX(startX)
            .withEndX(endX)
            .withStartY(startY)
            .withEndY(endY)
            .build());
    }
}
