package org.swiften.xtestkit.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.base.Engine;
import org.swiften.javautilities.protocol.RetryProviderType;

/**
 * Created by haipham on 3/25/17.
 */

/**
 * Parameter object for
 * {@link Engine#rxa_dismissAlert(AlertParam)}
 */
public class AlertParam implements RetryProviderType {
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
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RetryProviderType#retries()
     * @see Constants#DEFAULT_RETRIES
     */
    @Override
    public int retries() {
        return Constants.DEFAULT_RETRIES;
    }

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
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder shouldAccept(boolean accept) {
            PARAM.accept = accept;
            return this;
        }

        /**
         * Same as above, but defaults to true.
         * @return {@link Builder} instance.
         * @see #shouldAccept(boolean)
         */
        public Builder accept() {
            return shouldAccept(true);
        }

        /**
         * Same as above, but defaults to false.
         * @return {@link Builder} instance.
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
