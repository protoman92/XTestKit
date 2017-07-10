package org.swiften.xtestkit.ios.element.locator;

/**
 * Created by haipham on 12/6/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.xtestkitcomponents.platform.XMLAttributeType;

/**
 * XML attributes for {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
 */
public enum AndroidXMLAttribute implements ErrorProviderType, XMLAttributeType {
    CONTENT_DESC;

    /**
     * Override this method to provide default implementation.
     * @return {@link String} value.
     * @see #CONTENT_DESC
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public String value() {
        switch (this) {
            case CONTENT_DESC:
                return "content-desc";

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}
