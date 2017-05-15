package org.swiften.xtestkit.base.element.action.input.type;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.type.BaseErrorType;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides methods to handle input views.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseInputActionType<D extends WebDriver> extends
    BaseErrorType, BaseInputActionDelayType
{
    /**
     * Send {@link String} keys to a {@link WebElement}.
     * @param element A {@link WebElement} instance.
     * @param text A varargs of {@link String} values.
     * @see WebElement#sendKeys(CharSequence...)
     */
    default void sendKeys(@NotNull WebElement element, @NotNull String...text) {
        element.sendKeys(text);
    }

    /**
     * Send a certain {@link String} key to a {@link WebElement}.
     * @param ELEMENT The {@link WebElement} that will receive the key.
     * @param TEXT The {@link String} to be sent.
     * @return A {@link Flowable} instance.
     * @see #sendKeys(WebElement, String...)
     */
    @NotNull
    default Flowable<WebElement> rxSendKey(@NotNull final WebElement ELEMENT,
                                           @NotNull final String...TEXT) {
        final BaseInputActionType THIS = this;

        return Completable
            .fromAction(() -> THIS.sendKeys(ELEMENT, TEXT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }

    /**
     * Toggle the next input, for e.g. by clicking the Next key in a Android
     * keyboard. This method must be individually implemented for each
     * {@link org.swiften.xtestkit.base.type.PlatformType}.
     */
    default void toggleNextInput() {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    /**
     * Toggle the next input, then delay by a certain duration to allow the
     * views to adjust.
     * @return A {@link Flowable} instance.
     * @see #toggleNextInput()
     * @see #consecutiveNextToggleDelay()
     */
    @NotNull
    default Flowable<Boolean> rxToggleNextInput() {
        final BaseInputActionType THIS = this;
        long delay = consecutiveNextToggleDelay();

        return Completable
            .fromAction(THIS::toggleNextInput)
            .delay(delay, TimeUnit.MILLISECONDS, Schedulers.trampoline())
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Toggle next input until there is no more next input.
     * @return A {@link Flowable} instance.
     * @see #rxToggleNextInput()
     */
    @NotNull
    default Flowable<Boolean> rxToggleNextInputUntilDone() {
        final BaseInputActionType THIS = this;
        return rxToggleNextInput().flatMap(a -> THIS.rxToggleNextInput());
    }
}
