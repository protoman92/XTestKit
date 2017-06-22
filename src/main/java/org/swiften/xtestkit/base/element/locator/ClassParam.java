package org.swiften.xtestkit.base.element.locator;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.protocol.ClassNameType;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.property.sub.OfClassType;
import org.swiften.javautilities.protocol.RetryType;

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

    /**
     * Override this method to provide default implementation.
     * @return {@link String} value.
     * @see OfClassType#value()
     * @see #clsName
     */
    @NotNull
    @Override
    public String value() {
        return clsName;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RetryType#retries()
     * @see Constants#DEFAULT_RETRIES
     */
    @Override
    public int retries() {
        return Constants.DEFAULT_RETRIES;
    }

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
         * Set {@link #clsName}.
         * @param clsName The clsName to be used to query elements.
         * @return {@link Builder} instance.
         * @see #clsName
         */
        @NotNull
        public Builder withClass(@NotNull String clsName) {
            PARAM.clsName = clsName;
            return this;
        }

        /**
         * Set {@link #clsName}.
         * @param param {@link ClassNameType} instance.
         * @return {@link Builder} instance.
         * @see ClassNameType#className()
         * @see #withClass(ClassNameType)
         */
        @NotNull
        public Builder withClass(@NotNull ClassNameType param) {
            return withClass(param.className());
        }

        @NotNull
        public ClassParam build() {
            return PARAM;
        }
    }
    //endregion
}