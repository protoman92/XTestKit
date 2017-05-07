package org.swiften.xtestkit.engine.mobile.android.param;

import org.swiften.xtestkit.engine.base.type.RetriableType;
import org.swiften.xtestkit.engine.mobile.android.ADBHandler;
import org.swiften.xtestkit.engine.mobile.android.AndroidInstance;
import org.swiften.xtestkit.engine.mobile.android.type.DeviceUIDType;
import org.swiften.xtestkit.system.PortType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/8/17.
 */

/**
 * Parameter object for
 * {@link ADBHandler#rxStartEmulator(StartEmulatorParam)}
 */
public class StartEmulatorParam implements
    DeviceUIDType,
    RetriableType,
    PortType {
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
    private int retries;

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
    public int retries() {
        return retries;
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
         * Set the {@link #PARAM#retries} value.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            PARAM.retries = retries;
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
         * Set {@link #PARAM#retries} and {@link #PARAM#maxRetries}.
         * @param param A {@link RetriableType} instance.
         * @return A {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryProtocol(@NotNull RetriableType param) {
            return this.withRetries(param.retries());
        }

        /**
         * Set {@link #PARAM#deviceUID} from a {@link DeviceUIDType}
         * instance.
         * @param param A {@link DeviceUIDType} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUIDProtocol(@NotNull DeviceUIDType param) {
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
