package org.swiften.xtestkit.base.element.visibility;

/**
 * Created by haipham on 5/16/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.locator.type.BaseLocatorType;

/**
 * This interface provides methods to handle view visibility, such as polling
 * until a progress bar finishes. This can be used as a proxy for when a
 * network operation completes.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseVisibilityActionType<D extends WebDriver> extends BaseLocatorType<D> {
    /**
     * Watch {@link WebDriver} until it disappears from the screen, by
     * repeatedly checking its visibility.
     * @param element {@link WebElement} instance.
     * @see WebElement#isDisplayed()
     */
    default void watchUntilHidden(@NotNull WebElement element) {
        if (element.isDisplayed()) {
            watchUntilHidden(element);
        }
    }

    /**
     * Watch {@link WebDriver} until it disappears from the screen, by
     * repeatedly checking its visibility.
     * @param ELEMENT {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see #watchUntilHidden(WebElement)
     */
    @NotNull
    default Flowable<Boolean> rxa_watchUntilHidden(@NotNull final WebElement ELEMENT) {
        final BaseVisibilityActionType THIS = this;

        return Completable
            .fromAction(() -> THIS.watchUntilHidden(ELEMENT))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
}
