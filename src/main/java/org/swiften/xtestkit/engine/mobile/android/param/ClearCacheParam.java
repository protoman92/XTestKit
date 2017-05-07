package org.swiften.xtestkit.engine.mobile.android.param;

import org.swiften.xtestkit.engine.base.type.AppPackageType;
import org.swiften.xtestkit.engine.base.type.RetryType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.mobile.android.type.DeviceUIDType;

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
         * @param appPackage A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withAppPackage(@NotNull String appPackage) {
            PARAM.appPackage = appPackage;
            return this;
        }

        /**
         * Set the {@link #appPackage} value.
         * @param param A {@link AppPackageType} instance.
         * @return The current {@link Builder} instance.
         * @see #withAppPackage(String)
         */
        @NotNull
        public Builder withAppPackageType(@NotNull AppPackageType param) {
            return this.withAppPackage(param.appPackage());
        }

        /**
         * Set the {@link #deviceUID} value.
         * @param deviceUID A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String deviceUID) {
            PARAM.deviceUID = deviceUID;
            return this;
        }

        /**
         * Set the {@link #deviceUID} value.
         * @param param A {@link DeviceUIDType} instance.
         * @return The current {@link Builder} instance.
         * @see #withDeviceUID(String)
         */
        @NotNull
        public Builder withDeviceUIDType(@NotNull DeviceUIDType param) {
            return this.withDeviceUID(param.deviceUID());
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
         * Set the {@link #retries} value.
         * @param param A {@link RetryType} instance.
         * @return The current {@link Builder} instance.
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
