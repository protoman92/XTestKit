package org.swiften.xtestkit.android.param;

import org.swiften.javautilities.protocol.RetryProviderType;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.type.DeviceUIDProviderType;

/**
 * Created by haipham on 3/23/17.
 */

/**
 * Parameter object for
 * {@link ADBHandler#rxa_changeSettings(DeviceSettingParam)}
 */
public class DeviceSettingParam implements DeviceUIDProviderType, RetryProviderType {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String nameSpace;
    @NotNull private String key;
    @NotNull private String value;
    @NotNull private String deviceUID;

    DeviceSettingParam() {
        nameSpace = "";
        key = "";
        value = "";
        deviceUID = "";
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
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see DeviceUIDProviderType#deviceUID()
     * @see #deviceUID
     */
    @NotNull
    @Override
    public String deviceUID() {
        return deviceUID;
    }

    /**
     * Get {@link #nameSpace}.
     * @return {@link String} value.
     * @see #nameSpace
     */
    @NotNull
    public String nameSpace() {
        return nameSpace;
    }

    /**
     * Get {@link #key}.
     * @return {@link String} value.
     * @see #key
     */
    @NotNull
    public String key() {
        return key;
    }

    /**
     * Get {@link #value}.
     * @return {@link String} value.
     * @see #value
     */
    @NotNull
    public String value() {
        return value;
    }

    /**
     * Get 'put' command.
     * @return {@link String} value.
     * @see #key()
     * @see #nameSpace()
     * @see #value()
     */
    @NotNull
    public String cm_put() {
        return String.format("put %1$s %2$s %3$s", nameSpace(), key(), value());
    }

    /**
     * Get 'get' command.
     * @return {@link String} value.
     * @see #key()
     * @see #nameSpace()
     */
    @NotNull
    public String cm_get() {
        return String.format("get %1$s %2$s", nameSpace(), key());
    }

    /**
     * Builder class for {@link DeviceSettingParam}.
     */
    public static final class Builder {
        @NotNull private final DeviceSettingParam PARAM;

        Builder() {
            PARAM = new DeviceSettingParam();
        }

        /**
         * Set the {@link #PARAM#nameSpace} value. Can be one of
         * 'system', 'secure', 'global'.
         * @param nameSpace {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withNameSpace(@NotNull String nameSpace) {
            PARAM.nameSpace = nameSpace;
            return this;
        }

        /**
         * Same as above, but uses 'global' name space.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withGlobalNameSpace() {
            return withNameSpace("global");
        }

        /**
         * Set {@link #PARAM#key} value. Specifies the argument (option) to be
         * changed
         * @param key {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withKey(@NotNull String key) {
            PARAM.key = key;
            return this;
        }

        /**
         * Set {@link #PARAM#value} value. Specifies the new value for the
         * settings specified by {@link #PARAM#key}.
         * @param value {@link Object} that will be converted to a
         *              {@link String}.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withValue(@NotNull Object value) {
            PARAM.value = String.valueOf(value);
            return this;
        }

        /**
         * Set the {@link #PARAM#deviceUID} value.
         * @param uid {@link String} value.
         * @return The current {@link ConnectionParam.Builder} instance.
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
         * @return The current {@link ConnectionParam.Builder} instance.
         */
        @NotNull
        public Builder withDeviceUIDProtocol(@NotNull DeviceUIDProviderType param) {
            return this.withDeviceUID(param.deviceUID());
        }

        @NotNull
        public DeviceSettingParam build() {
            return PARAM;
        }
    }
}
