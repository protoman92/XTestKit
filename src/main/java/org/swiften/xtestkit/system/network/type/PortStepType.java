package org.swiften.xtestkit.system.network.type;

/**
 * Created by haipham on 18/5/17.
 */

/**
 * This interface provides a step value with which we increment port values
 * while checking for available ports. Usually used with {@link PortType}
 * and {@link MaxPortType} to provide port restrictions.
 */
public interface PortStepType {
    /**
     * Get the associated stepp value.
     * @return {@link Integer} value;
     */
    default int portStep() {
        return 1;
    }
}
