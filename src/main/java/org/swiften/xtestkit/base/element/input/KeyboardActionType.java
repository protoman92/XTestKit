package org.swiften.xtestkit.base.element.input;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.swiften.javautilities.util.LogUtil;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;

/**
 * This interface provides methods to interact with the keyboard.
 * * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface KeyboardActionType<D extends WebDriver> extends ErrorProviderType {
    /**
     * Hide the currently active keyboard.
     */
    void hideKeyboard();

    /**
     * Hide the currently active keyboard.
     * @return {@link Flowable} instance.
     * @see #hideKeyboard()
     */
    @NotNull
    default Flowable<Boolean> rxa_hideKeyboard() {
        LogUtil.printft("Hiding keyboard for %s", this);
        return Completable.fromAction(this::hideKeyboard).<Boolean>toFlowable().defaultIfEmpty(true);
    }
}
