package org.swiften.xtestkit.android.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.BaseErrorType;

/**
 * Created by haipham on 5/27/17.
 */

/**
 * Convenient {@link Enum} to set
 * {@link org.swiften.xtestkit.android.AndroidEngine#platformVersion}.
 */
public enum AndroidVersion implements BaseErrorType {
    SDK_22,
    SDK_23;

    /**
     * Get the SDK version {@link String}.
     * @return {@link String} value.
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public String version() {
        switch (this) {
            case SDK_22:
                return "5.1";

            case SDK_23:
                return "6.0";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}
