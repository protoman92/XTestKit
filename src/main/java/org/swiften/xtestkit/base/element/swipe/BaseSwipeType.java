package org.swiften.xtestkit.base.element.swipe;

/**
 * Created by haipham on 5/11/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.type.DriverContainerType;
import org.swiften.xtestkitcomponents.common.DurationType;
import org.swiften.xtestkitcomponents.common.RepeatType;
import org.swiften.xtestkitcomponents.direction.DirectionContainerType;

/**
 * This interface provides methods to perform swipe gestures.
 */
public interface BaseSwipeType<D extends WebDriver> extends DriverContainerType<D>, SwipeOnceType {
    /**
     * Perform a generic unidirectional swipe. This can be used anywhere a non-
     * full swipe is required.
     * The start and end coordinates will be computed using specified
     * {@link Point} origin and {@link Dimension} window size.
     * @param origin {@link Point} instance.
     * @param size {@link Dimension} instance.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see Dimension#getHeight()
     * @see Dimension#getWidth()
     * @see Point#getX()
     * @see Point#getY()
     * @see P#anchorRatio()
     * @see P#endRatio()
     * @see P#startRatio()
     * @see SwipeParam.Builder#withStartX(int)
     * @see SwipeParam.Builder#withEndX(int)
     * @see SwipeParam.Builder#withStartY(int)
     * @see SwipeParam.Builder#withEndY(int)
     * @see SwipeParam.Builder#withRepeatableType(RepeatType)
     * @see SwipeParam.Builder#withDurationType(DurationType)
     * @see org.swiften.xtestkitcomponents.direction.Direction#DOWN_UP
     * @see org.swiften.xtestkitcomponents.direction.Direction#LEFT_RIGHT
     * @see org.swiften.xtestkitcomponents.direction.Direction#RIGHT_LEFT
     * @see org.swiften.xtestkitcomponents.direction.Direction#UP_DOWN
     * @see #rxa_swipe(RepeatType)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    default <P extends
        DirectionContainerType &
        DurationType &
        RepeatType &
        RelativeSwipePositionType> Flowable<Boolean>
    rxa_swipeGeneric(@NotNull Point origin,
                     @NotNull Dimension size,
                     @NotNull P param) {
        double height = size.getHeight(), width = size.getWidth();
        int originX = origin.getX(), originY = origin.getY();
        int startX, endX, startY, endY;
        double startRatio = param.startRatio();
        double endRatio = param.endRatio();
        double anchorRatio = param.anchorRatio();

        int lowX = (int)(originX + width * startRatio);
        int midX = (int)(originX + width * anchorRatio);
        int highX = (int)(originX + width * endRatio);
        int lowY = (int)(originY + height * startRatio);
        int midY = (int)(originY + height * anchorRatio);
        int highY = (int)(originY + height * endRatio);

        switch (param.direction()) {
            case UP_DOWN:
                startX = endX = midX;
                startY = lowY;
                endY = highY;
                break;

            case DOWN_UP:
                startX = endX = midX;
                startY = highY;
                endY = lowY;
                break;

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

        return rxa_swipe(swipeParam);
    }

    /**
     * Perform a generic swipe on a {@link WebElement} instance.
     * @param ELEMENT {@link WebElement} instance.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see WebElement#getLocation()
     * @see WebElement#getSize()
     * @see #rxa_swipeGeneric(Point, Dimension, DirectionContainerType)
     */
    @NotNull
    default <P extends
        DirectionContainerType &
        DurationType &
        RepeatType &
        RelativeSwipePositionType> Flowable<WebElement>
    rxa_swipeGeneric(@NotNull final WebElement ELEMENT, @NotNull P param) {
        Point origin = ELEMENT.getLocation();
        Dimension size = ELEMENT.getSize();
        return rxa_swipeGeneric(origin, size, param).map(a -> ELEMENT);
    }

    /**
     * Perform a generic swipe on the app window.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see WebDriver#manage()
     * @see WebDriver.Options#window()
     * @see WebDriver.Window#getPosition()
     * @see WebDriver.Window#getSize()
     * @see #driver()
     * @see #rxa_swipeGeneric(Point, Dimension, DirectionContainerType)
     */
    @NotNull
    default <P extends
        DirectionContainerType &
        DurationType &
        RepeatType &
        RelativeSwipePositionType> Flowable<Boolean>
    rxa_swipeGeneric(@NotNull P param) {
        WebDriver driver = driver();
        WebDriver.Options options = driver.manage();
        WebDriver.Window window = options.window();
        Point origin = window.getPosition();
        Dimension size = window.getSize();
        return rxa_swipeGeneric(origin, size, param);
    }
}
