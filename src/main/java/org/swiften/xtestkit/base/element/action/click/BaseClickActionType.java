package org.swiften.xtestkit.base.element.action.click;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides methods to click on a {@link WebElement}.
 */
public interface BaseClickActionType {
    /**
     * Perform a click action for a {@link WebElement}.
     * @param element A {@link WebElement} instance.
     * @see WebElement#click()
     */
    default void click(@NotNull WebElement element) {
        element.click();
    }

    /**
     * Send a click event to a {@link WebElement} with
     * {@link WebElement#click()}.
     * @param ELEMENT The {@link WebElement} to be clicked.
     * @return A {@link Flowable} instance.
     * @see #click(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rxClick(@NotNull final WebElement ELEMENT) {
        return Completable
            .fromAction(() -> this.click(ELEMENT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }
}
