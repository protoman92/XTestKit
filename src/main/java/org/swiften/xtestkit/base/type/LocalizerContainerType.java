package org.swiften.xtestkit.base.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.localizer.LocalizerType;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides a {@link LocalizerType} instance for text
 * localization.
 */
public interface LocalizerContainerType {
    /**
     * Get the associated {@link LocalizerType} instance.
     * @return A {@link LocalizerType} instance.
     */
    @NotNull
    LocalizerType localizer();
}
