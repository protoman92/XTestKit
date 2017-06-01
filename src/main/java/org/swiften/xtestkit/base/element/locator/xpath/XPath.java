package org.swiften.xtestkit.base.element.locator.xpath;

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.property.base.AttributeType;
import org.swiften.xtestkit.base.element.property.base.StringType;
import org.swiften.xtestkit.base.element.property.sub.*;
import org.swiften.xtestkit.base.type.PlatformType;

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

    @NotNull private String className;
    @NotNull private String attribute;

    protected XPath() {
        attribute = "";
        className = "*";
    }

    @NotNull
    public String toString() {
        return fullAttribute();
    }

    /**
     * Get {@link #className}.
     * @return {@link String} value.
     * @see #className
     */
    @NotNull
    public String className() {
        return className;
    }

    /**
     * Get {@link #attribute}.
     * @return {@link String} value.
     * @see #attribute
     */
    @NotNull
    public String attribute() {
        return attribute;
    }

    /**
     * Get {@link #attribute} prefixed by path slashes.
     * @return {@link String} value.
     * @see #attribute()
     * @see #className()
     */
    @NotNull
    public String fullAttribute() {
        return String.format("//%s%s", className(), attribute());
    }

    /**
     * Append an attribute to the end of the current {@link #attribute}.
     * @param attr {@link String} value.
     * @see #attribute
     */
    void appendAttribute(@NotNull String attr) {
        attribute = String.format("%s[%s]", attribute, attr);
    }

    /**
     * Append a child attribute to the end of the current {@link #attribute}.
     * @param attr {@link String} value.
     * @see #attribute
     */
    void appendChildAttribute(@NotNull String attr) {
        attribute = String.format("%s%s", attribute, attr);
    }

    /**
     * Append a joiner symbol to then end of the current {@link #attribute}.
     * @param joiner {@link String} value.
     * @see #attribute
     */
    void appendJoinerSymbol(@NotNull String joiner) {
        attribute = String.format("%s%s", attribute, joiner);
    }

    //region Builder
    /**
     * Builder class for {@link XPath}.
     */
    public static class Builder {
        @NotNull private final XPath XPATH;
        @NotNull private final PlatformType PLATFORM;
        @NotNull private final String NO_ATTR_NAME_ERROR;

        protected Builder(@NotNull PlatformType platform) {
            PLATFORM = platform;
            XPATH = new XPath();
            NO_ATTR_NAME_ERROR = "Must specify attribute name";
        }

        /**
         * Set the {@link #className} value to prefix the {@link XPath}
         * {@link #attribute}.
         * @param className {@link String} value.
         * @return The current {@link Builder} instance.
         * @see #className
         */
        @NotNull
        public Builder setClass(@NotNull String className) {
            XPATH.className = className;
            return this;
        }

        /**
         * Set the {@link #attribute} value. Use this with caution because
         * it will override all other elements.
         * @param xPath {@link XPath} instance.
         * @return The current {@link Builder} instance.
         * @see #attribute
         * @see XPath#attribute()
         */
        @NotNull
        public Builder withXPath(@NotNull XPath xPath) {
            XPATH.attribute = xPath.attribute();
            return this;
        }

        /**
         * Add a child {@link XPath} to the end of {@link #attribute}. Take
         * case to use this method at the end of the chain, or else the
         * element order will be messed up.
         * @param xPath {@link XPath} instance.
         * @return The current {@link Builder} instance.
         * @see #attribute
         * @see XPath#appendChildAttribute(String)
         * @see XPath#fullAttribute()
         */
        @NotNull
        public Builder addChildXPath(@NotNull XPath xPath) {
            XPATH.appendChildAttribute(xPath.fullAttribute());
            return this;
        }

        /**
         * With {@link Attribute} instance, construct an attribute
         * {@link String} using the specified {@link Attribute.Mode}.
         * @param attribute {@link Attribute} instance.
         * @param FORMAT {@link String} value. This is the attribute format
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
         * @param attribute {@link Attribute} instance.
         * @param formatible {@link Formatible} instance.
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
         * @param atIndex {@link AtIndex} instance.
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
         * @param INDEX {@link Integer} value.
         * @return The current {@link Builder} instance.
         * @see #atIndex(AtIndex)
         */
        @NotNull
        public Builder atIndex(final int INDEX) {
            return atIndex(() -> INDEX);
        }

        /**
         * Append a @instance attribute.
         * @param ofInstance {@link OfInstance} instance.
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
         * @param INSTANCE {@link Integer} value.
         * @return The current {@link Builder} instance.
         * @see #ofInstance(int)
         */
        @NotNull
        public Builder ofInstance(final int INSTANCE) {
            return ofInstance(() -> INSTANCE);
        }

        /**
         * Append a contains(@class) attribute.
         * @param ofClass {@link OfClass} instance.
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
         * @param STRING_TYPE {@link StringType} instance.
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
         * @param CLS {@link String} value.
         * @return The current {@link Builder} instance.
         * @see #ofClass(OfClass)
         */
        @NotNull
        public Builder ofClass(@NotNull final String CLS) {
            return ofClass(() -> CLS);
        }

        /**
         * Append a contains(@id) attribute.
         * @param containsID {@link ContainsID} instance.
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
         * @param STRING_TYPE {@link StringType} instance.
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
         * @param ID {@link String} value.
         * @return The current {@link Builder} instance.
         * @see #containsID(ContainsID)
         */
        @NotNull
        public Builder containsID(@NotNull final String ID) {
            return containsID(() -> ID);
        }

        /**
         * Appends a @text attribute.
         * @param hasText {@link HasText} instance.
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
         * based on properties from {@link StringType} instance.
         * @param STRING_TYPE {@link StringType} instance.
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
         * Same as above, but uses {@link HasText} instance.
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
         * @param containsText {@link ContainsText} instance.
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
         * based on properties from {@link StringType} instance.
         * @param STRING_TYPE {@link StringType} instance.
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
         * @param hasHint {@link HasHint} instance.
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
         * based on properties from {@link StringType} instance.
         * @param STRING_TYPE {@link StringType} instance.
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
         * @param containsHint {@link ContainsHint} instance.
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
         * based on properties from {@link StringType} instance.
         * @param STRING_TYPE {@link StringType} instance.
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
         * @param enabled {@link Enabled} instance.
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
         * @param ENABLED {@link Boolean} value.
         * @return The current {@link Builder} instance.
         * @see #isEnabled(Enabled)
         */
        @NotNull
        public Builder isEnabled(final boolean ENABLED) {
            return isEnabled(() -> ENABLED);
        }

        /**
         * Appends a @clickable attribute.
         * @param clickable {@link Clickable} instance.
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
         * @param CLICKABLE {@link Boolean} value.
         * @return The current {@link Builder} instance.
         * @see #isClickable(Clickable)
         */
        @NotNull
        public Builder isClickable(final boolean CLICKABLE) {
            return isClickable(() -> CLICKABLE);
        }

        /**
         * Appends a @editable attribute.
         * @param editable {@link Editable} instance.
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
         * @param EDITABLE {@link Boolean} value.
         * @return The current {@link Builder} instance.
         * @see #isEditable(Editable)
         */
        @NotNull
        public Builder isEditable(final boolean EDITABLE) {
            return isEditable(() -> EDITABLE);
        }

        /**
         * Append a @focused attribute.
         * @param focused {@link Focused} instance.
         * @return The current {@link Builder} instance.
         * @see PlatformType#focusedAttribute()
         * @see Focused#stringFormat()
         * @see #appendAttribute(Attribute, String)
         */
        @NotNull
        public Builder isFocused(@NotNull Focused focused) {
            Attribute attribute = PLATFORM.focusedAttribute();
            String format = focused.stringFormat();
            return appendAttribute(attribute, format);
        }

        /**
         * Same as above, but uses a default {@link Focused} instance.
         * @param FOCUSED {@link Boolean} value.
         * @return The current {@link Builder} instance.
         * @see #isFocused(Focused)
         */
        @NotNull
        public Builder isFocused(final boolean FOCUSED) {
            return isFocused(() -> FOCUSED);
        }

        /**
         * Set {@link XPath} index. Contrary to {@link #atIndex(AtIndex)},
         * instead of appending an @index attribute, this method directly
         * sets index with square brackets, e.g. TextView[0].
         * @param atIndex {@link AtIndex} instance.
         * @return The current {@link Builder} instance.
         * @see XPath#appendAttribute(String)
         * @see AtIndex#value()
         * @see #build()
         */
        @NotNull
        public Builder setIndex(@NotNull AtIndex atIndex) {
            XPATH.appendAttribute(String.valueOf(atIndex.value()));
            return this;
        }

        /**
         * Same as above, but uses a default {@link AtIndex} instance.
         * @param INDEX {@link Integer} value.
         * @return The current {@link Builder} instance.
         * @see #setIndex(AtIndex)
         */
        @NotNull
        public Builder setIndex(final int INDEX) {
            return setIndex(() -> INDEX);
        }

        /**
         * Append a joiner symbol to the end of {@link #attribute}.
         * @param mode {@link Attribute.Mode} instance.
         * @return The current {@link Builder} instance.
         * @see XPath#appendJoinerSymbol(String)
         */
        @NotNull
        public Builder appendJoinerSymbol(@NotNull Attribute.Mode mode) {
            XPATH.appendJoinerSymbol(mode.joinerSymbol());
            return this;
        }

        @NotNull
        public XPath build() {
            return XPATH;
        }
    }
    //endregion

    //region Locator Types
    /**
     * Classes that implement this interface must provide {@link XPath}
     * format that can be used to construct {@link Attribute}.
     */
    @FunctionalInterface
    public interface Formatible<T> extends AttributeType<T> {
        /**
         * Get the value to be formatted. Override this to provide custom
         * values.
         * @param value {@link T} instance.
         * @return {@link String} value.
         */
        @NotNull
        default String formatValue(@NotNull T value) {
            return String.format("'%s'", value);
        }

        /**
         * Get the format {@link String} with which we construct an XPath
         * query.
         * @return {@link String} value.
         */
        @NotNull
        default String stringFormat() {
            String raw = formatValue(value());
            return String.format("@%1$s=%2$s", "%1$s", raw);
        }
    }

    /**
     * This interface provides methods to cle{@link String} of double
     * and single quote marks. Note that this is applicable both to direct
     * comparison queries and @contain(@translate) - however, we must not
     * use concat() when there are no quotation marks.
     */
    public interface QuotationFree extends Formatible<String> {
        /**
         * Strip the {@link String} to be formatted of single and double
         * quotes by separating and concatenating.
         * @param value {@link String} value.
         * @return {@link String} value.
         * @see QuoteMark#wrapInQuotation(String)
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
                Optional<QuoteMark> fqm = QuoteMark.from(fChar);
                Optional<QuoteMark> lqm = QuoteMark.from(lChar);
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
         * @return {@link String} value.
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
    @SuppressWarnings("WeakerAccess")
    public interface Editable extends EditableType, Formatible<Boolean> {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface Enabled extends EnabledType, Formatible<Boolean> {}

    @FunctionalInterface
    @SuppressWarnings("WeakerAccess")
    public interface Focused extends FocusedType, Formatible<Boolean> {}

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
