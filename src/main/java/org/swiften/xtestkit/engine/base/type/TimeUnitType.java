package org.swiften.xtestkit.engine.base.type;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides a {@link java.util.concurrent.TimeUnit}.
 */
public interface TimeUnitType {
    /**
     * Get the associated {@link TimeUnit} instance.
     * @return A {@link TimeUnit} instance.
     */
    @NotNull
    default TimeUnit timeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}
