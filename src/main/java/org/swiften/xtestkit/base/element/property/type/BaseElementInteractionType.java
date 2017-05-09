package org.swiften.xtestkit.base.element.property.type;

/**
 * Created by haipham on 5/9/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

/**
 * This interface provides interaction capabilities for
 * {@link org.openqa.selenium.WebElement}. Accessor methods are defined here,
 * but individual {@link org.swiften.xtestkit.base.type.PlatformType}
 * subclasses will need its own implementations.
 */
public interface BaseElementInteractionType extends BaseElementInteractionErrorType {
    /**
     * Get text from a {@link WebElement}, assuming that this {@link WebElement}
     * is capable of displaying a text.
     * @param element The {@link WebElement} to be inspected.
     * @return A {@link String} value.
     */
    @NotNull
    default String getText(@NotNull WebElement element) {
        throw new RuntimeException(INSPECTION_NOT_IMPLEMENTED);
    }

    /**
     * Send a certain {@link String} key to a {@link WebElement}.
     * @param ELEMENT The {@link WebElement} that will receive the key.
     * @param TEXT The {@link String} to be sent.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxSendKey(@NotNull final WebElement ELEMENT,
                                        @NotNull final String...TEXT) {
        return Completable
            .fromAction(() -> ELEMENT.sendKeys(TEXT))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Send a click event to a {@link WebElement} with
     * {@link WebElement#click()}.
     * @param ELEMENT The {@link WebElement} to be clicked.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxClick(@NotNull final WebElement ELEMENT) {
        return Completable
            .fromAction(ELEMENT::click)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
}
