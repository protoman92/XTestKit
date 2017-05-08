package org.swiften.xtestkit.locator.type.base;

/**
 * Created by haipham on 5/7/17.
 */

/**
 * This interface is the base for {@link String}-related locator operations.
 */
@FunctionalInterface
public interface StringType extends AttributeType<String> {
    /**
     * Check whether the locator should ignore case while looking for a
     * particular text.
     * @return A {@link Boolean} value.
     */
    default boolean ignoreCase() {
        return true;
    }
}
