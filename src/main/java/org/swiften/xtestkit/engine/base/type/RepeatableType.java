package org.swiften.xtestkit.engine.base.type;

/**
 * Created by haipham on 5/8/17.
 */

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides methods to repeat an action.
 */
@FunctionalInterface
public interface RepeatableType {
    /**
     * Get the number of times to repeat an action.
     * @return An {@link Integer} value.
     */
    int times();

    /**
     * Get the delay duration between every two actions.
     * @return A {@link Long} value.
     */
    default long delay() {
        return 0;
    }

    /**
     * Get the {@link TimeUnit} to be used with {@link #delay()}.
     * @return A {@link TimeUnit} instance.
     */
    @NotNull
    default TimeUnit timeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}
