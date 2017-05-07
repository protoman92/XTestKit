package org.swiften.xtestkit.engine.base.param;

import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.engine.base.View;
import org.swiften.xtestkit.engine.base.xpath.XPath;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * Parameter object for
 * {@link PlatformEngine#rxElementsByXPath(ByXPath)}.
 */
public class ByXPath {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private List<View> classes;
    @NotNull private String error;
    @NotNull private String xPath;
    @Nullable private Flowable<List<WebElement>> parent;

    ByXPath() {
        classes = new ArrayList<>();
        error = "";
        xPath = "";
    }

    @NotNull
    public List<View> classes() {
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

    @Nullable
    public Flowable<List<WebElement>> parent() {
        return parent;
    }

    public static final class Builder {
        @NotNull final ByXPath PARAM;

        Builder() {
            PARAM = new ByXPath();
        }

        /**
         * Add view classes to {@link #PARAM#classes}. These view classes
         * are used to construct the {@link XPath} query.
         * @param cls The {@link Collection} of {@link View}.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withClasses(@NotNull Collection<? extends View> cls) {
            PARAM.classes.addAll(cls);
            return this;
        }

        /**
         * Add a {@link View} instance to {@link #PARAM#classes}.
         * @param cls The {@link View} to be added.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder addClasses(@NotNull View cls) {
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
         * Sometimes, there is already a plural method (e.g. elements vs
         * element) that return a {@link Flowable} that emits a {@link List}
         * of {@link WebElement} and we simply want to take the first
         * {@link WebElement}. We can take the first element from that method
         * instead of composing a new XPath query. If {@link #PARAM#parent}
         * is set, {@link #PARAM#classes} and {@link #PARAM#xPath} will be
         * ignored.
         * @param parent A {@link Flowable} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withParent(@NotNull Flowable<List<WebElement>> parent) {
            PARAM.parent = parent;
            return this;
        }

        @NotNull
        public ByXPath build() {
            if (PARAM.classes.isEmpty()) {
                addClasses(View.ANY_VIEW);
            }

            return PARAM;
        }
    }
}
