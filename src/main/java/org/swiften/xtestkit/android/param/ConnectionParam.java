package org.swiften.xtestkit.android.param;

/**
 * Created by haipham on 3/23/17.
 */

import org.swiften.javautilities.protocol.RetryProviderType;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.type.DeviceUIDProviderType;

/**
 * Parameter object for
 * {@link ADBHandler#rxa_toggleInternet(ConnectionParam)}
 */
public class ConnectionParam implements DeviceUIDProviderType, RetryProviderType {
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
     * Override this method to provide default implementation.
     * @return {@link String} value.
     * @see #deviceUID
     */
    @NotNull
    @Override
    public String deviceUID() {
        return deviceUID;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RetryProviderType#retries()
     * @see Constants#DEFAULT_RETRIES
     */
    @Override
    public int retries() {
        return Constants.DEFAULT_RETRIES;
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
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder shouldEnable(boolean enable) {
            PARAM.enable = enable;
            return this;
        }

        /**
         * Set the {@link #PARAM#deviceUID} value.
         * @param uid {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String uid) {
            PARAM.deviceUID = uid;
            return this;
        }

        /**
         * Set {@link #PARAM#deviceUID} with {@link DeviceUIDProviderType}
         * instance.
         * @param param {@link DeviceUIDProviderType} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUIDProtocol(@NotNull DeviceUIDProviderType param) {
            return this.withDeviceUID(param.deviceUID());
        }

        @NotNull
        public ConnectionParam build() {
            return PARAM;
        }
    }
}
