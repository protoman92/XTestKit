package org.swiften.xtestkit.engine.base.type;

/**
 * Created by haipham on 5/9/17.
 */

/**
 * This interface provides delay duration and
 * {@link java.util.concurrent.TimeUnit}.
 */
@FunctionalInterface
public interface DelayType extends TimeUnitType {
    /**
     * Get the associated delay duration.
     * @return A {@link Long} value.
     */
    long delay();
}
