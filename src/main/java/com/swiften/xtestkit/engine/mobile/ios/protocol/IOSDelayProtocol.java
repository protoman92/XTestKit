package com.swiften.xtestkit.engine.mobile.ios.protocol;

/**
 * Created by haipham on 4/8/17.
 */
public interface IOSDelayProtocol {
    default long simulatorLaunchTimeout() {
        return 600000;
    }
}
