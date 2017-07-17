package org.swiften.xtestkit.base.element.password;

import io.reactivex.FlowableTransformer;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.rx.HPReactives;
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
    void togglePasswordMask(@NotNull WebElement element);

    /**
     * Toggle password mask to show/hide password.
     * @return {@link FlowableTransformer} instance.
     * @see #togglePasswordMask(WebElement)
     */
    @NotNull
    default FlowableTransformer<WebElement, Boolean> togglePasswordMaskFn() {
        final PasswordActionType THIS = this;

        return upstream -> upstream
            .compose(HPReactives.completableFn(THIS::togglePasswordMask))
            .map(HPBooleans::toTrue)
            .defaultIfEmpty(true);
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
