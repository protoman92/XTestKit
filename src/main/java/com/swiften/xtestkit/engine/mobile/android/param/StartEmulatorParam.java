package com.swiften.xtestkit.engine.mobile.android.param;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/8/17.
 */

/**
 * Parameter object for
 * {@link com.swiften.xtestkit.engine.mobile.android.ADBHandler#rxStartEmulator(StartEmulatorParam)}
 */
public class StartEmulatorParam implements RetryProtocol {
    @NotNull public static StartEmulatorParam DEFAULT;

    static {
        DEFAULT = new StartEmulatorParam();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String deviceName;

    private int port, minRetries, maxRetries;

    StartEmulatorParam() {
        deviceName = "";
    }

    @Override
    public int minRetries() {
        return minRetries;
    }

    @Override
    public int maxRetries() {
        return maxRetries;
    }

    /**
     * Return {@link #deviceName).
     * @return A {@link String} value.
     */
    @NotNull
    public String deviceName() {
        return deviceName;
    }

    /**
     * Return {@link #port}.
     * @return An {@link Integer} value.
     */
    public int port() {
        return port;
    }

    public static final class Builder {
        @NotNull final StartEmulatorParam PARAM;

        Builder() {
            PARAM = new StartEmulatorParam();
        }

        /**
         * Set the {@link #PARAM#deviceName} value.
         * @param name A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceName(@NotNull String name) {
            PARAM.deviceName = name;
            return this;
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
         * Set the {@link #PARAM#maxRetries} value.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        public Builder withMaxRetries(int retries) {
            PARAM.maxRetries = retries;
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
         * @param param A {@link RetryProtocol} instance.
         * @return A {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryProtocol(@NotNull RetryProtocol param) {
            return this
                .withMinRetries(param.minRetries())
                .withMaxRetries(param.maxRetries());
        }

        @NotNull
        public StartEmulatorParam build() {
            return PARAM;
        }
    }
}
