package org.swiften.xtestkit.base.element.action.visibility;

/**
 * Created by haipham on 5/16/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;

/**
 * This interface provides methods to handle view visibility, such as polling
 * until a progress bar finishes. This can be used as a proxy for when a
 * network operation completes.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseVisibilityActionType<D extends WebDriver> extends BaseLocatorType<D> {
    /**
     * Watch a {@link WebDriver} until it disappears from the screen, by
     * repeatedly checking its visibility.
     * @param element A {@link WebElement} instance.
     * @see WebElement#isDisplayed()
     */
    default void watchUntilHidden(@NotNull WebElement element) {
        if (element.isDisplayed()) {
            watchUntilHidden(element);
        }
    }

    /**
     * Watch a {@link WebDriver} until it disappears from the screen, by
     * repeatedly checking its visibility.
     * @param ELEMENT A {@link WebElement} instance.
     * @return A {@link Flowable} instance.
     * @see #watchUntilHidden(WebElement)
     */
    @NotNull
    default Flowable<Boolean> rx_watchUntilHidden(@NotNull final WebElement ELEMENT) {
        final BaseVisibilityActionType THIS = this;

        return Completable
            .fromAction(() -> THIS.watchUntilHidden(ELEMENT))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
}
