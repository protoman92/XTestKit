package org.swiften.xtestkit.base.element.tap;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.javautilities.util.Constants;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * Parameter object for
 * {@link TapType#tap(TapParamType)}
 */
@SuppressWarnings("WeakerAccess")
public class TapParam implements TapParamType, RetryProviderType {
    /**
     * Get a new {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    int x, y, retries;

    TapParam() {
        retries = Constants.DEFAULT_RETRIES;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see TapParamType#x()
     * @see #x
     */
    @Override
    public int x() {
        return x;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see TapParamType#y()
     * @see #y
     */
    @Override
    public int y() {
        return y;
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

    //region Builder
    /**
     * Builder for {@link TapParam}.
     */
    public static final class Builder {
        @NotNull private final TapParam PARAM;

        Builder() {
            PARAM = new TapParam();
        }

        /**
         * Set the {@link #x} value.
         * @param x {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withX(int x) {
            PARAM.x = x;
            return this;
        }

        /**
         * Set the {@link #y} value.
         * @param y {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withY(int y) {
            PARAM.y = y;
            return this;
        }

        /**
         * Set the {@link #x} and {@link #y} values.
         * @param type {@link RetryProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withX(int)
         * @see #withY(int)
         */
        @NotNull
        public Builder withTapParam(@NotNull TapParamType type) {
            return withX(type.x()).withY(type.y());
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
         * @param type {@link RetryProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withRetries(int)
         */
        @NotNull
        public Builder withRetryProvider(@NotNull RetryProviderType type) {
            return withRetries(type.retries());
        }

        @NotNull
        public TapParam build() {
            return PARAM;
        }
    }
    //endregion
}
