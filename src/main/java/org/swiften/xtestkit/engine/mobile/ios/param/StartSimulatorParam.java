package org.swiften.xtestkit.engine.mobile.ios.param;

/**
 * Created by haipham on 4/8/17.
 */

import org.swiften.xtestkit.engine.base.type.RetryType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.mobile.ios.XCRunHandler;

/**
 * Parameter object for
 * {@link XCRunHandler#rxStartSimulator(StartSimulatorParam)}
 */
public class StartSimulatorParam implements RetryType {
    @NotNull public static StartSimulatorParam DEFAULT;

    static {
        DEFAULT = new StartSimulatorParam();
    }

    @NotNull
    public static StartSimulatorParam.Builder builder() {
        return new StartSimulatorParam.Builder();
    }

    @NotNull private String deviceUID;

    private int retries;

    StartSimulatorParam() {
        deviceUID = "";
    }

    @Override
    public int retries() {
        return retries;
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
         * Set the {@link #PARAM#retries} value.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder retries(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set {@link #PARAM#retries} and {@link #PARAM#maxRetries}.
         * @param param A {@link RetryType} instance.
         * @return A {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryProtocol(@NotNull RetryType param) {
            return this.retries(param.retries());
        }

        @NotNull
        public StartSimulatorParam build() {
            return PARAM;
        }
    }
}

