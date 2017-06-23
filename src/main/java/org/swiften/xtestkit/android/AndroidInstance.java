package org.swiften.xtestkit.android;

/**
 * Created by haipham on 4/8/17.
 */

import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkit.android.type.DeviceUIDProviderType;
import org.swiften.xtestkitcomponents.system.network.type.PortProviderType;
import org.jetbrains.annotations.NotNull;

/**
 * Use this class to store device/emulator information.
 */
public class AndroidInstance implements DeviceUIDProviderType, PortProviderType {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String deviceName;
    @NotNull private String uid;
    @NotNull private TestMode mode;

    private int port;

    AndroidInstance() {
        deviceName = "";
        uid = "";
        mode = TestMode.SIMULATED;
    }

    /**
     * Return {@link #deviceName}.
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
    public synchronized int port() {
        return port;
    }

    /**
     * Return the device UID that will be used by adb to identify a device
     * instance.
     * @return {@link String} value.
     */
    @NotNull
    public String deviceUID() {
        switch (mode) {
            case SIMULATED:
                return String.format("emulator-%d", port());

            default:
                return uid;
        }
    }

    /**
     * Set {@link #port}.
     * @param port {@link Integer} value.
     */
    public synchronized void setPort(int port) {
        this.port = port;
    }

    public static final class Builder {
        @NotNull private final AndroidInstance INSTANCE;

        Builder() {
            INSTANCE = new AndroidInstance();
        }

        /**
         * Set the {@link #deviceName} value.
         * @param name {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceName(@NotNull String name) {
            INSTANCE.deviceName = name;
            return this;
        }

        /**
         * Set the {@link #uid} value.
         * @param uid {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            INSTANCE.uid = uid;
            return this;
        }

        /**
         * Set the {@link #INSTANCE#port} value.
         * @param port {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withPort(int port) {
            INSTANCE.port = port;
            return this;
        }

        /**
         * Set the {@link #mode} instance.
         * @param mode {@link TestMode} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withTestMode(@NotNull TestMode mode) {
            INSTANCE.mode = mode;
            return this;
        }

        @NotNull
        public AndroidInstance build() {
            return INSTANCE;
        }
    }
}
