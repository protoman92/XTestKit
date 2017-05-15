package org.swiften.xtestkit.base.element.action.input.type;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

/**
 * This interface provides methods to handle input views.
 */
public interface BaseInputActionType {
    /**
     * Send a certain {@link String} key to a {@link WebElement}.
     * @param ELEMENT The {@link WebElement} that will receive the key.
     * @param TEXT The {@link String} to be sent.
     * @return A {@link Flowable} instance.
     * @see WebElement#sendKeys(CharSequence...)
     */
    @NotNull
    default Flowable<WebElement> rxSendKey(@NotNull final WebElement ELEMENT,
                                           @NotNull final String...TEXT) {
        return Completable
            .fromAction(() -> ELEMENT.sendKeys(TEXT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }
}
