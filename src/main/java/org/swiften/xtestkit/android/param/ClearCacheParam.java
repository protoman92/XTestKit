package org.swiften.xtestkit.android.param;

import org.swiften.xtestkit.base.type.AppPackageType;
import org.swiften.javautilities.protocol.RetryType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.type.DeviceUIDType;

/**
 * Created by haipham on 4/10/17.
 */
public class ClearCacheParam implements
    AppPackageType,
    DeviceUIDType,
    RetryType {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String appPackage;
    @NotNull private String deviceUID;

    private int retries;

    ClearCacheParam() {
        appPackage = "";
        deviceUID = "";
    }

    //region AppPackageType
    @NotNull
    @Override
    public String appPackage() {
        return appPackage;
    }
    //endregion

    //region DeviceUIDType
    @NotNull
    @Override
    public String deviceUID() {
        return deviceUID;
    }
    //endregion

    //region RetryType
    @Override
    public int retries() {
        return retries;
    }
    //endregion

    //region Builder.
    /**
     * Builder class for {@link ClearCacheParam}.
     */
    public static final class Builder {
        @NotNull private final ClearCacheParam PARAM;

        Builder() {
            PARAM = new ClearCacheParam();
        }

        /**
         * Set the {@link #appPackage} value.
         * @param appPackage {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withAppPackage(@NotNull String appPackage) {
            PARAM.appPackage = appPackage;
            return this;
        }

        /**
         * Set the {@link #appPackage} value.
         * @param param {@link AppPackageType} instance.
         * @return {@link Builder} instance.
         * @see #withAppPackage(String)
         */
        @NotNull
        public Builder withAppPackageType(@NotNull AppPackageType param) {
            return this.withAppPackage(param.appPackage());
        }

        /**
         * Set the {@link #deviceUID} value.
         * @param deviceUID {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String deviceUID) {
            PARAM.deviceUID = deviceUID;
            return this;
        }

        /**
         * Set the {@link #deviceUID} value.
         * @param param {@link DeviceUIDType} instance.
         * @return {@link Builder} instance.
         * @see #withDeviceUID(String)
         */
        @NotNull
        public Builder withDeviceUIDType(@NotNull DeviceUIDType param) {
            return this.withDeviceUID(param.deviceUID());
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
         * Set the {@link #retries} value.
         * @param param {@link RetryType} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryType(@NotNull RetryType param) {
            return this.withRetries(param.retries());
        }

        @NotNull
        public ClearCacheParam build() {
            return PARAM;
        }
    }
    //endregion
}
