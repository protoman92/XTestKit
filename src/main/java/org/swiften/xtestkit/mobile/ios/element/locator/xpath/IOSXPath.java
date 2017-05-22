package org.swiften.xtestkit.mobile.ios.element.locator.xpath;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.general.xpath.Attribute;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.ios.element.property.type.ContainsNameType;

/**
 * Created by haipham on 5/23/17.
 */
public final class IOSXPath extends XPath {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for {@link IOSXPath}.
     */
    public static final class Builder extends XPath.Builder {
        Builder() {
            super(Platform.IOS);
        }

        /**
         * Append a @name attribute.
         * @param containsName {@link ContainsName} instance.
         * @return The current {@link Builder} instance.
         * @see Attribute#single(String)
         * @see #appendAttribute(Attribute, Formatible)
         */
        @NotNull
        public Builder containsName(@NotNull ContainsName containsName) {
            Attribute attribute = Attribute.single("name");
            appendAttribute(attribute, containsName);
            return this;
        }

        /**
         * Same as above, but uses a default {@link ContainsName}.
         * @param NAME {@link String} value.
         * @return The current {@link Builder} instance.
         * @see #containsName(ContainsName)
         */
        @NotNull
        public Builder containsName(@NotNull final String NAME) {
            return containsName(() -> NAME);
        }
    }

    @FunctionalInterface
    public interface ContainsName extends ContainsNameType, ContainsString {}
}
