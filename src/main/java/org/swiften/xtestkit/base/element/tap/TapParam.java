package org.swiften.xtestkit.base.element.tap;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.RetryType;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * Parameter object for
 * {@link BaseTapType#tap(TapType)}
 */
@SuppressWarnings("WeakerAccess")
public class TapParam implements TapType, RetryType {
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
        retries = RetryType.super.retries();
    }

    //region Getters
    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int retries() {
        return retries;
    }
    //endregion

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
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withX(int x) {
            PARAM.x = x;
            return this;
        }

        /**
         * Set the {@link #y} value.
         * @param y {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withY(int y) {
            PARAM.y = y;
            return this;
        }

        /**
         * Set the {@link #x} and {@link #y} values.
         * @param type {@link RetryType} instance.
         * @return The current {@link Builder} instance.
         * @see #withX(int)
         * @see #withY(int)
         */
        @NotNull
        public Builder withTapType(@NotNull TapType type) {
            return withX(type.x()).withY(type.y());
        }

        /**
         * Set the {@link #retries} value.
         * @param retries {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set the {@link #retries} value.
         * @param type {@link RetryType} instance.
         * @return The current {@link Builder} instance.
         * @see #withRetries(int)
         */
        @NotNull
        public Builder withRetryType(@NotNull RetryType type) {
            return withRetries(type.retries());
        }

        @NotNull
        public TapParam build() {
            return PARAM;
        }
    }
    //endregion
}
