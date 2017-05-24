package org.swiften.xtestkit.base.element.action.swipe.type;

/**
 * Created by haipham on 5/11/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;

/**
 * This interface is a more specialized version of {@link SwipeRepeatType}.
 * It gets the first and last sub-elements of the scrollable view, compare
 * their values with a pre-determined value to get the swipe
 * {@link org.swiften.xtestkit.base.element.action.general.model.Unidirection}.
 * Note that this interface is very opinionated.
 */
public interface SwipeRepeatComparisonType extends SwipeRepeatType {
    /**
     * Get the default {@link Unidirection} in case we cannot detect the
     * direction from the sub-elements.
     * @return {@link Unidirection} instance.
     */
    @NotNull
    default Unidirection defaultDirection() {
        return Unidirection.UP_DOWN;
    }

    /**
     * Get the {@link Unidirection} to be used after a successful comparison
     * with the first sub-element.
     * @return {@link Unidirection} instance.
     */
    @NotNull
    default Unidirection firstElementDirection() {
        return Unidirection.UP_DOWN;
    }

    /**
     * Get the {@link Unidirection} to be used after a successful comparison
     * with the last sub-element.
     * @return {@link Unidirection} instance.
     */
    @NotNull
    default Unidirection lastElementDirection() {
        return Unidirection.DOWN_UP;
    }

    /**
     * Get the result of the comparison with the first sub-element. If this
     * emits a value (i.e. not empty), return {@link #firstElementDirection()}.
     * It does not matter what element this {@link Flowable} emits - as long
     * as it is not empty, it passes.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<?> rx_compareFirst(@NotNull WebElement element);

    /**
     * Get the result of the comparison with the last sub-element. If this
     * emits a value (i.e. not empty), return {@link #lastElementDirection()}.
     * It does not matter what element this {@link Flowable} emits - as long
     * as it is not empty, it passes.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<?> rx_compareLast(@NotNull WebElement element);

    /**
     * Get the total number of item differences between the first
     * {@link WebElement}'s value and the target value. We use this value
     * to calculate an approximate number of initial swipes to be performed.
     * @param first The first visible {@link WebElement}.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Integer> rx_initialDifference(@NotNull WebElement first);

    /**
     * Get all visible sub-elements in the current scrollable view emitted
     * by {@link #rx_scrollableViewToSwipe()}
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rx_scrollViewChildItems();

    /**
     * Get the first visible {@link WebElement} in the scrollable view emitted
     * be {@link #rx_scrollableViewToSwipe()}.
     * @return {@link Flowable} instance.
     * @see #rx_scrollViewChildItems()
     */
    @NotNull
    default Flowable<WebElement> rx_firstVisibleChild() {
        return rx_scrollViewChildItems().firstElement().toFlowable();
    }

    /**
     * Get the last visible {@link WebElement} in the scrollable view emitted
     * be {@link #rx_scrollableViewToSwipe()}.
     * @return {@link Flowable} instance.
     * @see #rx_scrollViewChildItems()
     */
    @NotNull
    default Flowable<WebElement> rx_lastVisibleChild() {
        return rx_scrollViewChildItems().lastElement().toFlowable();
    }

    /**
     * Get the number of child items currently visible on the screen. Override
     * this method to provide custom values for when the number of child
     * items returned by {@link #rx_scrollViewChildItems()} does not correctly
     * reflect the actual number of visible {@link WebElement}.
     * @return {@link Flowable} instance.
     * @see #rx_scrollViewChildItems()
     */
    @NotNull
    default Flowable<Long> rx_scrollViewChildCount() {
        return rx_scrollViewChildItems().count().toFlowable();
    }

    /**
     * Get the number of initial swipes to perform to get as close to the
     * target value as possible.
     * @return {@link Flowable} instance.
     * @see #rx_scrollViewChildCount()
     * @see #rx_firstVisibleChild()
     */
    @NotNull
    default Flowable<Integer> rx_initialSwipesCount() {
        final SwipeRepeatComparisonType THIS = this;

        return Flowable.zip(
            THIS.rx_scrollViewChildCount()
                .doOnNext(a -> LogUtil.printfThread("%d child items", a))
                .map(Long::doubleValue),

            THIS.rx_firstVisibleChild()
                .flatMap(THIS::rx_initialDifference)
                .doOnNext(a -> LogUtil.printfThread("%d initial difference", a))
                .map(Math::abs)
                .map(Integer::doubleValue),

            (visible, diff) -> (int)(Math.ceil(diff / visible))
        ).doOnNext(a -> LogUtil.printfThread("%d initial swipes", a));
    }

