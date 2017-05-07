package org.swiften.xtestkit.engine.base.xpath;

import org.swiften.xtestkit.engine.base.PlatformType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Use this utility class to easily compose {@link XPath} queries in order
 * to write cross-platform test.
 */
public class XPath {
    @NotNull
    public static XPath EMPTY = new XPath();

    @NotNull
    public static Builder builder(@NotNull PlatformType platform) {
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
        @NotNull private final PlatformType PLATFORM;
        @NotNull private final String NO_ATTR_NAME_ERROR;

        Builder(@NotNull PlatformType platform) {
            PLATFORM = platform;
            XPATH = new XPath();
            NO_ATTR_NAME_ERROR = "Must specify attribute name";
        }

        /**
         * With an {@link Attribute} instance, construct an attribute
         * {@link String} using the specified {@link Attribute.Mode}.
         * @param attribute An {@link Attribute} instance.
         * @param FORMAT A {@link String} value. This is the attribute format
         *               that will be passed to
         *               {@link String#format(String, Object...)}, along with
         *               the attribute name and a specified value.
         * @param VALUE A {@link String} value. This will be passed to
         *              {@link String#format(String, Object...)} along with
         *              the attribute name.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder appendAttribute(@NotNull Attribute attribute,
                                       @NotNull final String FORMAT,
                                       @NotNull final String VALUE) {
            List<String> attributes = attribute
                .attributes()
                .stream()
                .map(a -> String.format(FORMAT, a, VALUE))
                .collect(Collectors.toList());

            if (attributes.isEmpty()) {
                throw new RuntimeException(NO_ATTR_NAME_ERROR);
            }

            Attribute.Mode mode = attribute.mode();
            String joiner = String.format(" %s ", mode.joiner());
            String append = String.join(joiner, attributes);
            XPATH.appendAttribute(append);
            return this;
        }

        /**
         * Appends a @text attribute.
         * @param text The text to be appended.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder hasText(@NotNull String text) {
            Attribute attribute = PLATFORM.textAttribute();
            String format = "@%1$s='%2$s'";
            return appendAttribute(attribute, format, text);
        }

        /**
         * Appends a contains(@text) attribute.
         * @param text The text to be appended.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder containsText(@NotNull String text) {
            Attribute attribute = PLATFORM.textAttribute();
            String format = "contains(@%1$s, '%2$s')";
            return appendAttribute(attribute, format, text);
        }

        /**
         * Appends a @hint attribute. There are, however, platform implications
         * since on iOS this may be called a placeholder.
         * @param hint The hint to be appended.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder hasHint(@NotNull String hint) {
            Attribute attribute = PLATFORM.hintAttribute();
            String format = "@%1$s='%2$s'";
            return appendAttribute(attribute, format, hint);
        }

        /**
         * Appends a contains(@hint) attribute. There are, however, platform
         * implications since on iOS this may be called a placeholder.
         * @param hint The hint to be appended.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder containsHint(@NotNull String hint) {
            Attribute attribute = PLATFORM.hintAttribute();
            String format = "contains(@%1$s, '%2$s')";
            return appendAttribute(attribute, format, hint);
        }

        /**
         * Appends an @enabled attribute.
         * @param enabled A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder isEnabled(boolean enabled) {
            Attribute attribute = PLATFORM.enabledAttribute();
            String format = "@%1$s='%2$b'";
            return appendAttribute(attribute, format, String.valueOf(enabled));
        }

        /**
         * Appends a @clickable attribute.
         * @param clickable A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder isClickable(boolean clickable) {
            Attribute attribute = PLATFORM.clickableAttribute();
            String format = "@%1$s='%2$b'";
            return appendAttribute(attribute, format, String.valueOf(clickable));
        }

        /**
         * Appends a @editable attribute.
         * @param editable A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder isEditable(boolean editable) {
            Attribute attribute = PLATFORM.editableAttribute();
            String format = "@%1$s='%2$b'";
            return appendAttribute(attribute, format, String.valueOf(editable));
        }

        @NotNull
        public XPath build() {
            return XPATH;
        }
    }
}
