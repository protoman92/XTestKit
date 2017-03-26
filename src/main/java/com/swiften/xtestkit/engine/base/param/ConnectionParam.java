package com.swiften.xtestkit.engine.base.param;

/**
 * Created by haipham on 3/23/17.
 */

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for
 * {@link com.swiften.xtestkit.engine.mobile.android.AndroidEngine#rxToggleInternetConnection(ConnectionParam)}
 */
public class ConnectionParam implements RetryProtocol {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    private boolean enable;

    ConnectionParam() {}

    public boolean enable() {
        return enable;
    }

    public static final class Builder {
        @NotNull private final ConnectionParam PARAM;

        Builder() {
            PARAM = new ConnectionParam();
        }

        /**
         * Set the {@link #PARAM#enable} value. Specifies whether connection
         * should be enabled/disabled.
         * @param enable A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder shouldEnable(boolean enable) {
            PARAM.enable = enable;
            return this;
        }

        @NotNull
        public ConnectionParam build() {
            return PARAM;
        }
    }
}
