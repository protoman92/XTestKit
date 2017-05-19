package org.swiften.xtestkit.base.type;

import org.apache.tools.ant.taskdefs.optional.windows.Attrib;
import org.swiften.xtestkit.base.element.locator.general.xpath.Attribute;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/21/17.
 */

/**
 * This interface provides platform-specific properties.
 */
public interface PlatformType {
    @NotNull String value();

    /**
     * Specify the name of an index attribute. Generally should be 'index'.
     * @return An {@link Attribute} instance.
     * @see Attribute#single(String)
     */
    @NotNull
    default Attribute indexAttribute() {
        return Attribute.single("index");
    }

    /**
     * Specify the name of an instance attribute. Generally should be
     * 'instance'.
     * @return An {@link Attribute} instance.
     * @see Attribute#single(String)
     */
    @NotNull
    default Attribute instanceAttribute() {
        return Attribute.single("instance");
    }

    /**
     * Specify the name of a class attribute. Generally should be 'class'.
     * @return An {@link Attribute} instance.
     * @see Attribute#single(String)
     */
    @NotNull
    default Attribute classAttribute() {
        return Attribute.single("class");
    }

    /**
     * Specify the name for a id attribute. Generally should be 'id'.
     * @return An {@link Attribute} instance.
     */
    @NotNull
    Attribute idAttribute();

    /**
     * Specify the name for a text attribute. Generally should be 'text'.
      * @return An {@link Attribute} instance.
     */
    @NotNull
    Attribute textAttribute();

    /**
     * Specify the name for a hint attribute. For e.g., on Android it could
     * be 'hint', while on iOS it could be 'placeholder'.
     * @return An {@link Attribute} instance.
     */
    @NotNull
    Attribute hintAttribute();

    /**
     * Specify the name for an enabled attribute. Generally should be
     * 'enabled'.
     * @return An {@link Attribute} instance.
     * @see Attribute#single(String)
     */
    @NotNull
    default Attribute enabledAttribute() {
        return Attribute.single("enabled");
    }

    /**
     * Specify the name for a clickable attribute. Generally should be
     * 'clickable'.
     * @return An {@link Attribute} instance.
     * @see Attribute#single(String)
     */
    @NotNull
    default Attribute clickableAttribute() {
        return Attribute.single("clickable");
    }

    /**
     * Specify the name for a editable attribute. Generally should be
     * 'editable'.
     * @return An {@link Attribute} instance.
     * @see Attribute#single(String)
     */
    @NotNull
    default Attribute editableAttribute() {
        return Attribute.single("editable");
    }
}
