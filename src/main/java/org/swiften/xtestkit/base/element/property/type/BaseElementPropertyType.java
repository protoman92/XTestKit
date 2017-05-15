package org.swiften.xtestkit.base.element.property.type;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.type.BaseErrorType;

/**
 * This interface provides interaction capabilities for
 * {@link org.openqa.selenium.WebElement}. Accessor methods are defined here,
 * but individual {@link org.swiften.xtestkit.base.type.PlatformType}
 * subclasses will need its own implementations.
 */
public interface BaseElementPropertyType extends BaseErrorType {
    /**
     * Get text from a {@link WebElement}, assuming that this {@link WebElement}
     * is capable of displaying a text.
     * @param element The {@link WebElement} to be inspected.
     * @return A {@link String} value.
     */
    @NotNull
    default String getText(@NotNull WebElement element) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }
}
