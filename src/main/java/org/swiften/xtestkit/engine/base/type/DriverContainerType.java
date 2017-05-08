package org.swiften.xtestkit.engine.base.type;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides a {@link D} driver instance.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
@FunctionalInterface
public interface DriverContainerType<D extends WebDriver> {
    /**
     * Get the associated {@link D} instance.
     * @return A {@link D} instance.
     */
    @NotNull D driver();
}
