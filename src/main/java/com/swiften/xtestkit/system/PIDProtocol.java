package com.swiften.xtestkit.system;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/10/17.
 */
public interface PIDProtocol {
    /**
     * Get a PID value.
     * @return A {@link String} value.
     */
    @NotNull String pid();
}
