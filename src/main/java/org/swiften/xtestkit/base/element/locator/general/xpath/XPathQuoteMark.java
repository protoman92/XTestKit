package org.swiften.xtestkit.base.element.locator.general.xpath;

/**
 * Created by haipham on 5/11/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.property.type.base.AttributeType;

import java.util.Optional;

/**
 * Use this with {@link XPath.QuotationFree} to strip quotation marks.
 */
public enum XPathQuoteMark implements AttributeType<String> {
    SINGLE,
    DOUBLE;

    /**
     * Return a {@link XPathQuoteMark} from a quotation mark.
     * @param quote A {@link String} value.
     * @return A {@link XPathQuoteMark} instance wrapped in an {@link Optional}.
     */
    @NotNull
    public static Optional<XPathQuoteMark> from(@NotNull String quote) {
        for (XPathQuoteMark qm : values()) {
            if (qm.value().equals(quote)) {
                return Optional.of(qm);
            }
        }

        return Optional.empty();
    }

    /**
     * Check if a {@link String} is a quotation mark.
     * @param value A {@link String} value.
     * @return A {@link Boolean} value.
     * @see #from(String)
     */
    public static boolean isQuoteMarks(@NotNull String value) {
        return from(value).isPresent();
    }

    /**
     * Wrap a {@link String} within appropriate quotation marks.
     * @param value A {@link String} value.
     * @return A {@link String} value.
     * @see #from(String)
     * @see #wrappedInQuotation()
     */
    @NotNull
    public static String wrapInQuotation(@NotNull String value) {
        Optional<XPathQuoteMark> qm = from(value);

        if (qm.isPresent()) {
            return qm.get().wrappedInQuotation();
        } else {
            return String.format("'%s'", value);
        }
    }

    //region AttributeType
    /**
     * Get the quotation mark to be used.
     * @return A {@link String} value.
     * @see AttributeType#value()
     */
    @NotNull
    public String value() {
        switch (this) {
            case SINGLE:
                return "'";

            case DOUBLE:
                return "\"";

            default:
                return "";
        }
    }
    //endregion

    /**
     * Return a quotation mark that is wrapped in quotation marks of the
     * opposite kind.
     * @return A {@link String} value.
     */
    @NotNull
    public String wrappedInQuotation() {
        switch (this) {
            case SINGLE:
                return "\"'\"";

            case DOUBLE:
                return "'\"'";

            default:
                return "";
        }
    }
}
