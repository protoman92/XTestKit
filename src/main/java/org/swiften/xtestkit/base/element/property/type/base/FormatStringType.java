package org.swiften.xtestkit.base.element.property.type.base;

import org.swiften.javautilities.localizer.LocalizationFormat;

/**
 * Created by haipham on 5/14/17.
 */

/**
 * This interface is the base for {@link String}-related locator operations
 * that involve {@link java.text.MessageFormat}.
 */
@FunctionalInterface
public interface FormatStringType extends
    AttributeType<LocalizationFormat>,
    IgnoreCaseType {}
