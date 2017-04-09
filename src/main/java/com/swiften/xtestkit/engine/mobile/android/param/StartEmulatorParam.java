package com.swiften.xtestkit.engine.mobile.android.param;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.android.AndroidInstance;
import com.swiften.xtestkit.engine.mobile.android.protocol.DeviceUIDProtocol;
import com.swiften.xtestkit.system.protocol.PortProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/8/17.
 */

/**
 * Parameter object for
 * {@link com.swiften.xtestkit.engine.mobile.android.ADBHandler#rxStartEmulator(StartEmulatorParam)}
 */
public class StartEmulatorParam implements DeviceUIDProtocol, RetryProtocol, PortProtocol {
    @NotNull public static StartEmulatorParam DEFAULT;

    static {
        DEFAULT = new StartEmulatorParam();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String deviceName;
    @NotNull private String deviceUID;

    private int port;
    private int minRetries;
    private int maxRetries;

    StartEmulatorParam() {
        deviceName = "";
        deviceUID = "";
    }

    /**
     * Return {@link #deviceUID}.
     * @return A {@link String} value.
     */
    @NotNull
    @Override
    public String deviceUID() {
        return deviceUID;
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
         * Set the {@link #PARAM#deviceUID} value.
         * @param uid A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            PARAM.deviceUID = uid;
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

        /**
         * Set {@link #PARAM#deviceUID} from a {@link DeviceUIDProtocol}
         * instance.
         * @param param A {@link DeviceUIDProtocol} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUIDProtocol(@NotNull DeviceUIDProtocol param) {
            return this.withDeviceUID(param.deviceUID());
        }

        /**
         * Set properties from an {@link AndroidInstance} instance.
         * @param instance An {@link AndroidInstance} instance.
         * @return A {@link Builder} instance.
         */
        @NotNull
        public Builder withAndroidInstance(@NotNull AndroidInstance instance) {
            return this
                .withDeviceName(instance.deviceName())
                .withDeviceUIDProtocol(instance)
                .withPort(instance.port());
        }

        @NotNull
        public StartEmulatorParam build() {
            return PARAM;
        }
    }
}
