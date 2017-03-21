package com.swiften.engine.base;

import com.swiften.engine.base.protocol.PlatformProtocol;
import com.swiften.engine.mobile.Platform;
import org.jetbrains.annotations.NotNull;
import sun.security.krb5.internal.PAData;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Use this utility class to easily compose {@link XPath} queries in order
 * to write cross-platform tests.
 */
public class XPath {
    @NotNull
    public static Builder newBuilder(@NotNull PlatformProtocol platform) {
        return new Builder(platform);
    }

    @NotNull private String attribute;

    XPath() {
        attribute = "";
    }

    @NotNull
    public String getAttribute() {
        return attribute;
    }

    void appendAttribute(@NotNull String attr) {
        attribute = String.format("%1$s[%2$s]", attribute, attr);
    }

    public static class Builder {
        @NotNull private final XPath XPATH;
        @NotNull private final PlatformProtocol PLATFORM;
        @NotNull private final String NO_ATTR_NAME_ERROR;

        Builder(@NotNull PlatformProtocol platform) {
            PLATFORM = platform;
            XPATH = new XPath();
            NO_ATTR_NAME_ERROR = "Must specify attribute name";
        }

        /**
         * Appends a @text attribute.
         * @param text The text to be appended.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder hasText(@NotNull String text) {
            String name = PLATFORM.textAttribute();

            if (name.isEmpty()) {
                throw new RuntimeException(NO_ATTR_NAME_ERROR);
            }

            String attr = String.format("@%1$s='%2$s'", name, text);
            XPATH.appendAttribute(attr);
            return this;
        }

        /**
         * Appends a contains(@text) attribute.
         * @param text The text to be appended.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder containsText(@NotNull String text) {
            String name = PLATFORM.textAttribute();

            if (name.isEmpty()) {
                throw new RuntimeException(NO_ATTR_NAME_ERROR);
            }

            String attr = String.format("contains(@%1$s, '%2$s')", name, text);
            XPATH.appendAttribute(attr);
            return this;
        }

        /**
         * Appends a @hint attribute. There are, however, platform implications
         * since on iOS this may be called a placeholder.
         * @param hint The hint to be appended.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder hasHint(@NotNull String hint) {
            String name = PLATFORM.hintAttribute();

            if (name.isEmpty()) {
                throw new RuntimeException(NO_ATTR_NAME_ERROR);
            }

            String attr = String.format("@%1$s='%2$s'", name, hint);
            XPATH.appendAttribute(attr);
            return this;
        }

        /**
         * Appends a contains(@hint) attribute. There are, however, platform
         * implications since on iOS this may be called a placeholder.
         * @param hint The hint to be appended.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder containsHint(@NotNull String hint) {
            String name = PLATFORM.hintAttribute();

            if (name.isEmpty()) {
                throw new RuntimeException(NO_ATTR_NAME_ERROR);
            }

            String attr = String.format("contains(@%1$s, '%2$s')", name, hint);
            XPATH.appendAttribute(attr);
            return this;
        }

        /**
         * Appends an @enabled attribute.
         * @param enabled A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder isEnabled(boolean enabled) {
            String name = PLATFORM.enabledAttribute();

            if (name.isEmpty()) {
                throw new RuntimeException(NO_ATTR_NAME_ERROR);
            }

            String attr = String.format("@%1$s='%2$s'", name, enabled);
            XPATH.appendAttribute(attr);
            return this;
        }

        @NotNull
        public XPath build() {
            return XPATH;
        }
    }
}
