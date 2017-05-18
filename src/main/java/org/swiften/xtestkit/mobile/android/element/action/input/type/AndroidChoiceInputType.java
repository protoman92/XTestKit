package org.swiften.xtestkit.mobile.android.element.action.input.type;

/**
 * Created by haipham on 18/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.input.type.ChoiceInputType;

/**
 * This interface provides methods to work with
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}'s choice-based input
 * {@link org.openqa.selenium.WebElement}.
 */
public interface AndroidChoiceInputType extends AndroidInputType, ChoiceInputType {
    /**
     * Get the scroll view picker id for
     * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
     * @return A {@link String} value.
     */
    @NotNull
    String androidScrollViewPickerId();
}
