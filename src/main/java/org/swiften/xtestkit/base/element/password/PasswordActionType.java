package org.swiften.xtestkit.base.element.password;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.property.ElementPropertyType;
import org.swiften.xtestkitcomponents.platform.PlatformType;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides methods to work with password fields. These methods
 * should be individually implemented for each
 * {@link PlatformType}.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface PasswordActionType<D extends WebDriver> extends ElementPropertyType {
    /**
     * Toggle password mask to show/hide password.
     * @param element {@link WebElement} instance.
     */
    default void togglePasswordMask(@NotNull WebElement element) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Toggle password mask to show/hide password.
     * @param ELEMENT {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see #togglePasswordMask(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rxa_togglePasswordMask(@NotNull final WebElement ELEMENT) {
        final PasswordActionType THIS = this;

        return Completable
            .fromAction(() -> THIS.togglePasswordMask(ELEMENT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }

    /**
     * Check if {@link WebElement} is hiding a password. Usually if the
     * password is hidden, {@link #getText(WebElement)} will return an
     * empty {@link String}.
     * @param element {@link WebElement} instance.
     * @return {@link Boolean} value.
     * @see #getText(WebElement)
     */
    default boolean isHidingPassword(@NotNull WebElement element) {
        return getText(element).isEmpty();
    }

    /**
     * Check if {@link WebElement} is showing a password.
     * @param element {@link WebElement} instance.
     * @return {@link Boolean} value.
     * @see #isHidingPassword(WebElement)
     */
    default boolean isShowingPassword(@NotNull WebElement element) {
        return !isHidingPassword(element);
    }
}
