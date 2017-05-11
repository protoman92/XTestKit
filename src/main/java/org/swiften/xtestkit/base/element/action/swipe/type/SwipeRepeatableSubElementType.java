package org.swiften.xtestkit.base.element.action.swipe.type;

/**
 * Created by haipham on 5/11/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;

/**
 * This interface is a more specialized version of {@link SwipeRepeatableType}.
 * It gets the first and last sub-elements of the scrollable view, compare
 * their values with a pre-determined value to get the swipe
 * {@link org.swiften.xtestkit.base.element.action.general.model.Unidirection}.
 * Note that this interface is very opinionated.
 */
public interface SwipeRepeatableSubElementType extends SwipeRepeatableType {
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
     * Get all visible sub-elements in the current scrollable view emitted
     * by {@link #rxScrollableElementToSwipe()}
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rxScrollViewChildItems();

    /**
     * @return A {@link Flowable} instance.
     * @see SwipeRepeatableType#rxDirectionToSwipe()
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Flowable<Unidirection> rxDirectionToSwipe() {
        final SwipeRepeatableSubElementType THIS = this;

        return Flowable
            .concatArray(
                THIS.rxScrollViewChildItems()
                    .firstElement()
                    .toFlowable()
                    .flatMap(THIS::rxCompareFirst)
                    .map(a -> THIS.firstElementDirection()),

                THIS.rxScrollViewChildItems()
                    .lastElement()
                    .toFlowable()
                    .flatMap(THIS::rxCompareLast)
                    .map(a -> THIS.lastElementDirection())
            )
            .firstElement()
            .toFlowable()
            .defaultIfEmpty(defaultDirection());
    }
}
