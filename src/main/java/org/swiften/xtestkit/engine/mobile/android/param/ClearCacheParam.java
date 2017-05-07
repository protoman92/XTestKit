package org.swiften.xtestkit.engine.mobile.android.param;

import org.swiften.xtestkit.engine.base.RetryProtocol;
import org.swiften.xtestkit.engine.base.AppPackageProtocol;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.mobile.android.DeviceUIDProtocol;

/**
 * Created by haipham on 4/10/17.
 */
public class ClearCacheParam implements
    AppPackageProtocol,
    DeviceUIDProtocol,
    RetryProtocol {
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

    //region AppPackageProtocol
    @NotNull
    @Override
    public String appPackage() {
        return appPackage;
    }
    //endregion

    //region DeviceUIDProtocol
    @NotNull
    @Override
    public String deviceUID() {
        return deviceUID;
    }
    //endregion

    //region RetryProtocol
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
         * @param param A {@link AppPackageProtocol} instance.
         * @return The current {@link Builder} instance.
         * @see #withAppPackage(String)
         */
        @NotNull
        public Builder withAppPackageProtocol(@NotNull AppPackageProtocol param) {
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
         * @param param A {@link DeviceUIDProtocol} instance.
         * @return The current {@link Builder} instance.
         * @see #withDeviceUID(String)
         */
        @NotNull
        public Builder withDeviceUIDProtocol(@NotNull DeviceUIDProtocol param) {
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
         * @param param A {@link RetryProtocol} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryProtocol(@NotNull RetryProtocol param) {
            return this.withRetries(param.retries());
        }

        @NotNull
        public ClearCacheParam build() {
            return PARAM;
        }
    }
}
