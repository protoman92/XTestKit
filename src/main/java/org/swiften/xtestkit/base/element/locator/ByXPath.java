package org.swiften.xtestkit.base.element.locator;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.xpath.XPath;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Parameter object for {@link Engine#rxe_byXPath(ByXPath...)}.
 */
public class ByXPath implements LocatorErrorType, RetryProviderType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String error;
    @NotNull private String xpath;
    private int retries;
    private boolean logXPath;

    ByXPath() {
        xpath = "";
        error = NO_SUCH_ELEMENT;
        logXPath = true;
        retries = Constants.DEFAULT_RETRIES;
    }

    @NotNull
    @Override
    public String toString() {
        return xpath;
    }

    /**
     * Get {@link #retries}.
     * @return {@link Integer} value.
     * @see RetryProviderType#retries()
     */
    @Override
    public int retries() {
        return retries;
    }

    /**
     * Get {@link #error}.
     * @return {@link String} value.
     */
    @NotNull
    public String error() {
        return error;
    }

    /**
     * Get {@link #xpath}.
     * @return {@link String} value.
     */
    @NotNull
    public String xpath() {
        return xpath;
    }

    /**
     * Get {@link #logXPath}.
     * @return {@link Boolean} value.
     */
    public boolean logXPath() {
        return logXPath;
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
         * @param xpath {@link XPath} instance.
         * @return {@link Builder} instance.
         */
        public Builder withXPath(@NotNull XPath xpath) {
            PARAM.xpath = xpath.attribute();
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
         * @param type {@link RetryProviderType} instance.
         * @return {@link Builder} instance.
         * @see #withRetries(int)
         */
        @NotNull
        public Builder withRetryProvider(@NotNull RetryProviderType type) {
            return withRetries(type.retries());
        }

        /**
         * Set {@link #logXPath}.
         * @param logXPath {@link Boolean} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder shouldLogXPath(boolean logXPath) {
            PARAM.logXPath = logXPath;
            return this;
        }

        /**
         * Get {@link #PARAM}.
         * @return {@link ByXPath} instance.
         */
        @NotNull
        public ByXPath build() {
            return PARAM;
        }
    }
}
