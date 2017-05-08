package org.swiften.xtestkit.engine.base.action.general.type;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.omg.CORBA.TIMEOUT;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.swiften.xtestkit.engine.base.param.AlertParam;
import org.swiften.xtestkit.engine.base.param.NavigateBack;
import org.swiften.xtestkit.engine.base.param.SwipeGestureParam;
import org.swiften.xtestkit.engine.base.param.UnidirectionalSwipeParam;
import org.swiften.xtestkit.engine.base.type.*;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides general actions, such as back/swipe navigation.
 * * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseActionType<D extends WebDriver> extends
    DriverContainerType<D>,
    BaseActionErrorType
{
    /**
     * Implicitly wait before search for elements.
     * @param param A {@link DelayType} param.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxImplicitlyWait(@NotNull DelayType param) {
        final WebDriver.Timeouts TIMEOUTS = driver().manage().timeouts();
        final long DELAY = param.delay();
        final TimeUnit UNIT = param.timeUnit();

        return Completable
            .fromAction(() -> TIMEOUTS.implicitlyWait(DELAY, UNIT))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Navigate backwards for certain number of times.
     * @param param A {@link RepeatableType} object.
     * @return A {@link Flowable} instance.
     * @see WebDriver#navigate()
     * @see WebDriver.Navigation#back()
     */
    @NotNull
    default Flowable<Boolean> rxNavigateBack(@NotNull RepeatableType param) {
        final WebDriver DRIVER = driver();
        final int TIMES = param.times();
        final long DELAY = param.delay();
        final WebDriver.Navigation NAVIGATION = DRIVER.navigate();

        class PerformBack {
            /**
             * Loop the operation until a stopping point is reached.
             * finished navigating back x times.
             */
            @NotNull
            private Completable back(final int ITERATION) {
                if (ITERATION < TIMES) {
                    return Completable
                        .fromAction(NAVIGATION::back)
                        .delay(DELAY, TimeUnit.MILLISECONDS)
                        .andThen(new PerformBack().back(ITERATION + 1));
                }

                return Completable.complete();
            }
        }

        return new PerformBack().back(0).<Boolean>toFlowable().defaultIfEmpty(true);
    }

    /**
     * Same as above, but uses a default {@link NavigateBack} instance.
     * @return A {@link Flowable} instance.
     * @see #rxNavigateBack(RepeatableType)
     */
    @NotNull
    default Flowable<Boolean> rxNavigateBackOnce() {
        return rxNavigateBack(() -> 1);
    }

    /**
     * Dismiss a currently active alert. Either accept or reject.
     * @param PARAM An {@link AlertParam} instance.
     * @return A {@link Flowable} instance.
     * @see WebDriver#switchTo()
     * @see WebDriver.TargetLocator#alert()
     * @see Alert#accept()
     * @see Alert#dismiss()
     */
    @NotNull
    default Flowable<Boolean> rxDismissAlert(@NotNull final AlertParam PARAM) {
        final Alert ALERT = driver().switchTo().alert();

        return Completable
            .fromAction(() -> {
                if (PARAM.shouldAccept()) {
                    ALERT.accept();
                } else {
                    ALERT.dismiss();
                }
            })
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Same as avove, but uses a default {@link AlertParam}.
     * @return A {@link Flowable} instance.
     * @see #rxDismissAlert(AlertParam)
     */
    @NotNull
    default Flowable<Boolean> rxAcceptAlert() {
        AlertParam param = AlertParam.builder().accept().build();
        return rxDismissAlert(param);
    }

    /**
     * Same as above, but uses a default {@link AlertParam}.
     * @return A {@link Flowable} instance.
     * @see #rxDismissAlert(AlertParam)
     */
    @NotNull
    default Flowable<Boolean> rxRejectAlert() {
        AlertParam param = AlertParam.builder().reject().build();
        return rxDismissAlert(param);
    }

    /**
     * Perform a swipe action. However, since {@link WebDriver} does not
     * implement swiping, we need to override this method in subclasses.
     * @param param A {@link SwipeGestureType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxSwipeOnce(@NotNull SwipeGestureType param) {
        return Flowable.just(true);
    }

    /**
     * Perform a repeated swipe action.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default <P extends RepeatableType & SwipeGestureType>
    Flowable<Boolean> rxSwipe(@NotNull P param) {
        final Flowable<Boolean> SWIPE = rxSwipeOnce(param);
        final int TIMES = param.times();
        final long DELAY = param.delay();
        final TimeUnit UNIT = param.timeUnit();

        class PerformSwipe {
            @NotNull
            private Flowable<Boolean> swipe(final int ITERATION) {
                if (ITERATION < TIMES) {
                    return SWIPE
                        .delay(DELAY, UNIT)
                        .flatMap(a -> new PerformSwipe().swipe(ITERATION + 1));
                }

                return Flowable.empty();
            }
        }

        return new PerformSwipe().swipe(0).defaultIfEmpty(true);
    }

    /**
     * Perform a generic unidirectional swipe. This can be used anywhere a non-
     * full swipe is required.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default <P extends DurationType & UnidirectionType & RepeatableType>
    Flowable<Boolean> rxSwipeGenericUnidirectional(@NotNull P param) {
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
                return Flowable.error(new Exception(WRONG_DIRECTION));
        }

        SwipeGestureParam swipeParam = SwipeGestureParam.builder()
            .withStartX(startX)
            .withStartY(startY)
            .withEndX(endX)
            .withEndY(endY)
            .withRepeatableType(param)
            .withDurationType(param)
            .build();

        return rxSwipe(swipeParam);
    }

    /**
     * Perform a generic horizontal swipe motion from left to right.
     * @param param A {@link P} instance.
     * @return A {@link Flowable} instance.
     * @see #rxSwipe(RepeatableType)
     */
    @NotNull
    default <P extends DurationType & RepeatableType>
    Flowable<Boolean> rxSwipeGenericLR(@NotNull P param) {
        UnidirectionalSwipeParam uniParam = UnidirectionalSwipeParam.builder()
            .withDirection(UnidirectionType.Unidirection.LEFT_RIGHT)
            .withRepeatableType(param)
            .withDurationType(param)
            .build();

        return rxSwipeGenericUnidirectional(uniParam);
    }

    /**
     * Perform a generic horizontal swipe motion from right to left.
     * @param param A {@link RepeatableType} instance.
     * @return A {@link Flowable} instance.
     * @see #rxSwipe(RepeatableType)
     */
    @NotNull
    default <P extends DurationType & RepeatableType>
    Flowable<Boolean> rxSwipeGenericRL(@NotNull P param) {
        UnidirectionalSwipeParam uniParam = UnidirectionalSwipeParam.builder()
            .withDirection(UnidirectionType.Unidirection.RIGHT_LEFT)
            .withRepeatableType(param)
            .withDurationType(param)
            .build();

        return rxSwipeGenericUnidirectional(uniParam);
    }
}
