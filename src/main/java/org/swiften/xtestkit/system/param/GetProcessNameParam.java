package org.swiften.xtestkit.system.param;

/**
 * Created by haipham on 4/10/17.
 */

import org.swiften.xtestkit.base.type.RetryType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.system.NetworkHandler;
import org.swiften.xtestkit.system.type.PIDIdentifiableType;

/**
 * Parameter object for
 * {@link NetworkHandler#rxGetProcessName(PIDIdentifiableType)}
 */
public class GetProcessNameParam implements PIDIdentifiableType, RetryType {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String pid;

    private int retries;

    GetProcessNameParam() {
        pid = "";
    }

    //region PIDIdentifiableType
    /**
     * Return {@link #pid}.
     * @return A {@link String} value.
     */
    @NotNull
    @Override
    public String pid() {
        return pid;
    }
    //endregion

    //region RetryType.
    /**
     * Return {@link #retries}.
     * @return An {@link Integer} value.
     */
    @Override
    public int retries() {
        return retries;
    }
    //endregion

    //region Builder.
    /**
     * Builder class for {@link GetProcessNameParam}.
     */
    public static final class Builder {
        @NotNull private GetProcessNameParam PARAM;

        Builder() {
            PARAM = new GetProcessNameParam();
        }

        /**
         * Set the {@link #pid} value.
         * @param pid A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPID(@NotNull String pid) {
            PARAM.pid = pid;
            return this;
        }

        /**
         * Set the {@link #pid} value.
         * @param param A {@link PIDIdentifiableType} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPIDProtocol(@NotNull PIDIdentifiableType param) {
            return this.withPID(param.pid());
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
        public GetProcessNameParam build() {
            return PARAM;
        }
    }
    //endregion
}
