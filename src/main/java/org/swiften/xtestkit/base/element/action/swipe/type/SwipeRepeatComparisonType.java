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
     * @return A {@link Unidirection} instance.
     */
    @NotNull
    default Unidirection defaultDirection() {
        return Unidirection.UP_DOWN;
    }

    /**
     * Get the {@link Unidirection} to be used after a successful comparison
     * with the first sub-element.
     * @return A {@link Unidirection} instance.
     */
    @NotNull
    default Unidirection firstElementDirection() {
        return Unidirection.UP_DOWN;
    }

    /**
     * Get the {@link Unidirection} to be used after a successful comparison
     * with the last sub-element.
     * @return A {@link Unidirection} instance.
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
     * @param element A {@link WebElement} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<?> rx_compareFirst(@NotNull WebElement element);

    /**
     * Get the result of the comparison with the last sub-element. If this
     * emits a value (i.e. not empty), return a {@link #lastElementDirection()}.
     * It does not matter what element this {@link Flowable} emits - as long
     * as it is not empty, it passes.
     * @param element A {@link WebElement} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<?> rx_compareLast(@NotNull WebElement element);

    /**
     * Get the total number of item differences between the first
     * {@link WebElement}'s value and the target value. We use this value
     * to calculate an approximate number of initial swipes to be performed.
     * @param first The first visible {@link WebElement}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Integer> rx_initialDifference(@NotNull WebElement first);

    /**
     * Get all visible sub-elements in the current scrollable view emitted
     * by {@link #rx_scrollableViewToSwipe()}
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rx_scrollViewChildItems();

    /**
     * Get the first visible {@link WebElement} in the scrollable view emitted
     * be {@link #rx_scrollableViewToSwipe()}.
     * @return A {@link Flowable} instance.
     * @see #rx_scrollViewChildItems()
     */
    @NotNull
    default Flowable<WebElement> rxFirstVisibleChildElement() {
        return rx_scrollViewChildItems().firstElement().toFlowable();
    }

    /**
     * Get the last visible {@link WebElement} in the scrollable view emitted
     * be {@link #rx_scrollableViewToSwipe()}.
     * @return A {@link Flowable} instance.
     * @see #rx_scrollViewChildItems()
     */
    @NotNull
    default Flowable<WebElement> rxLastVisibleChildElement() {
        return rx_scrollViewChildItems().lastElement().toFlowable();
    }

    /**
     * Get the number of child items currently visible on the screen. Override
     * this method to provide custom values for when the number of child
     * items returned by {@link #rx_scrollViewChildItems()} does not correctly
     * reflect the actual number of visible {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #rx_scrollViewChildItems()
     */
    @NotNull
    default Flowable<Long> rx_scrollViewChildCount() {
        return rx_scrollViewChildItems().count().toFlowable();
    }

    /**
     * Get the number of initial swipes to perform to get as close to the
     * target value as possible.
     * @return A {@link Flowable} instance.
     * @see #rx_scrollViewChildCount()
     * @see #rxFirstVisibleChildElement()
     */
    @NotNull
    default Flowable<Integer> rxInitialSwipesCount() {
        final SwipeRepeatComparisonType THIS = this;

        return Flowable.zip(
            THIS.rx_scrollViewChildCount()
                .doOnNext(a -> LogUtil.printfThread("%d child items", a))
                .map(Long::doubleValue),

            THIS.rxFirstVisibleChildElement()
                .flatMap(THIS::rx_initialDifference)
                .doOnNext(a -> LogUtil.printfThread(
                    "%d initial difference in elements", a)
                )
                .map(Math::abs)
                .map(Integer::doubleValue),

            (visible, diff) -> (int)(Math.round(diff / visible))
        ).doOnNext(a -> LogUtil.printfThread("%d initial swipes", a));
    }

    /**
     * Perform initial swipes a number of times.
     * @param ELEMENT The scrollable {@link WebElement}.
     * @param DIRECTION A {@link Unidirection} instance.
     * @param TIMES The number of times to swipe. An {@link Integer} value.
     * @return A {@link Flowable} instance.
     * @see #rx_swipeElement(WebElement, Unidirection, double)
     */
    @NotNull
    default Flowable<Boolean> rxPerformInitialSwipes(
        @NotNull final WebElement ELEMENT,
        @NotNull final Unidirection DIRECTION,
        final int TIMES
    ) {
        final SwipeRepeatComparisonType THIS = this;

        class InitialSwipe {
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Flowable<Boolean> repeat(final int INDEX) {
                if (INDEX < TIMES) {
                    return THIS
                        .rx_swipeElement(ELEMENT, DIRECTION, 1)
                        .flatMap(a -> new InitialSwipe().repeat(INDEX + 1));
                } else {
                    return Flowable.just(true);
                }
            }
        }

        return new InitialSwipe().repeat(0);
    }

    /**
     * Perform initial swipes to get us as close to the target value as
     * possible.
     * @return A {@link Flowable} instance.
     * @see #rx_scrollableViewToSwipe()
     * @see #rxDirectionToSwipe()
     * @see #rxFirstVisibleChildElement()
     * @see #rx_initialDifference(WebElement)
     * @see #rxPerformInitialSwipes(WebElement, Unidirection, int)
     */
    @NotNull
    default Flowable<Boolean> rxPerformInitialSwipes() {
        final SwipeRepeatComparisonType THIS = this;

        return Flowable.zip(
            rx_scrollableViewToSwipe(),
            rxDirectionToSwipe(),
            rxInitialSwipesCount(),
            this::rxPerformInitialSwipes
        ).flatMap(a -> a);
    }

    /**
     * @return A {@link Flowable} instance.
     * @see SwipeRepeatType#rxDirectionToSwipe()
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
    default Flowable<Unidirection> rxDirectionToSwipe() {
        final SwipeRepeatComparisonType THIS = this;

        return Flowable
            .mergeArray(
                THIS.rxFirstVisibleChildElement()
                    .flatMap(THIS::rx_compareFirst)
                    .map(a -> THIS.firstElementDirection()),

                THIS.rxLastVisibleChildElement()
                    .flatMap(THIS::rx_compareLast)
                    .map(a -> THIS.lastElementDirection())
            )
            .firstElement()
            .toFlowable()
            .defaultIfEmpty(defaultDirection());
    }

    /**
     * Override this method to perform initial swipes.
     * @return A {@link Flowable} instance.
     * @see SwipeRepeatType#rx_repeatSwipe()
     * @see #rxPerformInitialSwipes()
     * @see #rxSwipeRecursively()
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_repeatSwipe() {
        final SwipeRepeatComparisonType THIS = this;
        return rxPerformInitialSwipes().flatMap(a -> THIS.rxSwipeRecursively());
    }
}