    /**
     * Perform initial swipes a number of times.
     * @param element The scrollable {@link WebElement}.
     * @param direction {@link Unidirection} instance.
     * @param time The number of times to swipe. {@link Integer} value.
     * @return {@link Flowable} instance.
     * @see #rx_swipeElement(WebElement, Unidirection, double)
     */
    @NotNull
    default Flowable<Boolean> rx_initialSwipes(@NotNull WebElement element,
                                               @NotNull Unidirection direction,
                                               final int time) {
        return rx_initialSwipes(element, direction, time, 0);
    }

    /**
     * Perform initial swipes a number of times.
     * @param ELEMENT The scrollable {@link WebElement}.
     * @param DIRECTION {@link Unidirection} instance.
     * @param TIMES The number of times to swipe. {@link Integer} value.
     * @param CURRENT_INDEX The current swipe index.
     * @return {@link Flowable} instance.
     * @see #rx_swipeElement(WebElement, Unidirection, double)
     */
    @NotNull
    default Flowable<Boolean> rx_initialSwipes(
        @NotNull final WebElement ELEMENT,
        @NotNull final Unidirection DIRECTION,
        final int TIMES,
        final int CURRENT_INDEX
    ) {
        final SwipeRepeatComparisonType THIS = this;

        if (CURRENT_INDEX < TIMES) {
            return THIS.rx_swipeElement(ELEMENT, DIRECTION, 0.8d)
                .flatMap(a -> THIS.rx_initialSwipes(
                    ELEMENT,
                    DIRECTION,
                    TIMES,
                    CURRENT_INDEX + 1)
                );
        } else {
            return Flowable.just(true);
        }
    }

    /**
     * Perform initial swipes to get us as close to the target value as
     * possible.
     * @return {@link Flowable} instance.
     * @see #rx_scrollableViewToSwipe()
     * @see #rx_directionToSwipe()
     * @see #rx_firstVisibleChild()
     * @see #rx_initialDifference(WebElement)
     * @see #rx_initialSwipes(WebElement, Unidirection, int)
     */
    @NotNull
    default Flowable<Boolean> rx_initialSwipes() {
        return Flowable.zip(
            rx_scrollableViewToSwipe(),
            rx_directionToSwipe(),
            rx_initialSwipesCount(),
            this::rx_initialSwipes
        ).flatMap(a -> a);
    }

    /**
     * @return {@link Flowable} instance.
     * @see SwipeRepeatType#rx_directionToSwipe()
     * @see #rx_scrollViewChildItems()
     * @see #rx_compareFirst(WebElement)
     * @see #rx_compareLast(WebElement)
     * @see #firstElementDirection()
     * @see #lastElementDirection()
     * @see #defaultDirection()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Flowable<Unidirection> rx_directionToSwipe() {
        final SwipeRepeatComparisonType THIS = this;

        return Flowable
            .mergeArray(
                THIS.rx_firstVisibleChild()
                    .flatMap(THIS::rx_compareFirst)
                    .map(a -> THIS.firstElementDirection()),

                THIS.rx_lastVisibleChild()
                    .flatMap(THIS::rx_compareLast)
                    .map(a -> THIS.lastElementDirection())
            )
            .firstElement()
            .toFlowable()
            .defaultIfEmpty(defaultDirection());
    }

    /**
     * Override this method to perform initial swipes.
     * @return {@link Flowable} instance.
     * @see SwipeRepeatType#rx_execute()
     * @see #rx_initialSwipes()
     * @see #rx_swipeRecursively()
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_execute() {
        final SwipeRepeatComparisonType THIS = this;
        return rx_initialSwipes().flatMap(a -> THIS.rx_swipeRecursively());
    }
}
