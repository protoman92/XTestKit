package org.swiften.xtestkit.system.param;

/**
 * Created by haipham on 4/10/17.
 */

import org.swiften.xtestkit.engine.base.RetryProtocol;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.system.NetworkHandler;
import org.swiften.xtestkit.system.PIDProtocol;

/**
 * Parameter object for
 * {@link NetworkHandler#rxGetProcessName(PIDProtocol)}
 */
public class GetProcessNameParam implements PIDProtocol, RetryProtocol {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String pid;

    private int retries;

    GetProcessNameParam() {
        pid = "";
    }

    //region PIDProtocol
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

    /**
     * Return {@link #retries}.
     * @return An {@link Integer} value.
     */
    @Override
    public int retries() {
        return retries;
    }

    public static final class Builder {
        @NotNull private GetProcessNameParam PARAM;

        Builder() {
            PARAM = new GetProcessNameParam();
        }

        /**
         * Set the {@link #PARAM#pid} value.
         * @param pid A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPID(@NotNull String pid) {
            PARAM.pid = pid;
            return this;
        }

        /**
         * Set the {@link #PARAM#pid} value.
         * @param param A {@link PIDProtocol} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPIDProtocol(@NotNull PIDProtocol param) {
            return this.withPID(param.pid());
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
        public GetProcessNameParam build() {
            return PARAM;
        }
    }
}
