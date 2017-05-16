package org.swiften.xtestkit.base.element.property.type.base;

import org.swiften.javautilities.localizer.LCFormat;

/**
 * Created by haipham on 5/14/17.
 */

/**
 * This interface is the base for {@link String}-related locator operations
 * that involve {@link java.text.MessageFormat}.
 */
@FunctionalInterface
public interface FormatType extends
    AttributeType<LCFormat>,
    IgnoreCaseType {}
