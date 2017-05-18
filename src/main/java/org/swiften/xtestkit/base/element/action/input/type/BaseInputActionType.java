package org.swiften.xtestkit.base.element.action.input.type;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.type.BaseErrorType;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides methods to handle input views.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseInputActionType<D extends WebDriver> extends
    BaseErrorType, BaseInputActionDelayType, BaseLocatorType<D>
{
    /**
     * Send {@link String} keys to a {@link WebElement}.
     * @param element A {@link WebElement} instance.
     * @param text A varargs of {@link String} values.
     * @see WebElement#sendKeys(CharSequence...)
     */
    default void sendKeys(@NotNull WebElement element, @NotNull String...text) {
        LogUtil.printfThread("Sending keys '%s' to %s", text, element);
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
     * @param element The currently active editable {@link WebElement}.
     */
    default void toggleNextInput(@NotNull WebElement element) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    /**
     * Toggle the next input.
     * @param ELEMENT The currently active editable {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #toggleNextInput(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rxToggleNextInput(@NotNull final WebElement ELEMENT) {
        final BaseInputActionType<?> THIS = this;

        return Completable
            .fromAction(() -> THIS.toggleNextInput(ELEMENT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }

    /**
     * Toggle the done button, if available, for e.g. by clicking the submit
     * button on a soft keyboard. This should only be called when the current
     * editable field is the last one.
     * @param element The currently active editable {@link WebElement}.
     */
    default void toggleDoneInput(@NotNull WebElement element) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    /**
     * Toggle the done input.
     * @param ELEMENT The currently active editable {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #toggleDoneInput(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rxToggleDoneInput(@NotNull final WebElement ELEMENT) {
        final BaseInputActionType<?> THIS = this;

        return Completable
            .fromAction(() -> THIS.toggleDoneInput(ELEMENT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }

    /**
     * Toggle the next/done input, then delay by a certain duration to allow
     * the views to adjust. We need to check all editable elements to
     * see whether the currently active editable field is the last one in
     * the list.
     * @param ELEMENT A {@link WebElement} instance.
     * @return A {@link Flowable} instance.
     * @see #rxAllEditableElements()
     * @see #rxToggleNextInput(WebElement)
     * @see #rxToggleDoneInput(WebElement)
     * @see #consecutiveNextToggleDelay()
     */
    @NotNull
    default Flowable<WebElement> rxToggleNextOrDoneInput(@NotNull final WebElement ELEMENT) {
        final BaseInputActionType<?> THIS = this;
        long delay = consecutiveNextToggleDelay();

        return rxAllEditableElements()
            .lastElement()
            .toFlowable()
            .filter(ObjectUtil::nonNull)
            .filter(a -> {
                Point ap = a.getLocation(), ep = ELEMENT.getLocation();
                Dimension ad = a.getSize(), ed = ELEMENT.getSize();

                /* Since we cannot directly compare two WebElement instances,
                 * we can use a proxy method: by comparing their position
                 * and dimension. Usually editable fields are discrete views
                 * that do not overlap each other */
                return ap.equals(ep) && ad.equals(ed);
            })
            .flatMap(THIS::rxToggleDoneInput)
            .switchIfEmpty(THIS.rxToggleNextInput(ELEMENT))
            .delay(delay, TimeUnit.MILLISECONDS, Schedulers.trampoline());
    }
}
