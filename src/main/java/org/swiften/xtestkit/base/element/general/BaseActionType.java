package org.swiften.xtestkit.base.element.general;

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
public interface BaseActionType<D extends WebDriver> extends
    BaseActionDelayType,
    DriverContainerType<D>
{
    /**
     * Implicitly wait before search for elements.
     * @param param {@link DelayType} param.
     * @return {@link Flowable} instance.
     * @see WebDriver.Options#timeouts()
     * @see org.openqa.selenium.WebDriver.Timeouts#implicitlyWait(long, TimeUnit)
     */
    @NotNull
    default Flowable<Boolean> rxa_implicitlyWait(@NotNull DelayType param) {
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
    default Flowable<Boolean> rxa_navigateBackOnce() {
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
     * @see #rxa_navigateBackOnce()
     */
    @NotNull
    default Flowable<Boolean> rxa_navigateBack(@NotNull RepeatType param) {
        final BaseActionType<?> THIS = this;
        final int TIMES = param.times();
        final long DELAY = param.delay();
        final TimeUnit UNIT = TimeUnit.MILLISECONDS;

        return Flowable
            .range(0, TIMES)
            .concatMap(a -> THIS.rxa_navigateBackOnce().delay(DELAY, UNIT));
    }

    /**
     * Dismiss a currently active alert. Either accept or reject.
     * Beware the default implementation of
     * {@link WebDriver.TargetLocator#alert()}, because it throws an error
     * when an alert is not present on the screen.
     * @param PARAM {@link AlertParam} instance.
     * @return {@link Flowable} instance.
     * @see Alert#accept()
     * @see Alert#dismiss()
     * @see AlertParam#shouldAccept()
     * @see WebDriver#switchTo()
     * @see WebDriver.TargetLocator#alert()
     * @see #alertDismissalDelay()
     */
    @NotNull
    default Flowable<Boolean> rxa_dismissAlert(@NotNull final AlertParam PARAM) {
        final Alert ALERT = driver().switchTo().alert();

        return Completable
            .fromAction(() -> {
                if (PARAM.shouldAccept()) {
                    ALERT.accept();
                } else {
                    ALERT.dismiss();
                }
            })
            .onErrorComplete()
            .<Boolean>toFlowable()
            .defaultIfEmpty(true)
            .delay(alertDismissalDelay(), TimeUnit.MILLISECONDS);
    }

    /**
     * Same as avove, but uses a default {@link AlertParam}.
     * @return {@link Flowable} instance.
     * @see AlertParam.Builder#accept()
     * @see #rxa_dismissAlert(AlertParam)
     */
    @NotNull
    default Flowable<Boolean> rxa_acceptAlert() {
        AlertParam param = AlertParam.builder().accept().build();
        return rxa_dismissAlert(param);
    }

    /**
     * Same as above, but uses a default {@link AlertParam}.
     * @return {@link Flowable} instance.
     * @see AlertParam.Builder#reject()
     * @see #rxa_dismissAlert(AlertParam)
     */
    @NotNull
    default Flowable<Boolean> rxa_rejectAlert() {
        AlertParam param = AlertParam.builder().reject().build();
        return rxa_dismissAlert(param);
    }
}
