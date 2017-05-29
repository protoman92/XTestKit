package org.swiften.xtestkit.base.element.action.swipe;

/**
 * Created by haipham on 5/11/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.action.general.Unidirection;
import org.swiften.xtestkit.base.param.UnidirectionParam;
import org.swiften.xtestkit.base.type.DriverContainerType;
import org.swiften.xtestkit.base.type.DurationType;
import org.swiften.xtestkit.base.type.RepeatType;
import org.swiften.xtestkit.base.type.UnidirectionType;

/**
 * This interface provides methods to perform swipe gestures.
 */
public interface BaseSwipeType<D extends WebDriver> extends DriverContainerType<D>, SwipeOnceType {
    /**
     * Perform a generic unidirectional swipe. This can be used anywhere a non-
     * full swipe is required.
     * @param element {@link WebElement} instance to be swiped.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see WebElement#getLocation()
     * @see WebElement#getSize()
     * @see #rx_swipe(RepeatType)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    default <P extends DurationType & UnidirectionType & SwipeDampenType & RepeatType>
    Flowable<Boolean> rxa_swipeGeneric(@NotNull WebElement element, @NotNull P param) {
        Point origin = element.getLocation();
        Dimension size = element.getSize();
        double height = size.height, width = size.width;
        int originX = origin.getX(), originY = origin.getY();
        int startX, endX, startY, endY;
        double startRatio = param.startRatio(), endRatio = param.endRatio();

        int lowX = (int)(originX + width * startRatio);
        int midX = (int)(originX + width / 2);
        int highX = (int)(originX + width * endRatio);
        int lowY = (int)(originY + height * startRatio);
        int midY = (int)(originY + height / 2);
        int highY = (int)(originY + height * endRatio);

        switch (param.direction()) {
            case LEFT_RIGHT:
                startX = lowX;
                endX = highX;
                startY = endY = midY;
                break;

            case RIGHT_LEFT:
                startX = highX;
                endX = lowX;
                startY = endY = midY;
                break;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }

        SwipeParam swipeParam = SwipeParam.builder()
            .withStartX(startX)
            .withStartY(startY)
            .withEndX(endX)
            .withEndY(endY)
            .withRepeatableType(param)
            .withDurationType(param)
            .build();

        return rx_swipe(swipeParam);
    }

    /**
     * Perform a generic horizontal swipe motion from left to right.
     * @param param {@link P} instance.
     * @return {@link Flowable} instance.
     * @see #rxa_swipeGeneric(WebElement, DurationType)
     * @see Unidirection#LEFT_RIGHT
     */
    @NotNull
    default <P extends DurationType & RepeatType> Flowable<Boolean>
    rxa_swipeGenericLR(@NotNull WebElement element, @NotNull P param) {
        UnidirectionParam directionParam = UnidirectionParam.builder()
            .withDirection(Unidirection.LEFT_RIGHT)
            .withRepeatableType(param)
            .withDurationType(param)
            .build();

        return rxa_swipeGeneric(element, directionParam);
    }

    /**
     * Perform a generic horizontal swipe motion from right to left.
     * @param param {@link RepeatType} instance.
     * @return {@link Flowable} instance.
     * @see #rxa_swipeGeneric(WebElement, DurationType)
     * @see Unidirection#RIGHT_LEFT
     */
    @NotNull
    default <P extends DurationType & RepeatType> Flowable<Boolean>
    rxa_swipeGenericRL(@NotNull WebElement element, @NotNull P param) {
        UnidirectionParam directionParam = UnidirectionParam.builder()
            .withDirection(Unidirection.RIGHT_LEFT)
            .withRepeatableType(param)
            .withDurationType(param)
            .build();

        return rxa_swipeGeneric(element, directionParam);
    }
}
