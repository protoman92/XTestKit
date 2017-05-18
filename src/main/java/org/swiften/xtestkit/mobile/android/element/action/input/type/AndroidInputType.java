package org.swiften.xtestkit.mobile.android.element.action.input.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.input.type.InputType;

/**
 * Created by haipham on 18/5/17.
 */

/**
 * This interface provides methods to help interact with
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID} input-related
 * {@link org.openqa.selenium.WebElement}.
 */
public interface AndroidInputType extends InputType {
    /**
     * Get the view id for {@link org.swiften.xtestkit.mobile.Platform#ANDROID}
     * locator.
     * @return A {@link String} value.
     */
    @NotNull String androidViewId();
}
