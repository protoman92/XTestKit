package org.swiften.xtestkit.base.element.action.keyboard.type;

/**
 * Created by haipham on 5/15/17.
 */

import io.appium.java_client.MobileDriver;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.swiften.xtestkit.base.BaseEngine;
import org.swiften.xtestkit.base.type.BaseErrorType;

/**
 * This interface provides methods to interact with the keyboard.
 * * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseKeyboardActionType<D extends WebDriver> extends BaseErrorType {
    /**
     * Hide the currently active keyboard.
     */
    default void hideKeyboard() {
        throw new RuntimeException(NOT_IMPLEMENTED);
    };

    /**
     * Hide the currently active keyboard.
     * @return A {@link Flowable} instance.
     * @see #hideKeyboard()
     */
    @NotNull
    default Flowable<Boolean> rxHideKeyboard() {
        return Completable.fromAction(this::hideKeyboard)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
}
