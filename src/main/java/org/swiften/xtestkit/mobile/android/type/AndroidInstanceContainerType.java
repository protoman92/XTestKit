package org.swiften.xtestkit.mobile.android.type;

/**
 * Created by haipham on 5/15/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.mobile.android.AndroidInstance;

/**
 * This interface provides a
 * {@link org.swiften.xtestkit.mobile.android.AndroidInstance}.
 */
public interface AndroidInstanceContainerType {
    /**
     * Get a {@link AndroidInstance}.
     * @return A {@link AndroidInstance}.
     */
    @NotNull
    AndroidInstance androidInstance();
}
