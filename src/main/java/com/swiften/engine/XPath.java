package com.swiften.engine;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Use this utility class to easily compose {@link XPath} queries in order
 * to write cross-platform tests.
 */
public class XPath {
    @NotNull
    public static Builder newBuilder(@NotNull Platform platform) {
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
        attribute = String.format("%s[%s]", attribute, attr);
    }

    public static final class Builder {
        @NotNull private final XPath XPATH;
        @NotNull private final Platform PLATFORM;

        Builder(@NotNull Platform platform) {
            PLATFORM = platform;
            XPATH = new XPath();
        }

        /**
         * Appends a @text attribute.
         * @param text The text to be appended.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder hasText(@NotNull String text) {
            String attr = String.format("@text='%s", text);
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
            String attr = String.format("contains(@text, '%s')", text);
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
            String attr = String.format("@enabled='%b'", enabled);
            XPATH.appendAttribute(attr);
            return this;
        }

        @NotNull
        public XPath build() {
            return XPATH;
        }
    }
}
