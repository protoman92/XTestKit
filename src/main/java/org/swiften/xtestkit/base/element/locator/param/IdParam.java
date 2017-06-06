package org.swiften.xtestkit.base.element.locator.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.swiften.xtestkitcomponents.property.sub.ContainsIDType;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * Parameter object for {@link Engine#rxe_containsID(ContainsIDType[])}.
 */
public class IdParam implements ContainsIDType, RetryType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String id;

    IdParam() {
        id = "";
    }

    //region StringType.
    @NotNull
    @Override
    public String value() {
        return id;
    }
    //endregion

    //region Builder.
    /**
     * Builder class for {@link IdParam}.
     */
    public static final class Builder {
        @NotNull final IdParam PARAM;

        Builder() {
            PARAM = new IdParam();
        }

        /**
         * Set the {@link #id} value.
         * @param id The id to be used to query elements.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withId(@NotNull String id) {
            PARAM.id = id;
            return this;
        }

        @NotNull
        public IdParam build() {
            return PARAM;
        }
    }
    //endregion
}
