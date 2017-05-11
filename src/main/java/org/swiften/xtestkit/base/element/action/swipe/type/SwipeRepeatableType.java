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
import org.swiften.xtestkit.base.param.SwipeParam;
import org.swiften.xtestkit.mobile.android.type.DatePickerContainerType;

/**
 * This interface provides methods to repeatedly scroll a scrollable view so
 * long as a condition is satisfied.
 */
public interface SwipeRepeatableType extends SwipeOnceType {
    /**
     * Get the swipe ratio that is used to dampen the swipe gesture in order
     * to avoid a full unidirectional swipe.
     * @return A {@link Double} value.
     */
    double elementSwipeRatio();

    /**
     * Check whether the swipe action should be repeated, e.g. when we are
     * searching for an element in a list view, we can use a {@link Flowable}
     * that emits true as long as the element is not found yet.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxShouldKeepSwiping();

    /**
     * Get the {@link WebElement} to swipe.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rxScrollableElementToSwipe();

    /**
     * Get the {@link Unidirection} to swipe towards.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Unidirection> rxDirectionToSwipe();

    /**
     * Repeat a scroll while a condition is satisfied.
     * @return A {@link Flowable} instance.
     * @see #rxShouldKeepSwiping()
     * @see #rxScrollableElementToSwipe()
     * @see #rxDirectionToSwipe()
     * @see #rxSwipeElement(WebElement, Unidirection, double)
     * @see #rxRepeatSwipe()
     */
    @NotNull
    default Flowable<Boolean> rxRepeatSwipe() {
        return rxShouldKeepSwiping()
            .switchIfEmpty(RxUtil.error(""))
            .onErrorResumeNext(Flowable.zip(
                rxScrollableElementToSwipe(),
                rxDirectionToSwipe(),
                (element, direction) -> rxSwipeElement(
                    element, direction, elementSwipeRatio())
                )
                .flatMap(a -> a)
                .flatMap(a -> rxRepeatSwipe())
            );
    }

    /**
     * Scroll the picker list view to a new page or the previous page.
     * Applicable to {@link DatePickerContainerType.DatePickerType#CALENDAR},
     * or individual spinners in
     * {@link DatePickerContainerType.DatePickerType#SPINNER} mode.
     * @param element The calendar list view {@link WebElement}.
     * @param direction A {@link Unidirection} instance.
     * @param scrollRatio A dampening ratio for a vertical scroll.
     * @return A {@link Flowable} instance.
     * @see #rxSwipeOnce(SwipeType)
     */
    @NotNull
    default Flowable<Boolean> rxSwipeElement(
        @NotNull WebElement element,
        @NotNull Unidirection direction,
        double scrollRatio
    ) {
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

        return rxSwipeOnce(param);
    }
}
