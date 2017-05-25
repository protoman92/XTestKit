package org.swiften.xtestkit.base.element.action.input;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.type.BaseErrorType;

/**
 * This interface provides methods to interact with the keyboard.
 * * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseKeyboardActionType<D extends WebDriver> extends BaseErrorType {
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
    default Flowable<Boolean> rx_hideKeyboard() {
        LogUtil.printfThread("Hiding keyboard for %s", this);
        return Completable.fromAction(this::hideKeyboard).<Boolean>toFlowable().defaultIfEmpty(true);
    }
}
