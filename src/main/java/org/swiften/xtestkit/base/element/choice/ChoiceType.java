package org.swiften.xtestkit.base.element.choice;

/**
 * Created by haipham on 25/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.model.ChoiceInputType;

/**
 * Parameter object for {@link ChoiceSelectorType#rxa_selectChoice(ChoiceType)}
 */
public interface ChoiceType {
    /**
     * Get the associated {@link ChoiceMode}.
     * @return {@link ChoiceMode} instance.
     */
    @NotNull ChoiceMode mode();

    /**
     * Get the associated {@link ChoiceInputType}.
     * @return {@link ChoiceInputType} instance.
     */
    @NotNull ChoiceInputType input();

    /**
     * Get the selected choice.
     * @return {@link String} value.
     */
    @NotNull String selectedChoice();
}
