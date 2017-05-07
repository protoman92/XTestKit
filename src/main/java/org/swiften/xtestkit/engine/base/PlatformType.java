package org.swiften.xtestkit.engine.base;

import org.swiften.xtestkit.engine.base.xpath.Attribute;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/21/17.
 */
public interface PlatformType {
    @NotNull String value();

    /**
     * Specify the name for a text attribute. Generally should be 'text'.
      * @return A {@link Attribute} value.
     */
    @NotNull
    Attribute textAttribute();

    /**
     * Specify the name for a hint attribute. For e.g., on Android it could
     * be 'hint', while on iOS it could be 'placeholder'.
     * @return A {@link Attribute} value.
     */
    @NotNull
    Attribute hintAttribute();

    /**
     * Specify the name for an enabled attribute. Generally should be
     * 'enabled'.
     * @return A {@link String} value.
     */
    @NotNull
    default Attribute enabledAttribute() {
        return Attribute.withSingleAttribute("enabled");
    }

    /**
     * Specify the name for a clickable attribute. Generally should be
     * 'clickable'.
     * @return A {@link Attribute} value.
     */
    @NotNull
    default Attribute clickableAttribute() {
        return Attribute.withSingleAttribute("clickable");
    }

    /**
     * Specify the name for a editable attribute. Generally should be
     * 'editable'.
     * @return A {@link Attribute} value.
     */
    @NotNull
    default Attribute editableAttribute() {
        return Attribute.withSingleAttribute("editable");
    }
}
