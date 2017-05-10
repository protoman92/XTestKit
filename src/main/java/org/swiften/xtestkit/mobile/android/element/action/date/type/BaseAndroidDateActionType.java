package org.swiften.xtestkit.mobile.android.element.action.date.type;

/**
 * Created by haipham on 5/10/17.
 */

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.action.general.type.BaseActionType;
import org.swiften.xtestkit.base.param.SwipeGestureParam;
import org.swiften.xtestkit.base.type.SwipeGestureType;
import org.swiften.xtestkit.mobile.android.type.DatePickerContainerType;

/**
 * This interface is the base for all Android date action types. It defines
 * common methods that will be used by all interfaces that extends it.
 */
public interface BaseAndroidDateActionType extends
    BaseActionType<AndroidDriver<AndroidElement>>
{
    /**
     * Scroll the picker list view to a new page or the previous page.
     * Applicable to {@link DatePickerContainerType.DatePickerType#CALENDAR},
     * or individual spinners in
     * {@link DatePickerContainerType.DatePickerType#SPINNER} mode.
     * @param element The calendar list view {@link WebElement}.
     * @param direction A {@link Unidirection} instance.
     * @param scrollRatio A dampening ratio for a vertical scroll.
     * @return A {@link Flowable} instance.
     * @see #rxSwipeOnce(SwipeGestureType)
     */
    @NotNull
    default Flowable<Boolean> rxScrollPickerView(
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

        SwipeGestureType param = SwipeGestureParam.builder()
            .withStartX(startX)
            .withEndX(startX)
            .withStartY(startY)
            .withEndY(endY)
            .build();

        return rxSwipeOnce(param);
    }

    /**
     * Same as above, but uses a default scroll ratio.
     * @param element The calendar list view {@link WebElement}.
     * @param direction A {@link Unidirection} instance.
     * @return A {@link Flowable} instance.
     * @see #rxScrollPickerView(WebElement, Unidirection, double)
     */
    @NotNull
    default Flowable<Boolean> rxScrollPickerView(@NotNull WebElement element,
                                                 @NotNull Unidirection direction) {
        /* Do not perform a full vertical scroll from top-bottom or bottom-top
         * because we may overshoot. Rather, perform short swipes and
         * repeatedly check for the wanted component */
        double scrollRatio = 0.5d;
        return rxScrollPickerView(element, direction, scrollRatio);
    }
}
