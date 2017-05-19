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
    Flowable<?> rxCompareFirst(@NotNull WebElement element);

    /**
     * Get the result of the comparison with the last sub-element. If this
     * emits a value (i.e. not empty), return a {@link #lastElementDirection()}.
     * It does not matter what element this {@link Flowable} emits - as long
     * as it is not empty, it passes.
     * @param element A {@link WebElement} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<?> rxCompareLast(@NotNull WebElement element);

    /**
     * Get the total number of item differences between the first
     * {@link WebElement}'s value and the target value. We use this value
     * to calculate an approximate number of initial swipes to be performed.
     * @param first The first visible {@link WebElement}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Integer> rxInitialDifference(@NotNull WebElement first);

    /**
     * Get all visible sub-elements in the current scrollable view emitted
     * by {@link #rxScrollableViewToSwipe()}
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rxScrollViewChildItems();

    /**
     * Get the first visible {@link WebElement} in the scrollable view emitted
     * be {@link #rxScrollableViewToSwipe()}.
     * @return A {@link Flowable} instance.
     * @see #rxScrollViewChildItems()
     */
    @NotNull
    default Flowable<WebElement> rxFirstVisibleChildElement() {
        return rxScrollViewChildItems().firstElement().toFlowable();
    }

    /**
     * Get the last visible {@link WebElement} in the scrollable view emitted
     * be {@link #rxScrollableViewToSwipe()}.
     * @return A {@link Flowable} instance.
     * @see #rxScrollViewChildItems()
     */
    @NotNull
    default Flowable<WebElement> rxLastVisibleChildElement() {
        return rxScrollViewChildItems().lastElement().toFlowable();
    }

    /**
     * Get the number of child items currently visible on the screen. Override
     * this method to provide custom values for when the number of child
     * items returned by {@link #rxScrollViewChildItems()} does not correctly
     * reflect the actual number of visible {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #rxScrollViewChildItems()
     */
    @NotNull
    default Flowable<Long> rxScrollViewChildCount() {
        return rxScrollViewChildItems().count().toFlowable();
    }

    /**
     * Get the number of initial swipes to perform to get as close to the
     * target value as possible.
     * @return A {@link Flowable} instance.
     * @see #rxScrollViewChildCount()
     * @see #rxFirstVisibleChildElement()
     */
    @NotNull
    default Flowable<Integer> rxInitialSwipesCount() {
        final SwipeRepeatComparisonType THIS = this;
        final double RATIO = elementSwipeRatio();

        return Flowable.zip(
            THIS.rxScrollViewChildCount()
                .doOnNext(a -> LogUtil.printfThread("%d child items", a))
                .map(Long::doubleValue),

            THIS.rxFirstVisibleChildElement()
                .flatMap(THIS::rxInitialDifference)
                .doOnNext(a -> LogUtil.printfThread(
                    "%d initial difference in elements", a)
                )
                .map(Math::abs)
                .map(Integer::doubleValue),

            (visible, diff) -> (int)(Math.round(diff / visible / RATIO))
        ).doOnNext(a -> LogUtil.printfThread("%d initial swipes", a));
    }

    /**
     * Perform initial swipes a number of times.
     * @param ELEMENT The scrollable {@link WebElement}.
     * @param DIRECTION A {@link Unidirection} instance.
     * @param RATIO A {@link Double} value to be used as the scroll ratio.
     * @param TIMES The number of times to swipe. An {@link Integer} value.
     * @return A {@link Flowable} instance.
     * @see #rxSwipeElement(WebElement, Unidirection, double)
     */
    @NotNull
    default Flowable<Boolean> rxPerformInitialSwipes(
        @NotNull final WebElement ELEMENT,
        @NotNull final Unidirection DIRECTION,
        final double RATIO,
        final int TIMES
    ) {
        final SwipeRepeatComparisonType THIS = this;

        class InitialSwipe {
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Flowable<Boolean> repeat(final int INDEX) {
                if (INDEX < TIMES) {
                    return THIS
                        .rxSwipeElement(ELEMENT, DIRECTION, RATIO)
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
     * @see #rxScrollableViewToSwipe()
     * @see #rxDirectionToSwipe()
     * @see #rxFirstVisibleChildElement()
     * @see #rxInitialDifference(WebElement)
     * @see #rxPerformInitialSwipes(WebElement, Unidirection, double, int)
     */
    @NotNull
    default Flowable<Boolean> rxPerformInitialSwipes() {
        final SwipeRepeatComparisonType THIS = this;

        return Flowable.zip(
            THIS.rxScrollableViewToSwipe(),
            THIS.rxDirectionToSwipe(),
            THIS.rxInitialSwipesCount(),
            (element, direction, times) -> THIS.rxPerformInitialSwipes(
                element, direction, elementSwipeRatio(), times
            ))
            .flatMap(a -> a);
    }

    /**
     * @return A {@link Flowable} instance.
     * @see SwipeRepeatType#rxDirectionToSwipe()
     * @see #rxScrollViewChildItems()
     * @see #rxCompareFirst(WebElement)
     * @see #rxCompareLast(WebElement)
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
            .concatArray(
                THIS.rxFirstVisibleChildElement()
                    .flatMap(THIS::rxCompareFirst)
                    .map(a -> THIS.firstElementDirection()),

                THIS.rxLastVisibleChildElement()
                    .flatMap(THIS::rxCompareLast)
                    .map(a -> THIS.lastElementDirection())
            )
            .firstElement()
            .toFlowable()
            .defaultIfEmpty(defaultDirection());
    }

    /**
     * Override this method to perform initial swipes.
     * @return A {@link Flowable} instance.
     * @see SwipeRepeatType#rxRepeatSwipe()
     * @see #rxPerformInitialSwipes()
     * @see #rxSwipeRecursively()
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxRepeatSwipe() {
        final SwipeRepeatComparisonType THIS = this;
        return rxPerformInitialSwipes().flatMap(a -> THIS.rxSwipeRecursively());
    }
}
