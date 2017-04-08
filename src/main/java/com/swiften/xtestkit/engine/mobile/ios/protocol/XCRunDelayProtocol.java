package com.swiften.xtestkit.engine.mobile.ios.protocol;

/**
 * Created by haipham on 4/8/17.
 */
public interface XCRunDelayProtocol {
    default long simulatorBootRetryDelay() {
        return 1000;
    }
}
