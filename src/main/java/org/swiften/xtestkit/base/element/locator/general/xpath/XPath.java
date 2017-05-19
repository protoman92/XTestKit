package org.swiften.xtestkit.base.element.locator.general.xpath;

import io.reactivex.annotations.NonNull;
import org.swiften.xtestkit.base.type.PlatformType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.property.type.base.AttributeType;
import org.swiften.xtestkit.base.element.property.type.base.StringType;
import org.swiften.xtestkit.base.element.property.type.sub.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
    public String attribute() {
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
         * {@link Formatible} instance.
         * @param attribute An {@link Attribute} instance.
         * @param formatible A {@link Formatible} instance.
         * @return The current {@link Builder} instance.
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder appendAttribute(@NotNull Attribute attribute,
                                       @NotNull Formatible<?> formatible) {
            return appendAttribute(attribute, formatible.stringFormat());
        }

        /**
         * Append a @index attribute.
         * @param atIndex An {@link AtIndex} instance.
         * @return The current {@link Builder} instance.
         * @see PlatformType#indexAttribute()
         * @see AtIndex#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder atIndex(@NotNull AtIndex atIndex) {
            Attribute attribute = PLATFORM.indexAttribute();
            String format = atIndex.stringFormat();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link AtIndex} instance.
         * @param INDEX An {@link Integer} value.
         * @return The current {@link Builder} instance.
         * @see #atIndex(AtIndex)
         */
        @NotNull
        public Builder atIndex(final int INDEX) {
            return atIndex(() -> INDEX);
        }

        /**
         * Append a @instance attribute.
         * @param ofInstance An {@link OfInstance} instance.
         * @return The current {@link Builder} instance.
         * @see PlatformType#instanceAttribute()
         * @see OfInstance#stringFormat()
         * @see #appendAttribute(String)
         */
        @NotNull
        public Builder ofInstance(@NotNull OfInstance ofInstance) {
            Attribute attribute = PLATFORM.instanceAttribute();
            String format = ofInstance.stringFormat();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link OfInstance} instance.
         * @param INSTANCE An {@link Integer} value.
         * @return The current {@link Builder} instance.
         * @see #ofInstance(int)
         */
        @NotNull
        public Builder ofInstance(final int INSTANCE) {
            return ofInstance(() -> INSTANCE);
        }

        /**
         * Append a contains(@class) attribute.
         * @param ofClass A {@link OfClass} instance.
         * @return The current {@link Builder} instance.
         * @see PlatformType#classAttribute()
         * @see OfClass#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder ofClass(@NotNull OfClass ofClass) {
            Attribute attribute = PLATFORM.classAttribute();
            String format = ofClass.stringFormat();
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
         * @see PlatformType#idAttribute()
         * @see ContainsID#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder containsID(@NotNull ContainsID containsID) {
            Attribute attribute = PLATFORM.idAttribute();
            String format = containsID.stringFormat();
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
         * @see PlatformType#textAttribute()
         * @see HasText#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder hasText(@NotNull HasText hasText) {
            Attribute attribute = PLATFORM.textAttribute();
            String format = hasText.stringFormat();
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
         * @see PlatformType#textAttribute()
         * @see ContainsText#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder containsText(@NotNull ContainsText containsText) {
            Attribute attribute = PLATFORM.textAttribute();
            String format = containsText.stringFormat();
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
         * @see PlatformType#hintAttribute()
         * @see HasHint#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder hasHint(@NotNull HasHint hasHint) {
            Attribute attribute = PLATFORM.hintAttribute();
            String format = hasHint.stringFormat();
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
         * @see PlatformType#hintAttribute()
         * @see ContainsHint#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder containsHint(@NotNull ContainsHint containsHint) {
            Attribute attribute = PLATFORM.hintAttribute();
            String format = containsHint.stringFormat();
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
         * @see PlatformType#enabledAttribute()
         * @see Enabled#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder isEnabled(@NotNull Enabled enabled) {
            Attribute attribute = PLATFORM.enabledAttribute();
            String format = enabled.stringFormat();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link Enabled} instance.
         * @param ENABLED A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         * @see #isEnabled(Enabled)
         */
        @NotNull
        public Builder isEnabled(final boolean ENABLED) {
            return isEnabled(() -> ENABLED);
        }

        /**
         * Appends a @clickable attribute.
         * @param clickable A {@link Clickable} instance.
         * @return The current {@link Builder} instance.
         * @see PlatformType#clickableAttribute()
         * @see Clickable#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder isClickable(@NonNull Clickable clickable) {
            Attribute attribute = PLATFORM.clickableAttribute();
            String format = clickable.stringFormat();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link Clickable} instance.
         * @param CLICKABLE A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         * @see #isClickable(Clickable)
         */
        @NotNull
        public Builder isClickable(final boolean CLICKABLE) {
            return isClickable(() -> CLICKABLE);
        }

        /**
         * Appends a @editable attribute.
         * @param editable A {@link Editable} instance.
         * @return The current {@link Builder} instance.
         * @see PlatformType#editableAttribute()
         * @see Editable#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder isEditable(@NonNull Editable editable) {
            Attribute attribute = PLATFORM.editableAttribute();
            String format = editable.stringFormat();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link Editable}.
         * @param EDITABLE A {@link Boolean} value.
         * @return The current {@link Builder} instance.
         * @see #isEditable(Editable)
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
     * Classes that implement this interface must provide an {@link XPath}
     * format that can be used to construct {@link Attribute}.
     */
    @FunctionalInterface
    public interface Formatible<T> extends AttributeType<T> {
        /**
         * Get the value to be formatted. Override this to provide custom
         * values.
         * @param value A {@link T} instance.
         * @return A {@link String} value.
         */
        @NotNull
        default String formatValue(@NotNull T value) {
            return String.format("'%s'", value);
        }

        /**
         * Get the format {@link String} with which we construct an XPath
         * query.
         * @return A {@link String} value.
         */
        @NotNull
        default String stringFormat() {
            String raw = formatValue(value());
            return String.format("@%1$s=%2$s", "%1$s", raw);
        }
    }

    /**
     * This interface provides methods to clean a {@link String} of double
     * and single quote marks. Note that this is applicable both to direct
     * comparison queries and @contain(@translate) - however, we must not
     * use concat() when there are no quotation marks.
     */
    public interface QuotationFree extends Formatible<String> {
        /**
         * Strip the {@link String} to be formatted of single and double
         * quotes by separating and concatenating.
         * @param value A {@link String} value.
         * @return A {@link String} value.
         * @see XPathQuoteMark#wrapInQuotation(String)
         */
        @NotNull
        @Override
        default String formatValue(@NotNull String value) {
            if (!value.isEmpty()) {
                String fQuote = "", lQuote = "";
                List<String> fParts = new LinkedList<>();

                /* We need to take case of cases whereby the quotation marks
                 * are the first or the last character, or both. */
                String fChar = String.valueOf(value.charAt(0));
                String lChar = String.valueOf(value.charAt(value.length() - 1));
                Optional<XPathQuoteMark> fqm = XPathQuoteMark.from(fChar);
                Optional<XPathQuoteMark> lqm = XPathQuoteMark.from(lChar);
                String fFormat = "%s";

                if (fqm.isPresent()) {
                    fQuote = fqm.get().wrappedInQuotation();
                    value = value.substring(1, value.length());
                    fFormat = String.format("%1$s,%2$s", fQuote, fFormat);
                }

                if (lqm.isPresent()) {
                    lQuote = lqm.get().wrappedInQuotation();
                    value = value.substring(0, value.length() - 1);
                    fFormat = String.format("%1$s,%2$s", fFormat, lQuote);
                }

                /* Sequentially split the String using ', and then split each
                 * sub-string using " */
                String[] fSplit = value.split("'");

                for (String fs : fSplit) {
                    List<String> lParts = new LinkedList<>();
                    String[] lSplit = fs.split("\"");

                    for (String ss : lSplit) {
                        lParts.add(String.format("'%s'", ss));
                    }

                    String lJoined = String.join(",'\"',", lParts);
                    fParts.add(lJoined);
                }

                String joined = String.join(",\"'\",", fParts);
                String formatted = String.format(fFormat, joined);

                /* Only use concat if there is more than one concatenated
                 * sub-string, or there is either a quotation mark at the
                 * start/end of the String */
                if (fParts.size() > 1 || !(fQuote + lQuote).isEmpty()) {
                    return String.format("concat(%s)", formatted);
                } else {
                    return formatted;
                }
            } else {
                return "";
            }
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
    public interface ContainsString extends StringType, QuotationFree {
        /**
         * Override this method to provide custom format that can add ignore
         * case capability.
         * @return A {@link String} value.
         * @see #value()
         */
        @NotNull
        @Override
        default String stringFormat() {
            String value = value();

            if (ignoreCase()) {
                return String.format(
                    "contains(translate(@%1$s, %2$s, %3$s), %3$s)",
                    "%1$s",
                    formatValue(value.toUpperCase()),
                    formatValue(value.toLowerCase())
                );
            } else {
                return String.format(
                    "contains(@%1$s, %2$s)", "%1$s",
                    formatValue(value)
                );
            }
        }
    }

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface AtIndex extends Formatible<Integer> {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface OfInstance extends Formatible<Integer> {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface Clickable extends ClickableType, Formatible<Boolean> {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface ContainsHint extends ContainsHintType, ContainsString {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface ContainsID extends ContainsIDType, ContainsString {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface ContainsText extends ContainsTextType, ContainsString {}

    @FunctionalInterface
    public interface Editable extends EditableType, Formatible<Boolean> {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface Enabled extends EnabledType, Formatible<Boolean> {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface HasHint extends HasHintType, QuotationFree {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface HasText extends HasTextType, QuotationFree {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface OfClass extends OfClassType, ContainsString {}
    //endregion
}
