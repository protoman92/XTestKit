package org.swiften.xtestkit.base.element.locator.general.xpath.type;

/**
 * Created by haipham on 5/19/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;

/**
 * This interface provides a new
 * {@link org.swiften.xtestkit.base.element.locator.general.xpath.XPath.Builder}
 * instance.
 */
@FunctionalInterface
public interface NewXPathBuilderType {
    /**
     * Get the associated {@link XPath.Builder} instance.
     * @return {@link XPath.Builder} instance.
     */
    @NotNull XPath.Builder xPathBuilder();
}
