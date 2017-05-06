package com.swiften.xtestkit.engine.base;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/25/17.
 */

/**
 * Parameter object for
 * {@link com.swiften.xtestkit.engine.base.PlatformEngine#rxDismissAlert(AlertParam)}
 */
public class AlertParam implements RetryProtocol {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private boolean accept;

    AlertParam() {}

    public boolean shouldAccept() {
        return accept;
    }

    public static final class Builder {
        @NotNull final AlertParam PARAM;

        Builder() {
            PARAM = new AlertParam();
        }

        /**
         * Set the {@link #PARAM#accept} value. This specifies whether to
         * accept or reject to dialog message.
         * @param accept A {@link Boolean} value.
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
}
