package org.swiften.xtestkit.mobile.android.element.property.type;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.property.type.BaseElementPropertyType;

/**
 * Created by haipham on 5/9/17.
 */

/**
 * This interface provides interaction capabilities for
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
 */
public interface AndroidElementPropertyType extends BaseElementPropertyType {
    /**
     * @param element The {@link WebElement} to be inspected.
     * @return A {@link String} value.
     * @see BaseElementPropertyType#getText(WebElement)
     */
    @NotNull
    @Override
    default String getText(@NotNull WebElement element) {
        return element.getAttribute("text");
    }
}
