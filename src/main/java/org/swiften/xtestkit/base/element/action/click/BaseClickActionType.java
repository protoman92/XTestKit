package org.swiften.xtestkit.base.element.action.click;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides methods to click on {@link WebElement}.
 */
public interface BaseClickActionType {
    /**
     * Perform a click action for {@link WebElement}.
     * @param element {@link WebElement} instance.
     * @see WebElement#click()
     */
    default void click(@NotNull WebElement element) {
        element.click();
    }

    /**
     * Send a click event to {@link WebElement} with
     * {@link WebElement#click()}.
     * @param ELEMENT The {@link WebElement} to be clicked.
     * @return {@link Flowable} instance.
     * @see #click(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rxa_click(@NotNull final WebElement ELEMENT) {
        LogUtil.printfThread("Clicking on %s", ELEMENT);

        return Completable
            .fromAction(() -> this.click(ELEMENT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }
}
