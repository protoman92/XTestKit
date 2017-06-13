package org.swiften.xtestkit.base.model;

/**
 * Created by haipham on 5/13/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides convenience methods for testing text-based inputs.
 */
public interface TextInputType extends InputType {
    /**
     * Get a random {@link String} input.
     * @param helper {@link InputHelperType} instance.
     * @return {@link String} value.
     */
    @NotNull String randomInput(@NotNull InputHelperType helper);
}
