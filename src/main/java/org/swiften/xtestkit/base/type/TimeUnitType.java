package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides {@link java.util.concurrent.TimeUnit}.
 */
public interface TimeUnitType {
    /**
     * Get the associated {@link TimeUnit} instance.
     * @return {@link TimeUnit} instance.
     */
    @NotNull
    default TimeUnit timeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}
