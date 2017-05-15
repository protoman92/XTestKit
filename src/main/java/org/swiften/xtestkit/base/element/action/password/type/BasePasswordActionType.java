package org.swiften.xtestkit.base.element.action.password.type;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.action.tap.type.BaseTapType;
import org.swiften.xtestkit.base.element.property.type.BaseElementPropertyType;
import org.swiften.xtestkit.base.type.BaseErrorType;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides methods to work with password fields. These methods
 * should be individually implemented for each
 * {@link org.swiften.xtestkit.base.type.PlatformType}.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BasePasswordActionType<D extends WebDriver> extends
    BaseErrorType, BaseElementPropertyType
{
    /**
     * Toggle password mask to show/hide password.
     * @param element A {@link WebElement} instance.
     */
    default void togglePasswordMask(@NotNull WebElement element) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    /**
     * Toggle password mask to show/hide password.
     * @param ELEMENT A {@link WebElement} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxTogglePasswordMask(@NotNull final WebElement ELEMENT) {
        final BasePasswordActionType THIS = this;

        return Completable
            .fromAction(() -> THIS.togglePasswordMask(ELEMENT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }

    /**
     * Check if a {@link WebElement} is hiding a password. Usually if the
     * password is hidden, {@link #getText(WebElement)} will return an
     * empty {@link String}.
     * @param element A {@link WebElement} instance.
     * @return A {@link Boolean} value.
     * @see #getText(WebElement)
     */
    default boolean isHidingPassword(@NotNull WebElement element) {
        return getText(element).isEmpty();
    }

    /**
     * Check if a {@link WebElement} is showing a password.
     * @param element A {@link WebElement} instance.
     * @return A {@link Boolean} value.
     * @see #isHidingPassword(WebElement)
     */
    default boolean isShowingPassword(@NotNull WebElement element) {
        return !isHidingPassword(element);
    }
}
