package org.swiften.xtestkit.engine.mobile.android.param;

import org.swiften.xtestkit.engine.base.AppPackageType;
import org.swiften.xtestkit.engine.base.RetriableType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.mobile.android.DeviceUIDType;

/**
 * Created by haipham on 4/10/17.
 */
public class ClearCacheParam implements
    AppPackageType,
    DeviceUIDType,
    RetriableType {
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

    //region RetriableType
    @Override
    public int retries() {
        return retries;
    }
    //endregion

    public static final class Builder {
        @NotNull private final ClearCacheParam PARAM;

        Builder() {
            PARAM = new ClearCacheParam();
        }

        /**
         * Set the {@link #PARAM#appPackage} value.
         * @param appPackage A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withAppPackage(@NotNull String appPackage) {
            PARAM.appPackage = appPackage;
            return this;
        }

        /**
         * Set the {@link #PARAM#appPackage} value.
         * @param param A {@link AppPackageType} instance.
         * @return The current {@link Builder} instance.
         * @see #withAppPackage(String)
         */
        @NotNull
        public Builder withAppPackageProtocol(@NotNull AppPackageType param) {
            return this.withAppPackage(param.appPackage());
        }

        /**
         * Set the {@link #PARAM#deviceUID} value.
         * @param deviceUID A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String deviceUID) {
            PARAM.deviceUID = deviceUID;
            return this;
        }

        /**
         * Set the {@link #PARAM#deviceUID} value.
         * @param param A {@link DeviceUIDType} instance.
         * @return The current {@link Builder} instance.
         * @see #withDeviceUID(String)
         */
        @NotNull
        public Builder withDeviceUIDProtocol(@NotNull DeviceUIDType param) {
            return this.withDeviceUID(param.deviceUID());
        }

        /**
         * Set the {@link #PARAM#retries} value.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set the {@link #PARAM#retries} value.
         * @param param A {@link RetriableType} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryProtocol(@NotNull RetriableType param) {
            return this.withRetries(param.retries());
        }

        @NotNull
        public ClearCacheParam build() {
            return PARAM;
        }
    }
}
