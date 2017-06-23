package org.swiften.xtestkit.android.param;

import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkitcomponents.system.network.type.PortProviderType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/9/17.
 */

/**
 * Parameter object for
 * {@link ADBHandler#rxa_stopEmulator(StopEmulatorParam)}
 */
public class StopEmulatorParam implements RetryProviderType, PortProviderType {
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

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RetryProviderType#retries()
     * @see #retries
     */
    @Override
    public int retries() {
        return retries;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see PortProviderType#port()
     * @see #port
     */
    @Override
    public int port() {
        return port;
    }

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
         * @param param {@link RetryProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withRetries(int)
         */
        @NotNull
        public Builder withRetryProvider(@NotNull RetryProviderType param) {
            return this.withRetries(param.retries());
        }

        /**
         * Set {@link #port}.
         * @param param {@link PortProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withPort(int)
         */
        @NotNull
        public Builder withPortProvider(@NotNull PortProviderType param) {
            return this.withPort(param.port());
        }

        @NotNull
        public StopEmulatorParam build() {
            return PARAM;
        }
    }
}
