package org.swiften.xtestkit.android.type;

/**
 * Created by haipham on 5/15/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.AndroidInstance;

/**
 * This interface provides a
 * {@link org.swiften.xtestkit.android.AndroidInstance}.
 */
public interface AndroidInstanceProviderType {
    /**
     * Get {@link AndroidInstance}.
     * @return {@link AndroidInstance}.
     */
    @NotNull
    AndroidInstance androidInstance();
}
