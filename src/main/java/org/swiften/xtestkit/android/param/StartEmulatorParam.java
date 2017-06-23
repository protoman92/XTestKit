package org.swiften.xtestkit.android.param;

import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.AndroidInstance;
import org.swiften.xtestkit.android.type.DeviceUIDProviderType;
import org.swiften.xtestkitcomponents.system.network.type.PortProviderType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/8/17.
 */

/**
 * Parameter object for
 * {@link ADBHandler#rxa_startEmulator(StartEmulatorParam)}
 */
public class StartEmulatorParam implements
    DeviceUIDProviderType,
    RetryProviderType,
    PortProviderType
{
    @NotNull public static StartEmulatorParam DEFAULT;

    static {
        DEFAULT = new StartEmulatorParam();
    }

    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
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
     * @return {@link String} value.
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
     * @return {@link String} value.
     */
    @NotNull
    public String deviceName() {
        return deviceName;
    }

    /**
     * Return {@link #port}.
     * @return {@link Integer} value.
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
         * @param name {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceName(@NotNull String name) {
            PARAM.deviceName = name;
            return this;
        }

        /**
         * Set the {@link #deviceUID} value.
         * @param uid {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            PARAM.deviceUID = uid;
            return this;
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
         * Set {@link #retries}.
         * @param param {@link RetryProviderType} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryProvider(@NotNull RetryProviderType param) {
            return this.withRetries(param.retries());
        }

        /**
         * Set {@link #PARAM#deviceUID} from {@link DeviceUIDProviderType}
         * instance.
         * @param param {@link DeviceUIDProviderType} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUIDProvider(@NotNull DeviceUIDProviderType param) {
            return this.withDeviceUID(param.deviceUID());
        }

        /**
         * Set properties from {@link AndroidInstance} instance.
         * @param instance {@link AndroidInstance} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withAndroidInstance(@NotNull AndroidInstance instance) {
            return this
                .withDeviceName(instance.deviceName())
                .withDeviceUIDProvider(instance)
                .withPort(instance.port());
        }

        @NotNull
        public StartEmulatorParam build() {
            return PARAM;
        }
    }
    //endregion
}
