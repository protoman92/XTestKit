package org.swiften.xtestkit.system.network.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.system.network.type.MaxPortType;
import org.swiften.xtestkit.system.network.type.PortStepType;
import org.swiften.xtestkit.system.network.type.PortType;

/**
 * Created by haipham on 18/5/17.
 */

/**
 * Parameter object for
 * {@link org.swiften.xtestkit.system.network.NetworkHandler#rxCheckUntilPortAvailable(PortType)}
 */
public class PortCheckParam implements PortType, MaxPortType, PortStepType, RetryType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private int port, maxPort, portStep, retries;

    PortCheckParam() {
        port = 0;
        maxPort = MaxPortType.super.maxPort();
        portStep = PortStepType.super.portStep();
        retries = RetryType.super.retries();
    }

    //region Getters
    @Override
    public int port() {
        return port;
    }

    @Override
    public int maxPort() {
        return maxPort;
    }

    @Override
    public int portStep() {
        return portStep;
    }

    @Override
    public int retries() {
        return retries;
    }
    //endregion

    /**
     * Builder class for {@link PortCheckParam}.
     */
    public static final class Builder {
        @NotNull private final PortCheckParam PARAM;

        Builder() {
            PARAM = new PortCheckParam();
        }

        /**
         * Set the {@link #port} value.
         * @param port {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPort(int port) {
            PARAM.port = port;
            return this;
        }

        /**
         * Set the {@link #maxPort} value.
         * @param maxPort {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withMaxPort(int maxPort) {
            PARAM.maxPort = maxPort;
            return this;
        }

        /**
         * Set the {@link #portStep} value.
         * @param step {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPortStep(int step) {
            PARAM.portStep = step;
            return this;
        }

        /**
         * Set the {@link #retries} value.
         * @param retries {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set the {@link #retries} value.
         * @param type {@link RetryType} instance.
         * @return The current {@link Builder} instance.
         * @see #withRetries(int)
         */
        @NotNull
        public Builder withRetryType(@NotNull RetryType type) {
            return withRetries(type.retries());
        }

        /**
         * Get {@link #PARAM}.
         * @return {@link PortCheckParam} instance.
         * @see #PARAM
         */
        @NotNull
        public PortCheckParam build() {
            return PARAM;
        }
    }
}
