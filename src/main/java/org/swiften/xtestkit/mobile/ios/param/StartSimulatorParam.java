package org.swiften.xtestkit.mobile.ios.param;

/**
 * Created by haipham on 4/8/17.
 */

import org.swiften.xtestkit.base.type.RetryType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.mobile.android.type.DeviceUIDType;
import org.swiften.xtestkit.mobile.ios.XCRunHandler;

/**
 * Parameter object for {@link XCRunHandler#rxStartSimulator(StartSimulatorParam)}.
 */
public class StartSimulatorParam implements RetryType, DeviceUIDType {
    @NotNull public static StartSimulatorParam DEFAULT;

    static {
        DEFAULT = new StartSimulatorParam();
    }

    /**
     * Return a {@link Builder} instance.
     * @return A {@link Builder} instance.
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

    //region RetryType.
    @Override
    public int retries() {
        return retries;
    }
    //endregion

    //region DeviceUIDType.
    @NotNull
    public String deviceUID() {
        return deviceUID;
    }
    //endregion

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
         * @param name A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDeviceUID(@NotNull String name) {
            PARAM.deviceUID = name;
            return this;
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
         * Set {@link #retries} value.
         * @param param A {@link RetryType} instance.
         * @return A {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryType(@NotNull RetryType param) {
            return this.withRetries(param.retries());
        }

        @NotNull
        public StartSimulatorParam build() {
            return PARAM;
        }
    }
    //endregion
}

