package org.swiften.xtestkit.base.element.input;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.rx.HPReactives;
import org.swiften.javautilities.util.HPLog;
import org.swiften.xtestkit.base.element.locator.LocatorType;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.xtestkitcomponents.platform.PlatformType;

/**
 * This interface provides methods to handle input views.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface InputActionType<D extends WebDriver> extends
    ErrorProviderType, InputActionDelayType, LocatorType<D>
{
    /**
     * Send {@link String} keys to {@link WebElement}.
     * @param element {@link WebElement} instance.
     * @param text {@link String} value.
     * @see WebElement#sendKeys(CharSequence...)
     */
    default void sendValue(@NotNull WebElement element, @NotNull String text) {
        HPLog.printft("Sending '%s' to %s", text, element);
        element.sendKeys(text);
    }

    /**
     * Send a certain {@link String} key to {@link WebElement}.
     * @param TEXT The {@link String} to be sent.
     * @return {@link FlowableTransformer} instance.
     * @see #sendValue(WebElement, String)
     */
    @NotNull
    default FlowableTransformer<WebElement, Boolean> sendValueFn(@NotNull final String TEXT) {
        final InputActionType THIS = this;

        return upstream -> upstream
            .compose(HPReactives.completableFn(a -> THIS.sendValue(a, TEXT)))
            .map(HPBooleans::toTrue)
            .defaultIfEmpty(true);
    }

    /**
     * Toggle the next input, for e.g. by clicking the Next key in a Android
     * keyboard. This method must be individually implemented for each
     * {@link PlatformType}.
     * @param element The currently active editable {@link WebElement}.
     */
    void toggleNextInput(@NotNull WebElement element);

    /**
     * Toggle the next input.
     * @return {@link FlowableTransformer} instance.
     * @see #toggleNextInput(WebElement)
     */
    @NotNull
    default FlowableTransformer<WebElement, Boolean> toggleNextInputFn() {
        final InputActionType<?> THIS = this;

        return upstream -> upstream
            .compose(HPReactives.completableFn(THIS::toggleNextInput))
            .map(HPBooleans::toTrue)
            .defaultIfEmpty(true);
    }

    /**
     * Toggle the done button, if available, for e.g. by clicking the submit
     * button on a soft keyboard. This should only be called when the current
     * editable field is the last one.
     * @param element The currently active editable {@link WebElement}.
     */
    void finishInput(@NotNull WebElement element);

    /**
     * Toggle the done input.
     * @return {@link FlowableTransformer} instance.
     * @see #finishInput(WebElement)
     */
    @NotNull
    default FlowableTransformer<WebElement, Boolean> finishInputFn() {
        final InputActionType<?> THIS = this;

        return upstream -> upstream
            .compose(HPReactives.completableFn(THIS::finishInput))
            .map(HPBooleans::toTrue)
            .defaultIfEmpty(true);
    }

    /**
     * Check whether an input {@link WebElement} is the last one out of a
     * list of {@link WebElement}.
     * @return {@link FlowableTransformer} instance.
     * @see #sameOriginAndSize(WebElement, WebElement)
     * @see #rxe_editables()
     */
    @NotNull
    default FlowableTransformer<WebElement, Boolean> isLastInputFn() {
        final InputActionType<?> THIS = this;

        return upstream -> Flowable.zip(
            THIS.rxe_editables().lastElement().toFlowable(),
            upstream,
            THIS::sameOriginAndSize);
    }

    /**
     * Toggle the next/done input, then delay by a certain duration to allow
     * the views to adjust. We need to check all editable elements to
     * see whether the currently active editable field is the last one in
     * the list.
     * @return {@link Flowable} instance.
     * @see #isLastInputFn()
     * @see #toggleNextInputFn()
     * @see #finishInputFn()
     * @see #consecutiveNextToggleDelay()
     */
    @NotNull
    default FlowableTransformer<WebElement, Boolean> toggleNextOrFinishInputFn() {
        final InputActionType<?> THIS = this;

        return upstream -> upstream
            .compose(THIS.isLastInputFn())
            .map(a -> a ? THIS.finishInputFn() : THIS.toggleNextInputFn())
            .flatMap(upstream::compose);
    }
}
