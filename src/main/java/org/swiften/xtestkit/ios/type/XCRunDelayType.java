package org.swiften.xtestkit.ios.type;

/**
 * Created by haipham on 4/8/17.
 */
public interface XCRunDelayType {
    default long simulatorBootRetryDelay() {
        return 1000;
    }
}
