package com.swiften.xtestkit.engine.mobile.ios.param;

/**
 * Created by haipham on 4/8/17.
 */

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter object for
 * {@link com.swiften.xtestkit.engine.mobile.ios.XCRunHandler#rxStartSimulator(StartSimulatorParam)}
 */
public class StartSimulatorParam implements RetryProtocol {
    @NotNull public static StartSimulatorParam DEFAULT;

    static {
        DEFAULT = new StartSimulatorParam();
    }

    @NotNull
    public static StartSimulatorParam.Builder builder() {
        return new StartSimulatorParam.Builder();
    }

    @NotNull private String deviceUID;

    private int minRetries, maxRetries;

    StartSimulatorParam() {
        deviceUID = "";
    }

    @Override
    public int minRetries() {
        return minRetries;
    }

    @Override
    public int maxRetries() {
        return maxRetries;
    }

    @NotNull
    public String deviceUID() {
        return deviceUID;
    }

    public static final class Builder {
        @NotNull final StartSimulatorParam PARAM;

        Builder() {
            PARAM = new StartSimulatorParam();
        }

        /**
         * Set the {@link #PARAM#deviceUID} value.
         * @param name A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String name) {
            PARAM.deviceUID = name;
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
        public StartSimulatorParam build() {
            return PARAM;
        }
    }
}

