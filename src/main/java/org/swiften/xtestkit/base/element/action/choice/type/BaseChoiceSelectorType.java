package org.swiften.xtestkit.base.element.action.choice.type;

/**
 * Created by haipham on 25/5/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.choice.model.ChoiceMode;

/**
 * This interface provides methods to select choices from various
 * {@link org.swiften.xtestkit.base.element.action.choice.model.ChoiceMode}.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseChoiceSelectorType<D extends WebDriver> extends ChoiceHelperType<D> {
    /**
     * Select a choice using {@link ChoiceType}.
     * @param param {@link ChoiceType} instance.
     * @return {@link Flowable} instance.
     * @see #rx_selectGeneralChoice(ChoiceType)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    default Flowable<Boolean> rx_selectChoice(@NotNull ChoiceType param) {
        ChoiceMode mode = param.mode();

        switch (mode) {
            case GENERAL:
                return rx_selectGeneralChoice(param);

            default:
                return RxUtil.error(NOT_AVAILABLE);
        }
    }

    /**
     * Select a general {@link String} choice.
     * @param param {@link ChoiceType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_selectGeneralChoice(@NotNull ChoiceType param);
}
