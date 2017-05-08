package org.swiften.xtestkit.engine.mobile.android.param;

import org.swiften.xtestkit.engine.base.type.RetryType;
import org.swiften.xtestkit.engine.mobile.android.ADBHandler;
import org.swiften.xtestkit.engine.mobile.android.AndroidInstance;
import org.swiften.xtestkit.engine.mobile.android.type.DeviceUIDType;
import org.swiften.xtestkit.system.type.PortType;
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
    RetryType,
    PortType
{
    @NotNull public static StartEmulatorParam DEFAULT;

    static {
        DEFAULT = new StartEmulatorParam();
    }

    /**
     * Get a {@link Builder} instance.
     * @return A {@link Builder} instance.
     */
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

    //region Builder.
    /**
     * Builder class for {@link StartEmulatorParam}.
     */
    public static final class Builder {
        @NotNull final StartEmulatorParam PARAM;

        Builder() {
            PARAM = new StartEmulatorParam();
        }

        /**
         * Set the {@link #deviceName} value.
         * @param name A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceName(@NotNull String name) {
            PARAM.deviceName = name;
            return this;
        }

        /**
         * Set the {@link #deviceUID} value.
         * @param uid A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            PARAM.deviceUID = uid;
            return this;
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
         * Set {@link #retries}.
         * @param param A {@link RetryType} instance.
         * @return A {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryType(@NotNull RetryType param) {
            return this.withRetries(param.retries());
        }

        /**
         * Set {@link #PARAM#deviceUID} from a {@link DeviceUIDType}
         * instance.
         * @param param A {@link DeviceUIDType} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUIDType(@NotNull DeviceUIDType param) {
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
                .withDeviceUIDType(instance)
                .withPort(instance.port());
        }

        @NotNull
        public StartEmulatorParam build() {
            return PARAM;
        }
    }
    //endregion
}
