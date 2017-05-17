package org.swiften.xtestkit.system.network.type;

/**
 * Created by haipham on 4/9/17.
 */

/**
 * This interface provides a port value.
 */
@FunctionalInterface
public interface PortType {
    /**
     * Get the associated port value.
     * @return An {@link Integer} value.
     */
    int port();
}
