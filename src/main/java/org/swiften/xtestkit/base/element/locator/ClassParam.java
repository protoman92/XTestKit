package org.swiften.xtestkit.base.element.locator;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.property.sub.OfClassType;
import org.swiften.xtestkitcomponents.common.RetryType;

/**
 * Created by haipham on 5/9/17.
 */
/**
 * Parameter object for {@link Engine#rxe_ofClass(OfClassType[])}.
 */
public class ClassParam implements OfClassType, RetryType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String clsName;

    ClassParam() {
        clsName = "";
    }

    //region StringType.
    @NotNull
    @Override
    public String value() {
        return clsName;
    }
    //endregion

    //region Builder.
    /**
     * Builder class for {@link IdParam}.
     */
    public static final class Builder {
        @NotNull final ClassParam PARAM;

        Builder() {
            PARAM = new ClassParam();
        }

        /**
         * Set the {@link #clsName} value.
         * @param clsName The clsName to be used to query elements.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withClass(@NotNull String clsName) {
            PARAM.clsName = clsName;
            return this;
        }

        @NotNull
        public ClassParam build() {
            return PARAM;
        }
    }
    //endregion
}