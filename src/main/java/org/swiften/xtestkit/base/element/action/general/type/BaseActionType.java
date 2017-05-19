package org.swiften.xtestkit.base.element.action.general.type;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.swiften.xtestkit.base.param.AlertParam;
import org.swiften.xtestkit.base.param.NavigateBack;
import org.swiften.xtestkit.base.type.*;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides general actions, such as back/swipe navigation.
 * * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseActionType<D extends WebDriver> extends DriverContainerType<D> {
    /**
     * Implicitly wait before search for elements.
     * @param param A {@link DelayType} param.
     * @return A {@link Flowable} instance.
     * @see WebDriver.Options#timeouts()
     * @see org.openqa.selenium.WebDriver.Timeouts#implicitlyWait(long, TimeUnit)
     */
    @NotNull
    default Flowable<Boolean> rx_implicitlyWait(@NotNull DelayType param) {
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
     * @param param A {@link RepeatType} object.
     * @return A {@link Flowable} instance.
     * @see #driver()
     * @see WebDriver#navigate()
     * @see WebDriver.Navigation#back()
     */
    @NotNull
    default Flowable<Boolean> rx_navigateBack(@NotNull RepeatType param) {
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

        return new PerformBack().back(0)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Same as above, but uses a default {@link NavigateBack} instance.
     * @return A {@link Flowable} instance.
     * @see #rx_navigateBack(RepeatType)
     */
    @NotNull
    default Flowable<Boolean> rx_navigateBackOnce() {
        return rx_navigateBack(() -> 1);
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
    default Flowable<Boolean> rx_dismissAlert(@NotNull final AlertParam PARAM) {
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
     * @see #rx_dismissAlert(AlertParam)
     */
    @NotNull
    default Flowable<Boolean> rx_acceptAlert() {
        return rx_dismissAlert(AlertParam.builder().accept().build());
    }

    /**
     * Same as above, but uses a default {@link AlertParam}.
     * @return A {@link Flowable} instance.
     * @see #rx_dismissAlert(AlertParam)
     */
    @NotNull
    default Flowable<Boolean> rx_rejectAlert() {
        return rx_dismissAlert(AlertParam.builder().reject().build());
    }
}
