package org.swiften.xtestkit.system.network.type;

/**
 * Created by haipham on 18/5/17.
 */

/**
 * This interface provides a max port value. Usually it is used in tandem
 * with {@link PortType} to provide port value restrictions.
 */
public interface MaxPortType {
    /**
     * Get the maximum port value to be used.
     * @return {@link Integer} value.
     */
    default int maxPort() {
        return Integer.MAX_VALUE;
    }
}
