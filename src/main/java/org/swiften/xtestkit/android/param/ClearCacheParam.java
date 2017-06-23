package org.swiften.xtestkit.android.param;

import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.xtestkit.base.type.AppPackageProviderType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.type.DeviceUIDProviderType;

/**
 * Created by haipham on 4/10/17.
 */
public class ClearCacheParam implements
    AppPackageProviderType,
    DeviceUIDProviderType,
    RetryProviderType {
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

    /**
     * Override this method to provide default implementation.
     * @return {@link String} value.
     * @see AppPackageProviderType#appPackage()
     * @see #appPackage
     */
    @NotNull
    @Override
    public String appPackage() {
        return appPackage;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link String} value.
     * @see DeviceUIDProviderType#deviceUID()
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
     * @see #retries
     */
    @Override
    public int retries() {
        return retries;
    }

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
         * @param param {@link AppPackageProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withAppPackage(String)
         */
        @NotNull
        public Builder withAppPackageProvider(@NotNull AppPackageProviderType param) {
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
         * @param param {@link DeviceUIDProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withDeviceUID(String)
         */
        @NotNull
        public Builder withDeviceUIDProvider(@NotNull DeviceUIDProviderType param) {
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
         * @param param {@link RetryProviderType} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryProvider(@NotNull RetryProviderType param) {
            return this.withRetries(param.retries());
        }

        @NotNull
        public ClearCacheParam build() {
            return PARAM;
        }
    }
    //endregion
}
