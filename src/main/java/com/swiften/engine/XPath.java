package com.swiften.engine;

import com.swiften.util.Log;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Use this utility class to easily compose {@link XPath} queries in order
 * to write cross-platform tests.
 */
public final class XPath {
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
        attribute = String.format("%1$s[%2$s]", attribute, attr);
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
            String attr = String.format("@text='%s'", text);
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
         * Appends a @hint attribute. There are, however, platform implications
         * since on iOS this may be called a placeholder.
         * @param hint The hint to be appended.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder hasHint(@NotNull String hint) {
            String attr = String.format("@hint='%s'", hint);
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
            String attr = String.format("contains(@hint, '%s')", hint);
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
