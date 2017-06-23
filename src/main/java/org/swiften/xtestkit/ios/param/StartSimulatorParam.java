package org.swiften.xtestkit.ios.param;

/**
 * Created by haipham on 4/8/17.
 */

import org.swiften.javautilities.protocol.RetryProviderType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.type.DeviceUIDProviderType;
import org.swiften.xtestkit.ios.XCRunHandler;

/**
 * Parameter object for {@link XCRunHandler#rxa_startSimulator(StartSimulatorParam)}.
 */
public class StartSimulatorParam implements RetryProviderType, DeviceUIDProviderType {
    @NotNull public static StartSimulatorParam DEFAULT;

    static {
        DEFAULT = new StartSimulatorParam();
    }

    /**
     * Return {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static StartSimulatorParam.Builder builder() {
        return new StartSimulatorParam.Builder();
    }

    @NotNull private String deviceUID;

    private int retries;

    StartSimulatorParam() {
        deviceUID = "";
    }

    @Override
    public int retries() {
        return retries;
    }

    @NotNull
    @Override
    public String deviceUID() {
        return deviceUID;
    }

    //region Builder.
    /**
     * Builder class for {@link StartSimulatorParam}.
     */
    public static final class Builder {
        @NotNull final StartSimulatorParam PARAM;

        Builder() {
            PARAM = new StartSimulatorParam();
        }

        /**
         * Set the {@link #deviceUID} value.
         * @param name {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String name) {
            PARAM.deviceUID = name;
            return this;
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
         * Set {@link #retries} value.
         * @param param {@link RetryProviderType} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryProvider(@NotNull RetryProviderType param) {
            return this.withRetries(param.retries());
        }

        @NotNull
        public StartSimulatorParam build() {
            return PARAM;
        }
    }
    //endregion
}

