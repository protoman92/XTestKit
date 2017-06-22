package org.swiften.xtestkit.android.param;

import org.swiften.javautilities.protocol.RetryType;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkitcomponents.system.network.type.PortType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/9/17.
 */

/**
 * Parameter object for
 * {@link ADBHandler#rxa_stopEmulator(StopEmulatorParam)}
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
     * @return {@link Integer} value.
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
         * @param retries {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set the {@link #port} value.
         * @param port {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withPort(int port) {
            PARAM.port = port;
            return this;
        }

        /**
         * Set {@link #retries} value.
         * @param param {@link RetryType} instance.
         * @return {@link Builder} instance.
         * @see #withRetries(int)
         */
        @NotNull
        public Builder withRetryType(@NotNull RetryType param) {
            return this.withRetries(param.retries());
        }

        /**
         * Set {@link #port}.
         * @param param {@link PortType} instance.
         * @return {@link Builder} instance.
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
