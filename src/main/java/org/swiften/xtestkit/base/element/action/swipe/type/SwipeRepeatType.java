package org.swiften.xtestkit.base.element.action.swipe.type;

/**
 * Created by haipham on 5/11/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.action.swipe.param.SwipeParam;
import org.swiften.xtestkit.mobile.android.element.action.date.type.AndroidDatePickerContainerType;

/**
 * This interface provides methods to repeatedly scroll a scrollable view so
 * long as a condition is satisfied.
 */
public interface SwipeRepeatType extends SwipeOnceType {
    /**
     * Get the swipe ratio that is used to dampen the swipe gesture in order
     * to avoid a full unidirectional swipe.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Double> rx_elementSwipeRatio();

    /**
     * Check whether the swipe action should be repeated, e.g. when we are
     * searching for an element in a list view, we can use {@link Flowable}
     * that emits true as long as the element is not found yet.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_shouldKeepSwiping();

    /**
     * Get the {@link WebElement} to swipe.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rx_scrollableViewToSwipe();

    /**
     * Get the {@link Unidirection} to swipe towards.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Unidirection> rxDirectionToSwipe();

    /**
     * Repeat a scroll while a condition is satisfied.
     * @return {@link Flowable} instance.
     * @see #rx_shouldKeepSwiping()
     * @see #rx_scrollableViewToSwipe()
     * @see #rxDirectionToSwipe()
     * @see #rx_swipeElement(WebElement, Unidirection, double)
     * @see #rx_repeatSwipe()
     */
    @NotNull
    default Flowable<Boolean> rx_swipeRecursively() {
        final SwipeRepeatType THIS = this;

        return rx_shouldKeepSwiping()
            .switchIfEmpty(RxUtil.error())
            .onErrorResumeNext(Flowable
                .zip(
                    rx_scrollableViewToSwipe(),
                    rxDirectionToSwipe(),
                    rx_elementSwipeRatio(),
                    THIS::rx_swipeElement
                )
                .flatMap(a -> a)
                .flatMap(a -> THIS.rx_swipeRecursively())
            );
    }

    /**
     * Repeat a scroll while a condition is satisfied.
     * @return {@link Flowable} instance.
     * @see #rx_swipeRecursively()
     */
    @NotNull
    default Flowable<Boolean> rx_repeatSwipe() {
        return rx_swipeRecursively();
    }

    /**
     * Scroll the picker list view to a new page or the previous page.
     * @param element The calendar list view {@link WebElement}.
     * @param direction {@link Unidirection} instance.
     * @param scrollRatio A dampening ratio for a vertical scroll.
     * @return {@link Flowable} instance.
     * @see #rx_swipeOnce(SwipeType)
     */
    @NotNull
    default Flowable<Boolean> rx_swipeElement(@NotNull WebElement element,
                                              @NotNull Unidirection direction,
                                              double scrollRatio) {
        Dimension dimension = element.getSize();
        Point location = element.getLocation();
        double height = dimension.getHeight();
        int startX = location.getX() + dimension.getWidth() / 2;
        int startY = 0, endY = 0;

        /* Depending on the swipe direction, we need to have different
         * startY and endY values. The direction corresponds to whether the
         * year being searched is after or before the current selected year */
        switch (direction) {
            case UP_DOWN:
                endY = (int)(location.getY() + height);
                startY = (int)(endY - height * scrollRatio);
                break;

            case DOWN_UP:
                endY = location.getY();
                startY = (int)(endY + height * scrollRatio);
                break;

            default:
                break;
        }

        SwipeType param = SwipeParam.builder()
            .withStartX(startX)
            .withEndX(startX)
            .withStartY(startY)
            .withEndY(endY)
            .build();

        return rx_swipeOnce(param);
    }
}
