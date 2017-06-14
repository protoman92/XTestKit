package org.swiften.xtestkit.android.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkitcomponents.common.BaseErrorType;

/**
 * Created by haipham on 5/27/17.
 */

/**
 * Convenient {@link Enum} to set
 * {@link org.swiften.xtestkit.android.AndroidEngine#platformVersion}.
 */
public enum AndroidSDK implements BaseErrorType {
    SDK_18,
    SDK_22,
    SDK_23;

    /**
     * Get {@link AndroidSDK} from a version code.
     * @param version {@link String} value.
     * @return {@link AndroidSDK} instance.
     * @see #values()
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public static AndroidSDK from(@NotNull String version) {
        AndroidSDK[] values = values();

        for (AndroidSDK sdk : values) {
            if (sdk.version().equals(version)) {
                return sdk;
            }
        }

        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Get the SDK version {@link String}.
     * @return {@link String} value.
     * @see #SDK_18
     * @see #SDK_22
     * @see #SDK_23
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public String version() {
        switch (this) {
            case SDK_18:
                return "4.3";

            case SDK_22:
                return "5.1";

            case SDK_23:
                return "6.0";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Check if the version is at least M.
     * @return {@link Boolean} value.
     * @see #SDK_23
     */
    public boolean isAtLeastM() {
        switch (this) {
            case SDK_23:
                return true;

            default:
                return false;
        }
    }

    /**
     * Check if the version is at least Lollipop.
     * @return {@link Boolean} value.
     * @see #SDK_22
     * @see #SDK_23
     */
    public boolean isAtLeastLollipop() {
        switch (this) {
            case SDK_22:
            case SDK_23:
                return true;

            default:
                return false;
        }
    }
}
