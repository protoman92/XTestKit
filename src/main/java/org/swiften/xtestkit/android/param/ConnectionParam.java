package org.swiften.xtestkit.android.param;

/**
 * Created by haipham on 3/23/17.
 */

import org.swiften.xtestkit.base.type.RetryType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.type.DeviceUIDType;

/**
 * Parameter object for
 * {@link ADBHandler#rx_toggleInternet(ConnectionParam)}
 */
public class ConnectionParam implements DeviceUIDType, RetryType {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String deviceUID;

    private boolean enable;

    ConnectionParam() {
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

    /**
     * Return {@link #enable}.
     * @return {@link Boolean} value.
     */
    public boolean enable() {
        return enable;
    }

    public static final class Builder {
        @NotNull private final ConnectionParam PARAM;

        Builder() {
            PARAM = new ConnectionParam();
        }

        /**
         * Set the {@link #PARAM#enable} value. Specifies whether connection
         * should be enabled/disabled.
         * @param enable {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder shouldEnable(boolean enable) {
            PARAM.enable = enable;
            return this;
        }

        /**
         * Set the {@link #PARAM#deviceUID} value.
         * @param uid {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            PARAM.deviceUID = uid;
            return this;
        }

        /**
         * Set {@link #PARAM#deviceUID} with {@link DeviceUIDType}
         * instance.
         * @param param {@link DeviceUIDType} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUIDProtocol(@NotNull DeviceUIDType param) {
            return this.withDeviceUID(param.deviceUID());
        }

        @NotNull
        public ConnectionParam build() {
            return PARAM;
        }
    }
}
