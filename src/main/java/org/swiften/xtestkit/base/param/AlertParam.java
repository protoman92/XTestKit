package org.swiften.xtestkit.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.type.RetryType;

/**
 * Created by haipham on 3/25/17.
 */

/**
 * Parameter object for
 * {@link Engine#rxa_dismissAlert(AlertParam)}
 */
public class AlertParam implements RetryType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private boolean accept;

    AlertParam() {}

    /**
     * Get {@link #accept}.
     * @return {@link Boolean} value.
     * @see #accept
     */
    public boolean shouldAccept() {
        return accept;
    }

    //region Builder
    /**
     * Builder for {@link AlertParam}.
     */
    public static final class Builder {
        @NotNull final AlertParam PARAM;

        Builder() {
            PARAM = new AlertParam();
        }

        /**
         * Set the {@link #PARAM#accept} value. This specifies whether to
         * accept or reject to dialog message.
         * @param accept {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder shouldAccept(boolean accept) {
            PARAM.accept = accept;
            return this;
        }

        /**
         * Same as above, but defaults to true.
         * @return The current {@link Builder} instance.
         * @see #shouldAccept(boolean)
         */
        public Builder accept() {
            return shouldAccept(true);
        }

        /**
         * Same as above, but defaults to false.
         * @return The current {@link Builder} instance.
         * @see #shouldAccept(boolean)
         */
        public Builder reject() {
            return shouldAccept(false);
        }

        @NotNull
        public AlertParam build() {
            return PARAM;
        }
    }
    //endregion
}
