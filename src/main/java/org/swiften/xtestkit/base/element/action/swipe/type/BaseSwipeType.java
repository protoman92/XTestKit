package org.swiften.xtestkit.base.element.action.swipe.type;

/**
 * Created by haipham on 5/11/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.action.swipe.param.SwipeParam;
import org.swiften.xtestkit.base.param.UnidirectionalSwipeParam;
import org.swiften.xtestkit.base.type.*;

/**
 * This interface provides methods to perform swipe gestures.
 */
public interface BaseSwipeType<D extends WebDriver> extends
    BaseSwipeErrorType, DriverContainerType<D>, SwipeOnceType
{
    /**
     * Perform a generic unidirectional swipe. This can be used anywhere a non-
     * full swipe is required.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rx_swipe(RepeatType)
     * @see WebDriver#manage()
     * @see WebDriver.Options#window()
     * @see WebDriver.Window#getSize()
     */
    @NotNull
    default <P extends DurationType & UnidirectionContainerType & RepeatType>
    Flowable<Boolean> rx_swipeGenericUnidirectional(@NotNull P param) {
        Dimension size = driver().manage().window().getSize();
        double height = size.height, width = size.width;
        int startX, endX, startY, endY;
        double startRatio = 0.3d, endRatio = 0.7d;

        int lowX = (int)(width * startRatio);
        int midX = (int)(width / 2);
        int highX = (int)(width * endRatio);
        int lowY = (int)(height * startRatio);
        int midY = (int)(height / 2);
        int highY = (int)(height * endRatio);

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
                return RxUtil.error(WRONG_DIRECTION);
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
     * @param param A {@link P} instance.
     * @return A {@link Flowable} instance.
     * @see #rx_swipeGenericUnidirectional(DurationType)
     * @see Unidirection#LEFT_RIGHT
     */
    @NotNull
    default <P extends DurationType & RepeatType>
    Flowable<Boolean> rx_swipeGenericLR(@NotNull P param) {
        UnidirectionalSwipeParam uniParam = UnidirectionalSwipeParam.builder()
            .withDirection(Unidirection.LEFT_RIGHT)
            .withRepeatableType(param)
            .withDurationType(param)
            .build();

        return rx_swipeGenericUnidirectional(uniParam);
    }

    /**
     * Perform a generic horizontal swipe motion from right to left.
     * @param param A {@link RepeatType} instance.
     * @return A {@link Flowable} instance.
     * @see #rx_swipeGenericUnidirectional(DurationType)
     * @see Unidirection#RIGHT_LEFT
     */
    @NotNull
    default <P extends DurationType & RepeatType>
    Flowable<Boolean> rxSwipeGenericRL(@NotNull P param) {
        UnidirectionalSwipeParam uniParam = UnidirectionalSwipeParam.builder()
            .withDirection(Unidirection.RIGHT_LEFT)
            .withRepeatableType(param)
            .withDurationType(param)
            .build();

        return rx_swipeGenericUnidirectional(uniParam);
    }
}
