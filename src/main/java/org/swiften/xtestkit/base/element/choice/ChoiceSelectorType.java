package org.swiften.xtestkit.base.element.choice;

/**
 * Created by haipham on 25/5/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;

/**
 * This interface provides methods to select choices from various
 * {@link ChoiceMode}.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface ChoiceSelectorType<D extends WebDriver> extends ChoiceHelperType<D> {
    /**
     * Select a choice using {@link ChoiceType}.
     * @param param {@link ChoiceType} instance.
     * @return {@link Flowable} instance.
     * @see ChoiceType#mode()
     * @see ChoiceMode#GENERAL
     * @see #rxa_selectGeneralChoice(ChoiceType)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    default Flowable<Boolean> rxa_selectChoice(@NotNull ChoiceType param) {
        ChoiceMode mode = param.mode();

        switch (mode) {
            case GENERAL:
                return rxa_selectGeneralChoice(param);

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Select a general {@link String} choice.
     * @param param {@link ChoiceType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxa_selectGeneralChoice(@NotNull ChoiceType param);
}
