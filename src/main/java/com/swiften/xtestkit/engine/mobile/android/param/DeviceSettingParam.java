package com.swiften.xtestkit.engine.mobile.android.param;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/23/17.
 */

/**
 * Parameter object for
 * {@link com.swiften.xtestkit.engine.mobile.android.AndroidEngine#cmPutSettings(DeviceSettingParam)}
 */
public class DeviceSettingParam implements RetryProtocol {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull private String nameSpace;
    @NotNull private String key;
    @NotNull private String value;

    DeviceSettingParam() {
        nameSpace = "";
        key = "";
        value = "";
    }

    @NotNull
    public String nameSpace() {
        return nameSpace;
    }

    @NotNull
    public String key() {
        return key;
    }

    @NotNull
    public String value() {
        return value;
    }

    @NotNull
    public String putCommand() {
        return String.format("put %1$s %2$s %3$s", nameSpace, key, value);
    }

    @NotNull
    public String getCommand() {
        return String.format("get %1$s %2$s", nameSpace, key);
    }

    public static final class Builder {
        @NotNull private final DeviceSettingParam PARAM;

        Builder() {
            PARAM = new DeviceSettingParam();
        }

        /**
         * Set the {@link #PARAM#nameSpace} value. Can be one of
         * 'system', 'secure', 'global'.
         * @param nameSpace A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withNameSpace(@NotNull String nameSpace) {
            PARAM.nameSpace = nameSpace;
            return this;
        }

        /**
         * Same as above, but uses 'global' name space.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withGlobalNameSpace() {
            return withNameSpace("global");
        }

        /**
         * Set {@link #PARAM#key} value. Specifies the argument (option) to be
         * changed
         * @param key A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withKey(@NotNull String key) {
            PARAM.key = key;
            return this;
        }

        /**
         * Set {@link #PARAM#value} value. Specifies the new value for the
         * settings specified by {@link #PARAM#key}.
         * @param value An {@link Object} that will be converted to a
         *              {@link String}.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withValue(@NotNull Object value) {
            PARAM.value = String.valueOf(value);
            return this;
        }

        @NotNull
        public DeviceSettingParam build() {
            return PARAM;
        }
    }
}