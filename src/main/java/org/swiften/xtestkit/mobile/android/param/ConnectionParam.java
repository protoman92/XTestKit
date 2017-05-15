package org.swiften.xtestkit.mobile.android.param;

/**
 * Created by haipham on 3/23/17.
 */

import org.swiften.xtestkit.base.type.RetryType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.mobile.android.adb.ADBHandler;
import org.swiften.xtestkit.mobile.android.type.DeviceUIDType;

/**
 * Parameter object for
 * {@link ADBHandler#rxToggleInternetConnection(ConnectionParam)}
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
     * @return A {@link String} value.
     */
    @NotNull
    @Override
    public String deviceUID() {
        return deviceUID;
    }

    /**
     * Return {@link #enable}.
     * @return A {@link Boolean} value.
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
         * @param enable A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder shouldEnable(boolean enable) {
            PARAM.enable = enable;
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
         * Set {@link #PARAM#deviceUID} with a {@link DeviceUIDType}
         * instance.
         * @param param A {@link DeviceUIDType} instance.
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
