package org.swiften.xtestkit.engine.base.param;

import org.swiften.xtestkit.engine.base.BaseEngine;
import org.swiften.xtestkit.engine.base.type.BaseViewType;
import org.swiften.xtestkit.engine.base.type.RetryType;
import org.swiften.xtestkit.engine.base.locator.xpath.XPath;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Parameter object for {@link BaseEngine#rxElementsByXPath(ByXPath)}.
 */
public class ByXPath implements RetryType {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private List<BaseViewType> classes;
    @NotNull private String error;
    @NotNull private String xPath;
    private int retries;

    ByXPath() {
        classes = new ArrayList<>();
        xPath = "";
        error = "";
        retries = RetryType.super.retries();
    }

    //region RetryType.
    @Override
    public int retries() {
        return retries;
    }
    //endregion

    @NotNull
    public List<BaseViewType> classes() {
        return classes;
    }

    @NotNull
    public String error() {
        return error;
    }

    @NotNull
    public String xPath() {
        return xPath;
    }

    public static final class Builder {
        @NotNull final ByXPath PARAM;

        Builder() {
            PARAM = new ByXPath();
        }

        /**
         * Add view classes to {@link #PARAM#classes}. These view classes
         * are used to construct the {@link XPath} query.
         * @param cls The {@link Collection} of {@link BaseViewType}.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withClasses(@NotNull Collection<? extends BaseViewType> cls) {
            PARAM.classes.addAll(cls);
            return this;
        }

        /**
         * Add a {@link BaseViewType} instance to {@link #PARAM#classes}.
         * @param cls The {@link BaseViewType} to be added.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder addClasses(@NotNull BaseViewType cls) {
            PARAM.classes.add(cls);
            return this;
        }

        /**
         * Set the {@link #PARAM#error} value. This error will be used to
         * construct an {@link Exception} when the Appium driver fails to
         * find an element.
         * @param error The error {@link String} to be thrown when no elements
         *              are found.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withError(@NotNull String error) {
            PARAM.error = error;
            return this;
        }

        /**
         * The {@link XPath} query that will be used to search for elements.
         * @param xPath A {@link XPath} instance.
         * @return The current {@link Builder} instance.
         */
        public Builder withXPath(@NotNull XPath xPath) {
            PARAM.xPath = xPath.getAttribute();
            return this;
        }

        /**
         * Set {@link #retries} value.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryCount(int retries) {
            PARAM.retries = retries;
            return this;
        }

        /**
         * Set {@link #retries} value.
         * @param type A {@link RetryType} instance.
         * @return THe current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryType(@NotNull RetryType type) {
            return withRetryCount(type.retries());
        }

        @NotNull
        public ByXPath build() {
            if (PARAM.classes.isEmpty()) {
                addClasses(BaseViewType.ANY_VIEW);
            }

            return PARAM;
        }
    }
}
