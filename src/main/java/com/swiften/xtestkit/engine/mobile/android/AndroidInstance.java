package com.swiften.xtestkit.engine.mobile.android;

/**
 * Created by haipham on 4/8/17.
 */

import com.swiften.xtestkit.engine.base.Platform;
import com.swiften.xtestkit.engine.mobile.TestMode;
import com.swiften.xtestkit.engine.mobile.android.protocol.DeviceUIDProtocol;
import com.swiften.xtestkit.system.protocol.PortProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Use this class to store device/emulator information.
 */
public class AndroidInstance implements DeviceUIDProtocol, PortProtocol {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String deviceName;
    @NotNull private TestMode mode;

    private int port;

    AndroidInstance() {
        deviceName = "";
        mode = TestMode.EMULATOR;
    }

    /**
     * Return {@link #deviceName}.
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

    /**
     * Return the device UID that will be used by adb to identify a device
     * instance.
     * @return A {@link String} value.
     */
    @NotNull
    public String deviceUID() {
        switch (mode) {
            case EMULATOR:
                return String.format("emulator-%d", port());

            default:
                return "";
        }
    }

    /**
     * Set {@link #port}.
     * @param port An {@link Integer} value.
     */
    public void setPort(int port) {
        this.port = port;
    }

    public static final class Builder {
        @NotNull private final AndroidInstance INSTANCE;

        Builder() {
            INSTANCE = new AndroidInstance();
        }

        /**
         * Set the {@link #INSTANCE#deviceName} value.
         * @param name A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceName(@NotNull String name) {
            INSTANCE.deviceName = name;
            return this;
        }

        /**
         * Set the {@link #INSTANCE#port} value.
         * @param port An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPort(int port) {
            INSTANCE.port = port;
            return this;
        }

        /**
         * Set the {@link #INSTANCE#mode} instance.
         * @param mode A {@link TestMode} instance.
         * @return The current {@link Builder} instance.
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
