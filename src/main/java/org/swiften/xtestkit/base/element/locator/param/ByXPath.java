package org.swiften.xtestkit.base.element.locator.param;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.element.locator.type.BaseLocatorErrorType;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.swiften.xtestkitcomponents.xpath.XPath;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Parameter object for {@link Engine#rxe_byXPath(ByXPath)}.
 */
public class ByXPath implements BaseLocatorErrorType, RetryType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String error;
    @NotNull private String xPath;
    private int retries;

    ByXPath() {
        xPath = "";
        error = NO_SUCH_ELEMENT;
        retries = RetryType.super.retries();
    }

    @Override
    public int retries() {
        return retries;
    }

    @NotNull
    public String error() {
        return error;
    }

    @NotNull
    public String xPath() {
        return xPath;
    }

    /**
     * Builder class for {@link ByXPath}.
     */
    public static final class Builder {
        @NotNull final ByXPath PARAM;

        Builder() {
            PARAM = new ByXPath();
        }

        /**
         * Set the {@link #PARAM#error} value. This error will be used to
         * construct {@link Exception} when the Appium driver fails to
         * find an element.
         * @param error The error {@link String} to be thrown when no elements
         *              are found.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withError(@NotNull String error) {
            PARAM.error = error;
            return this;
        }

        /**
         * The {@link XPath} query that will be used to search for elements.
         * @param xPath {@link XPath} instance.
         * @return {@link Builder} instance.
         * @see XPath#compoundAttributes()
         */
        public Builder withXPath(@NotNull XPath xPath) {
            PARAM.xPath = xPath.attribute();
            return this;
        }

        /**
         * Set {@link #retries} value.
         * @param retries {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set {@link #retries} value.
         * @param type {@link RetryType} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryType(@NotNull RetryType type) {
            return withRetries(type.retries());
        }

        @NotNull
        public ByXPath build() {
            return PARAM;
        }
    }
}
