package org.swiften.xtestkit.base.element.swipe;

/**
 * Created by haipham on 5/11/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.general.Unidirection;

/**
 * This interface is a more specialized version of {@link MultiSwipeType}.
 * It gets the first and last sub-elements of the scrollable view, compare
 * their values with a pre-determined value to get the swipe
 * {@link Unidirection}.
 * Note that this interface is very opinionated.
 */
public interface MultiSwipeComparisonType extends MultiSwipeType {
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
    Flowable<?> rxa_compareFirst(@NotNull WebElement element);

    /**
     * Get the result of the comparison with the last sub-element. If this
     * emits a value (i.e. not empty), return {@link #lastElementDirection()}.
     * It does not matter what element this {@link Flowable} emits - as long
     * as it is not empty, it passes.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<?> rxa_compareLast(@NotNull WebElement element);

    /**
     * Get the total number of item differences between the first
     * {@link WebElement}'s value and the target value. We use this value
     * to calculate an approximate number of initial swipes to be performed.
     * @param first The first visible {@link WebElement}.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Integer> rxe_initialDifference(@NotNull WebElement first);

    /**
     * Get all visible sub-elements in the current scrollable view emitted
     * by {@link #rxe_scrollableViewToSwipe()}
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rxe_scrollViewChildItems();

    /**
     * Get the first visible {@link WebElement} in the scrollable view emitted
     * be {@link #rxe_scrollableViewToSwipe()}.
     * @return {@link Flowable} instance.
     * @see #rxe_scrollViewChildItems()
     */
    @NotNull
    default Flowable<WebElement> rxe_firstVisibleChild() {
        return rxe_scrollViewChildItems().firstElement().toFlowable();
    }

    /**
     * Get the last visible {@link WebElement} in the scrollable view emitted
     * be {@link #rxe_scrollableViewToSwipe()}.
     * @return {@link Flowable} instance.
     * @see #rxe_scrollViewChildItems()
     */
    @NotNull
    default Flowable<WebElement> rxe_lastVisibleChild() {
        return rxe_scrollViewChildItems().lastElement().toFlowable();
    }

    /**
     * Get the number of child items currently visible on the screen. Override
     * this method to provide custom values for when the number of child
     * items returned by {@link #rxe_scrollViewChildItems()} does not correctly
     * reflect the actual number of visible {@link WebElement}.
     * @return {@link Flowable} instance.
     * @see #rxe_scrollViewChildItems()
     */
    @NotNull
    default Flowable<Long> rxe_scrollViewChildCount() {
        return rxe_scrollViewChildItems().count().toFlowable();
    }

    /**
     * Get the number of initial swipes to perform to get as close to the
     * target value as possible.
     * @return {@link Flowable} instance.
     * @see #rxe_scrollViewChildCount()
     * @see #rxe_firstVisibleChild()
     */
    @NotNull
    default Flowable<Integer> rxe_initialSwipesCount() {
        final MultiSwipeComparisonType THIS = this;

        return Flowable.zip(
            THIS.rxe_scrollViewChildCount()
                .doOnNext(a -> LogUtil.printfThread("%d child items", a))
                .map(Long::doubleValue),

            THIS.rxe_firstVisibleChild()
                .flatMap(THIS::rxe_initialDifference)
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
     * @see #rxa_swipeElement(WebElement, Unidirection, double)
     */
    @NotNull
    default Flowable<Boolean> rxa_swipeInitially(@NotNull WebElement element,
                                                 @NotNull Unidirection direction,
                                                 final int time) {
        return rxa_swipeInitially(element, direction, time, 0);
    }

    /**
     * Perform initial swipes a number of times.
     * @param ELEMENT The scrollable {@link WebElement}.
     * @param DIRECTION {@link Unidirection} instance.
     * @param TIMES The number of times to swipe. {@link Integer} value.
     * @param CURRENT_INDEX The current swipe index.
     * @return {@link Flowable} instance.
     * @see #rxa_swipeElement(WebElement, Unidirection, double)
     */
    @NotNull
    default Flowable<Boolean> rxa_swipeInitially(
        @NotNull final WebElement ELEMENT,
        @NotNull final Unidirection DIRECTION,
        final int TIMES,
        final int CURRENT_INDEX
    ) {
        final MultiSwipeComparisonType THIS = this;

        if (CURRENT_INDEX < TIMES) {
            return THIS.rxa_swipeElement(ELEMENT, DIRECTION, 0.8d)
                .flatMap(a -> THIS.rxa_swipeInitially(
                    ELEMENT,
                    DIRECTION,
                    TIMES,
                    CURRENT_INDEX + 1
                ));
        } else {
            return Flowable.just(true);
        }
    }

    /**
     * Perform initial swipes to get us as close to the target value as
     * possible.
     * @return {@link Flowable} instance.
     * @see #rxa_swipeInitially(WebElement, Unidirection, int)
     * @see #rxe_scrollableViewToSwipe()
     * @see #rxe_directionToSwipe()
     * @see #rxe_firstVisibleChild()
     * @see #rxe_initialDifference(WebElement)
     */
    @NotNull
    default Flowable<Boolean> rxa_swipeInitially() {
        return Flowable.zip(
            rxe_scrollableViewToSwipe(),
            rxe_directionToSwipe(),
            rxe_initialSwipesCount(),
            this::rxa_swipeInitially
        ).flatMap(a -> a);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see MultiSwipeType#rxe_directionToSwipe()
     * @see #rxa_compareFirst(WebElement)
     * @see #rxa_compareLast(WebElement)
     * @see #rxe_scrollViewChildItems()
     * @see #firstElementDirection()
     * @see #lastElementDirection()
     * @see #defaultDirection()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Flowable<Unidirection> rxe_directionToSwipe() {
        final MultiSwipeComparisonType THIS = this;

        return Flowable
            .mergeArray(
                THIS.rxe_firstVisibleChild()
                    .flatMap(THIS::rxa_compareFirst)
                    .map(a -> THIS.firstElementDirection()),

                THIS.rxe_lastVisibleChild()
                    .flatMap(THIS::rxa_compareLast)
                    .map(a -> THIS.lastElementDirection())
            )
            .lastElement()
            .toFlowable()
            .defaultIfEmpty(defaultDirection());
    }

    /**
     * Override this method to perform initial swipes.
     * @return {@link Flowable} instance.
     * @see MultiSwipeType#rxa_performAction()
     * @see #rxa_swipeInitially()
     * @see #rxa_swipeRecursively()
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_performAction() {
        final MultiSwipeComparisonType THIS = this;
        return rxa_swipeInitially().flatMap(a -> THIS.rxa_swipeRecursively());
    }
}
