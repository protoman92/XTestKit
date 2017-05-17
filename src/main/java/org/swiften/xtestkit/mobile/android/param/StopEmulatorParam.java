package org.swiften.xtestkit.mobile.android.param;

import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.mobile.android.adb.ADBHandler;
import org.swiften.xtestkit.system.network.type.PortType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/9/17.
 */

/**
 * Parameter object for
 * {@link ADBHandler#rxStopEmulator(StopEmulatorParam)}
 */
public class StopEmulatorParam implements RetryType, PortType {
    @NotNull
    public static StopEmulatorParam DEFAULT;

    static {
        DEFAULT = new StopEmulatorParam();
    }

    @NotNull
    public static StopEmulatorParam.Builder builder() {
        return new StopEmulatorParam.Builder();
    }

    private int port, retries;

    StopEmulatorParam() {}

    //region RetryType
    @Override
    public int retries() {
        return retries;
    }
    //endregion

    //region PortType
    /**
     * Return {@link #port}.
     * @return An {@link Integer} value.
     */
    @Override
    public int port() {
        return port;
    }
    //endregion

    public static final class Builder {
        @NotNull final StopEmulatorParam PARAM;

        Builder() {
            PARAM = new StopEmulatorParam();
        }

        /**
         * Set the {@link #retries} value.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set the {@link #port} value.
         * @param port An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPort(int port) {
            PARAM.port = port;
            return this;
        }

        /**
         * Set {@link #retries} value.
         * @param param A {@link RetryType} instance.
         * @return The current {@link Builder} instance.
         * @see #withRetries(int)
         */
        @NotNull
        public Builder withRetryType(@NotNull RetryType param) {
            return this.withRetries(param.retries());
        }

        /**
         * Set {@link #port}.
         * @param param A {@link PortType} instance.
         * @return The current {@link Builder} instance.
         * @see #withPort(int)
         */
        @NotNull
        public Builder withPortType(@NotNull PortType param) {
            return this.withPort(param.port());
        }

        @NotNull
        public StopEmulatorParam build() {
            return PARAM;
        }
    }
}
