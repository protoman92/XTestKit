package org.swiften.xtestkit.engine.base.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.locator.element.type.sub.OfClassType;
import org.swiften.xtestkit.engine.base.type.RetryType;

/**
 * Created by haipham on 5/9/17.
 */
/**
 * Parameter object for
 * {@link org.swiften.xtestkit.engine.base.BaseEngine#rxElementsOfClass(ClassParam)}.
 */
public class ClassParam implements OfClassType, RetryType {
    /**
     * Get a {@link Builder} instance.
     * @return A {@link Builder} instance.
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
         * @return The current {@link Builder} instance.
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