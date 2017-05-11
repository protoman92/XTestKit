package org.swiften.xtestkit.base.element.locator.general.xpath;

import io.reactivex.annotations.NonNull;
import org.swiften.xtestkit.base.type.PlatformType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.property.type.base.AttributeType;
import org.swiften.xtestkit.base.element.property.type.base.StringType;
import org.swiften.xtestkit.base.element.property.type.sub.*;

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
    @NotNull public static XPath EMPTY = new XPath();

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

    //region Builder
    /**
     * Builder class for {@link XPath}.
     */
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
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder appendAttribute(@NotNull Attribute attribute,
                                       @NotNull final String FORMAT) {
            List<String> attributes = attribute
                .attributes().stream()
                .map(a -> String.format(FORMAT, a))
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
         * Same as above, but get a format {@link String} from a
         * {@link Formattable} instance.
         * @param attribute An {@link Attribute} instance.
         * @param formattable A {@link Formattable} instance.
         * @return The current {@link Builder} instance.
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder appendAttribute(@NotNull Attribute attribute,
                                       @NotNull Formattable<?> formattable) {
            return appendAttribute(attribute, formattable.format());
        }

        /**
         * Append a contains(@class) attribute.
         * @param ofClass A {@link OfClass} instance.
         * @return The current {@link Builder} instance.
         * @see OfClass#format()
         */
        @NotNull
        public Builder ofClass(@NotNull OfClass ofClass) {
            Attribute attribute = PLATFORM.classAttribute();
            String format = ofClass.format();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link OfClass}.
         * @param STRING_TYPE A {@link StringType} instance.
         * @return The current {@link Builder} instance.
         * @see StringType#value()
         * @see StringType#ignoreCase()
         * @see #ofClass(OfClass)
         */
        @NotNull
        public Builder ofClass(@NotNull final StringType STRING_TYPE) {
            return ofClass(new OfClass() {
                @NotNull
                @Override
                public String value() {
                    return STRING_TYPE.value();
                }

                @Override
                public boolean ignoreCase() {
                    return STRING_TYPE.ignoreCase();
                }
            });
        }

        /**
         * Same as above, but uses a default {@link OfClass}.
         * @param CLS A {@link String} value.
         * @return The current {@link Builder} instance.
         * @see #ofClass(OfClass)
         */
        @NotNull
        public Builder ofClass(@NotNull final String CLS) {
            return ofClass(() -> CLS);
        }

        /**
         * Append a contains(@id) attribute.
         * @param containsID A {@link ContainsID} instance.
         * @return The current {@link Builder} instance.
         * @see ContainsID#format()
         */
        @NotNull
        public Builder containsID(@NotNull ContainsID containsID) {
            Attribute attribute = PLATFORM.idAttribute();
            String format = containsID.format();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link ContainsID} instance.
         * @param STRING_TYPE A {@link StringType} instance.
         * @return The current {@link Builder} instance.
         * @see StringType#value()
         * @see StringType#ignoreCase()
         * @see #containsID(ContainsID)
         */
        @NotNull
        public Builder containsID(@NotNull final StringType STRING_TYPE) {
            return containsID(new ContainsID() {
                @NotNull
                @Override
                public String value() {
                    return STRING_TYPE.value();
                }

                @Override
                public boolean ignoreCase() {
                    return STRING_TYPE.ignoreCase();
                }
            });
        }

        /**
         * Same as above, but uses a default {@link ContainsID} instance.
         * @param ID A {@link String} value.
         * @return The current {@link Builder} instance.
         * @see #containsID(ContainsID)
         */
        @NotNull
        public Builder containsID(@NotNull final String ID) {
            return containsID(() -> ID);
        }

        /**
         * Appends a @text attribute.
         * @param hasText A {@link HasText} instance.
         * @return The current {@link Builder} instance.
         * @see HasText#format()
         */
        @NotNull
        public Builder hasText(@NotNull HasText hasText) {
            Attribute attribute = PLATFORM.textAttribute();
            String format = hasText.format();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses an anonymously-created {@link HasText},
         * based on properties from a {@link StringType} instance.
         * @param STRING_TYPE A {@link StringType} instance.
         * @return The current {@link Builder} instance.
         * @see StringType#value()
         * @see StringType#ignoreCase()
         * @see #hasText(HasText)
         */
        @NotNull
        public Builder hasText(@NotNull final StringType STRING_TYPE) {
            return hasText(new HasText() {
                @NotNull
                @Override
                public String value() {
                    return STRING_TYPE.value();
                }

                @Override
                public boolean ignoreCase() {
                    return STRING_TYPE.ignoreCase();
                }
            });
        }

        /**
         * Same as above, but uses a {@link HasText} instance.
         * @param TEXT The text to be appended.
         * @return The current {@link Builder} instance.
         * @see #hasText(HasText)
         */
        @NotNull
        public Builder hasText(@NotNull final String TEXT) {
            return hasText(() -> TEXT);
        }

        /**
         * Appends a contains(@text) attribute.
         * @param containsText A {@link ContainsText} instance.
         * @return The current {@link Builder} instance.
         * @see ContainsText#format()
         */
        @NotNull
        public Builder containsText(@NotNull ContainsText containsText) {
            Attribute attribute = PLATFORM.textAttribute();
            String format = containsText.format();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses an anonymously-created {@link HasText},
         * based on properties from a {@link StringType} instance.
         * @param STRING_TYPE A {@link StringType} instance.
         * @return The current {@link Builder} instance.
         * @see StringType#value()
         * @see StringType#ignoreCase()
         * @see #containsText(ContainsText)
         */
        @NotNull
        public Builder containsText(@NotNull final StringType STRING_TYPE) {
            return containsText(new ContainsText() {
                @NotNull
                @Override
                public String value() {
                    return STRING_TYPE.value();
                }

                @Override
                public boolean ignoreCase() {
                    return STRING_TYPE.ignoreCase();
                }
            });
        }

        /**
         * Same as above, but uses a default {@link ContainsText} instance.
         * @param TEXT The text to be appended.
         * @return The current {@link Builder} instance.
         * @see #containsText(ContainsText)
         */
        @NotNull
        public Builder containsText(@NotNull final String TEXT) {
            return containsText(() -> TEXT);
        }

        /**
         * Appends a @hint attribute. There are, however, platform implications
         * since on iOS this may be called a placeholder.
         * @param hasHint A {@link HasHint} instance.
         * @return The current {@link Builder} instance.
         * @see HasHint#format()
         */
        @NotNull
        public Builder hasHint(@NotNull HasHint hasHint) {
            Attribute attribute = PLATFORM.hintAttribute();
            String format = hasHint.format();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses an anonymously-created {@link HasText},
         * based on properties from a {@link StringType} instance.
         * @param STRING_TYPE A {@link StringType} instance.
         * @return The current {@link Builder} instance.
         * @see StringType#value()
         * @see StringType#ignoreCase()
         * @see #hasHint(HasHint)
         */
        @NotNull
        public Builder hasHint(@NotNull final StringType STRING_TYPE) {
            return hasHint(new HasHint() {
                @NotNull
                @Override
                public String value() {
                    return STRING_TYPE.value();
                }

                @Override
                public boolean ignoreCase() {
                    return STRING_TYPE.ignoreCase();
                }
            });
        }

        /**
         * Same as above, but uses a default {@link HasHint} instance.
         * @param HINT The hint to be appended.
         * @return The current {@link Builder} instance.
         * @see #hasHint(HasHint)
         */
        @NotNull
        public Builder hasHint(@NotNull final String HINT) {
            return hasHint(() -> HINT);
        }

        /**
         * Appends a contains(@hint) attribute. There are, however, platform
         * implications since on iOS this may be called a placeholder.
         * @param containsHint A {@link ContainsHint} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder containsHint(@NotNull ContainsHint containsHint) {
            Attribute attribute = PLATFORM.hintAttribute();
            String format = containsHint.format();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses an anonymously-created {@link HasText},
         * based on properties from a {@link StringType} instance.
         * @param STRING_TYPE A {@link StringType} instance.
         * @return The current {@link Builder} instance.
         * @see StringType#value()
         * @see StringType#ignoreCase()
         */
        @NotNull
        public Builder containsHint(@NotNull final StringType STRING_TYPE) {
            return containsHint(new ContainsHint() {
                @NotNull
                @Override
                public String value() {
                    return STRING_TYPE.value();
                }

                @Override
                public boolean ignoreCase() {
                    return STRING_TYPE.ignoreCase();
                }
            });
        }

        /**
         * Same as above, but uses a default {@link ContainsHint} instance.
         * @param HINT The hint to be searched.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder containsHint(@NotNull final String HINT) {
            return containsHint(() -> HINT);
        }

        /**
         * Appends an @enabled attribute.
         * @param enabled A {@link Enabled} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder isEnabled(@NotNull Enabled enabled) {
            Attribute attribute = PLATFORM.enabledAttribute();
            String format = enabled.format();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link Enabled} instance.
         * @param ENABLED A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder isEnabled(final boolean ENABLED) {
            return isEnabled(() -> ENABLED);
        }

        /**
         * Appends a @clickable attribute.
         * @param clickable A {@link Clickable} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder isClickable(@NonNull Clickable clickable) {
            Attribute attribute = PLATFORM.clickableAttribute();
            String format = clickable.format();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link Clickable} instance.
         * @param CLICKABLE A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder isClickable(final boolean CLICKABLE) {
            return isClickable(() -> CLICKABLE);
        }

        /**
         * Appends a @editable attribute.
         * @param editable A {@link Editable} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder isEditable(@NonNull Editable editable) {
            Attribute attribute = PLATFORM.editableAttribute();
            String format = editable.format();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link Editable}.
         * @param EDITABLE A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder isEditable(final boolean EDITABLE) {
            return isEditable(() -> EDITABLE);
        }

        @NotNull
        public XPath build() {
            return XPATH;
        }
    }
    //endregion

    //region Locator Types
    /**
     * Classes that implement this interface must provide an XPath format
     * that can be used to construct attributes.
     */
    @FunctionalInterface
    public interface Formattable<T> extends AttributeType<T> {
        /**
         * Get the format {@link String} with which we construct an XPath
         * query.
         * @return A {@link String} value.
         * @see #value()
         */
        @NotNull
        default String format() {
            T raw = value();
            String value = String.valueOf(raw);
            return String.format("@%1$s='%2$s'", "%1$s", value);
        }
    }

    /**
     * This is a special case - we can use a translate operation to perform
     * case-insensitive contains-text locator operations.
     * We need to convert each character in the text we are search for into
     * lowercase. This way, it does not matter where the text is capitalized;
     * it will be standardized and subsequently can be searched.
     */
    @FunctionalInterface
    public interface ContainsString extends StringType, Formattable<String> {
        @NotNull
        @Override
        default String format() {
            String value = value();

            if (ignoreCase()) {
                return String.format(
                    "contains(translate(@%1$s, '%2$s', '%3$s'), '%3$s')",
                    "%1$s",
                    value.toUpperCase(),
                    value.toLowerCase());
            } else {
                return String.format("contains(@%1$s, '%2$s')", "%1$s", value);
            }
        }
    }

    @FunctionalInterface
    public interface Clickable extends ClickableType, Formattable<Boolean> {}

    @FunctionalInterface
    public interface ContainsHint extends ContainsHintType, ContainsString {}

    @FunctionalInterface
    public interface ContainsID extends ContainsIDType, ContainsString {}

    @FunctionalInterface
    public interface ContainsText extends ContainsTextType, ContainsString {}

    @FunctionalInterface
    public interface Editable extends EditableType, Formattable<Boolean> {}

    @FunctionalInterface
    public interface Enabled extends EnabledType, Formattable<Boolean> {}

    @FunctionalInterface
    public interface HasHint extends HasHintType, Formattable<String> {}

    @FunctionalInterface
    public interface HasText extends HasTextType, Formattable<String> {}

    @FunctionalInterface
    public interface OfClass extends OfClassType, ContainsString {}
    //endregion
}
