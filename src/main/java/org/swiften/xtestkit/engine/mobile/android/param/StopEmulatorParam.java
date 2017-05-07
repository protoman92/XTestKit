package org.swiften.xtestkit.engine.mobile.android.param;

import org.swiften.xtestkit.engine.base.type.RetriableType;
import org.swiften.xtestkit.engine.mobile.android.ADBHandler;
import org.swiften.xtestkit.system.PortType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/9/17.
 */

/**
 * Parameter object for
 * {@link ADBHandler#rxStopEmulator(StopEmulatorParam)}
 */
public class StopEmulatorParam implements RetriableType, PortType {
    @NotNull
    public static StopEmulatorParam DEFAULT;

    static {
        DEFAULT = new StopEmulatorParam();
    }

    @NotNull
    public static StopEmulatorParam.Builder builder() {
        return new StopEmulatorParam.Builder();
    }

    private int port, minRetries;

    StopEmulatorParam() {}

    //region RetriableType
    @Override
    public int retries() {
        return minRetries;
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
         * Set the {@link #PARAM#minRetries} value.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withMinRetries(int retries) {
            PARAM.minRetries = retries;
            return this;
        }

        /**
         * Set the {@link #PARAM#port} value.
         * @param port An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPort(int port) {
            PARAM.port = port;
            return this;
        }

        /**
         * Set {@link #PARAM#minRetries} and {@link #PARAM#maxRetries}.
         * @param param A {@link RetriableType} instance.
         * @return The current {@link Builder} instance.
         * @see #withMinRetries(int)
         */
        @NotNull
        public Builder withRetryProtocol(@NotNull RetriableType param) {
            return this.withMinRetries(param.retries());
        }

        /**
         * Set {@link #PARAM#port}.
         * @param param A {@link PortType} instance.
         * @return The current {@link Builder} instance.
         * @see #withPort(int)
         */
        @NotNull
        public Builder withPortProtocol(@NotNull PortType param) {
            return this.withPort(param.port());
        }

        @NotNull
        public StopEmulatorParam build() {
            return PARAM;
        }
    }
}
