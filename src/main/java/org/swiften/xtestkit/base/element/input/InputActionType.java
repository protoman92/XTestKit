package org.swiften.xtestkit.base.element.input;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.util.LogUtil;
import org.swiften.xtestkit.base.element.locator.LocatorType;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
import org.swiften.xtestkitcomponents.platform.PlatformType;

/**
 * This interface provides methods to handle input views.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface InputActionType<D extends WebDriver> extends
    BaseErrorType, BaseInputActionDelayType, LocatorType<D>
{
    /**
     * Send {@link String} keys to {@link WebElement}.
     * @param element {@link WebElement} instance.
     * @param text {@link String} value.
     * @see WebElement#sendKeys(CharSequence...)
     */
    default void sendValue(@NotNull WebElement element, @NotNull String text) {
        LogUtil.printft("Sending '%s' to %s", text, element);
        element.sendKeys(text);
    }

    /**
     * Send a certain {@link String} key to {@link WebElement}.
     * @param ELEMENT The {@link WebElement} that will receive the key.
     * @param TEXT The {@link String} to be sent.
     * @return {@link Flowable} instance.
     * @see #sendValue(WebElement, String)
     */
    @NotNull
    default Flowable<WebElement> rxa_sendValue(@NotNull final WebElement ELEMENT,
                                               @NotNull final String TEXT) {
        final InputActionType THIS = this;

        return Completable
            .fromAction(() -> THIS.sendValue(ELEMENT, TEXT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }

    /**
     * Toggle the next input, for e.g. by clicking the Next key in a Android
     * keyboard. This method must be individually implemented for each
     * {@link PlatformType}.
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
    default Flowable<WebElement> rxa_toggleNextInput(@NotNull final WebElement ELEMENT) {
        final InputActionType<?> THIS = this;

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
    default void endInput(@NotNull WebElement element) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Toggle the done input.
     * @param ELEMENT The currently active editable {@link WebElement}.
     * @return {@link Flowable} instance.
     * @see #endInput(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rxa_finishInput(@NotNull final WebElement ELEMENT) {
        final InputActionType<?> THIS = this;

        return Completable
            .fromAction(() -> THIS.endInput(ELEMENT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }

    /**
     * Check whether an input {@link WebElement} is the last one out of a
     * list of {@link WebElement}.
     * @param ELEMENT {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see ObjectUtil#nonNull(Object)
     * @see #sameOriginAndSize(WebElement, WebElement)
     * @see #rxe_editables()
     */
    @NotNull
    default Flowable<Boolean> rxv_isLastInput(@NotNull final WebElement ELEMENT) {
        final InputActionType<?> THIS = this;
        return rxe_editables()
            .lastElement()
            .toFlowable()
            .filter(ObjectUtil::nonNull)
            .map(a -> THIS.sameOriginAndSize(a, ELEMENT));
    }

    /**
     * Toggle the next/done input, then delay by a certain duration to allow
     * the views to adjust. We need to check all editable elements to
     * see whether the currently active editable field is the last one in
     * the list.
     * @param ELEMENT {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#isTrue(boolean)
     * @see #rxv_isLastInput(WebElement)
     * @see #rxa_toggleNextInput(WebElement)
     * @see #rxa_finishInput(WebElement)
     * @see #consecutiveNextToggleDelay()
     */
    @NotNull
    default Flowable<WebElement> rxa_toggleNextOrFinishInput(@NotNull final WebElement ELEMENT) {
        final InputActionType<?> THIS = this;

        return rxv_isLastInput(ELEMENT)
            .filter(BooleanUtil::isTrue)
            .flatMap(a -> THIS.rxa_finishInput(ELEMENT))
            .switchIfEmpty(THIS.rxa_toggleNextInput(ELEMENT));
    }
}
