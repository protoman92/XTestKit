package org.swiften.xtestkit.ios.type;

/**
 * Created by haipham on 4/8/17.
 */

/**
 * This interface provides delay duration for
 * {@link org.swiften.xtestkit.ios.IOSEngine}.
 */
public interface IOSDelayType {
    default long simulatorLaunchTimeout() {
        return 60000;
    }
}
