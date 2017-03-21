package com.swiften.engine.base.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/21/17.
 */
public interface PlatformProtocol {
    @NotNull String value();

    /**
     * Specify the name for a text attribute. Generally should be 'text'.
      * @return A {@link String} value.
     */
    @NotNull
    default String textAttribute() {
        return "text";
    }

    /**
     * Specify the name for a hint attribute. For e.g., on Android it could
     * be 'hint', while on iOS it could be 'placeholder'.
     * @return A {@link String} value.
     */
    @NotNull
    String hintAttribute();

    /**
     * Specify the name for an enabled attribute. Generally should be
     * 'enabled'.
     * @return A {@link String} value.
     */
    @NotNull
    default String enabledAttribute() {
        return "enabled";
    }
}
