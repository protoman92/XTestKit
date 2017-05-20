package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides a duration in
 * {@link java.util.concurrent.TimeUnit#MILLISECONDS}.
 */
@FunctionalInterface
public interface DurationType {
    /**
     * Get a duration in {@link java.util.concurrent.TimeUnit#MILLISECONDS}.
     * @return {@link Integer} value.
     */
    int duration();
}
