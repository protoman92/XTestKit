package org.swiften.xtestkit.base.element.action.tap.param;

import org.apache.regexp.RE;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.password.type.TapType;
import org.swiften.xtestkit.base.type.RetryType;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * Parameter object for
 * {@link org.swiften.xtestkit.base.element.action.tap.type.BaseTapType#tap(TapType)}
 */
@SuppressWarnings("WeakerAccess")
public class TapParam implements TapType, RetryType {
    /**
     * Get a new {@link Builder} instance.
     * @return A {@link Builder} instance.
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
    public final class Builder {
        @NotNull private final TapParam PARAM;

        Builder() {
            PARAM = new TapParam();
        }

        /**
         * Set the {@link #x} value.
         * @param x An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withX(int x) {
            PARAM.x = x;
            return this;
        }

        /**
         * Set the {@link #y} value.
         * @param y An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withY(int y) {
            PARAM.y = y;
            return this;
        }

        /**
         * Set the {@link #x} and {@link #y} values.
         * @param type A {@link RetryType} instance.
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
         * @param type A {@link RetryType} instance.
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
