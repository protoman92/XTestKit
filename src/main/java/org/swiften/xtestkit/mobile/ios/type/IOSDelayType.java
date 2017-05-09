package org.swiften.xtestkit.mobile.ios.type;

/**
 * Created by haipham on 4/8/17.
 */
public interface IOSDelayType {
    default long simulatorLaunchTimeout() {
        return 600000;
    }
}
