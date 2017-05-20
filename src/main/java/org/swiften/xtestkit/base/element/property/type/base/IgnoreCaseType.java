package org.swiften.xtestkit.base.element.property.type.base;

/**
 * Created by haipham on 5/14/17.
 */

/**
 * This interface provides case-ignoring capabilities for locators.
 */
public interface IgnoreCaseType {
    /**
     * Check whether the locator should ignore case while looking for a
     * particular text.
     * @return {@link Boolean} value.
     */
    default boolean ignoreCase() {
        return true;
    }
}
