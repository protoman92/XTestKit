package com.swiften.xtestkit.engine.mobile.ios.protocol;

/**
 * Created by haipham on 3/31/17.
 */
public interface IOSDelayProtocol {
    default long simulatorBootRetryDelay() {
        return 1000;
    }

    default long simulatorLaunchTimeout() {
        return 60000;
    }
}
