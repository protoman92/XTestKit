package org.swiften.xtestkit.base.element.action.general;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.swiften.xtestkit.base.param.AlertParam;
import org.swiften.xtestkit.base.type.DelayType;
import org.swiften.xtestkit.base.type.DriverContainerType;
import org.swiften.xtestkit.base.type.RepeatType;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides general actions, such as back/swipe navigation.
 * * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseActionType<D extends WebDriver> extends DriverContainerType<D> {
    /**
     * Implicitly wait before search for elements.
     * @param param {@link DelayType} param.
     * @return {@link Flowable} instance.
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
     * Navigate back only once.
     * @return {@link Flowable} instance.
     * @see WebDriver#navigate()
     * @see WebDriver.Navigation#back()
     * @see #driver()
     */
    @NotNull
    default Flowable<Boolean> rx_navigateBackOnce() {
        final WebDriver DRIVER = driver();
        final WebDriver.Navigation NAVIGATION = DRIVER.navigate();

        return Completable
            .fromAction(NAVIGATION::back)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Navigate backwards for certain number of times.
     * @param param {@link RepeatType} object.
     * @return {@link Flowable} instance.
     * @see RepeatType#times()
     * @see RepeatType#delay()
     * @see #rx_navigateBackOnce()
     */
    @NotNull
    default Flowable<Boolean> rx_navigateBack(@NotNull RepeatType param) {
        final BaseActionType<?> THIS = this;
        final int TIMES = param.times();
        final long DELAY = param.delay();
        final TimeUnit UNIT = TimeUnit.MILLISECONDS;

        return Flowable
            .range(0, TIMES)
            .concatMap(a -> THIS.rx_navigateBackOnce().delay(DELAY, UNIT));
    }

    /**
     * Dismiss a currently active alert. Either accept or reject.
     * @param PARAM {@link AlertParam} instance.
     * @return {@link Flowable} instance.
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
     * @return {@link Flowable} instance.
     * @see #rx_dismissAlert(AlertParam)
     */
    @NotNull
    default Flowable<Boolean> rx_acceptAlert() {
        return rx_dismissAlert(AlertParam.builder().accept().build());
    }

    /**
     * Same as above, but uses a default {@link AlertParam}.
     * @return {@link Flowable} instance.
     * @see #rx_dismissAlert(AlertParam)
     */
    @NotNull
    default Flowable<Boolean> rx_rejectAlert() {
        return rx_dismissAlert(AlertParam.builder().reject().build());
    }
}
