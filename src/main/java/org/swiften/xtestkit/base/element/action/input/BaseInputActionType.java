package org.swiften.xtestkit.base.element.action.input;

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
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.type.BaseErrorType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * This interface provides methods to handle input views.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseInputActionType<D extends WebDriver> extends
    BaseErrorType, BaseInputActionDelayType, BaseLocatorType<D>
{
    /**
     * Send {@link String} keys to {@link WebElement}.
     * @param element {@link WebElement} instance.
     * @param text A varargs of {@link String} values.
     * @see WebElement#sendKeys(CharSequence...)
     */
    default void sendKeys(@NotNull WebElement element, @NotNull String...text) {
        LogUtil.printfThread("Sending '%s' to %s", Arrays.toString(text), element);
        element.sendKeys(text);
    }

    /**
     * Send a certain {@link String} key to {@link WebElement}.
     * @param ELEMENT The {@link WebElement} that will receive the key.
     * @param TEXT The {@link String} to be sent.
     * @return {@link Flowable} instance.
     * @see #sendKeys(WebElement, String...)
     */
    @NotNull
    default Flowable<WebElement> rx_sendKeys(@NotNull final WebElement ELEMENT,
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
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Toggle the next input.
     * @param ELEMENT The currently active editable {@link WebElement}.
     * @return {@link Flowable} instance.
     * @see #toggleNextInput(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rx_toggleNextInput(@NotNull final WebElement ELEMENT) {
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
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Toggle the done input.
     * @param ELEMENT The currently active editable {@link WebElement}.
     * @return {@link Flowable} instance.
     * @see #toggleDoneInput(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rx_toggleDoneInput(@NotNull final WebElement ELEMENT) {
        final BaseInputActionType<?> THIS = this;

        return Completable
            .fromAction(() -> THIS.toggleDoneInput(ELEMENT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }

    /**
     * Check whether an input {@link WebElement} is the last one out of a
     * list of {@link WebElement}.
     * @param ELEMENT {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see ObjectUtil#nonNull(Object)
     * @see #rx_editable()
     * @see WebElement#getLocation()
     * @see WebElement#getSize()
     */
    @NotNull
    default Flowable<Boolean> rx_isLastInput(@NotNull final WebElement ELEMENT) {
        return rx_editable()
            .lastElement()
            .toFlowable()
            .filter(ObjectUtil::nonNull)
            .map(a -> {
                Point ap = a.getLocation(), ep = ELEMENT.getLocation();
                Dimension ad = a.getSize(), ed = ELEMENT.getSize();

                /* Since we cannot directly compare two WebElement instances,
                 * we can use a proxy method: by comparing their position
                 * and dimension. Usually editable fields are discrete views
                 * that do not overlap each other */
                return ap.equals(ep) && ad.equals(ed);
            });
    }

    /**
     * Toggle the next/done input, then delay by a certain duration to allow
     * the views to adjust. We need to check all editable elements to
     * see whether the currently active editable field is the last one in
     * the list.
     * @param ELEMENT {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#isTrue(boolean)
     * @see #rx_isLastInput(WebElement)
     * @see #rx_toggleNextInput(WebElement)
     * @see #rx_toggleDoneInput(WebElement)
     * @see #consecutiveNextToggleDelay()
     */
    @NotNull
    default Flowable<WebElement> rx_toggleNextOrDoneInput(@NotNull final WebElement ELEMENT) {
        final BaseInputActionType<?> THIS = this;
        long delay = consecutiveNextToggleDelay();

        return rx_isLastInput(ELEMENT)
            .filter(BooleanUtil::isTrue)
            .flatMap(a -> THIS.rx_toggleDoneInput(ELEMENT))
            .switchIfEmpty(THIS.rx_toggleNextInput(ELEMENT))
            .delay(delay, TimeUnit.MILLISECONDS);
    }
}
