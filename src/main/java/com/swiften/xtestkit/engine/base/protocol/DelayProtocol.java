package com.swiften.xtestkit.engine.base.protocol;

/**
 * Created by haipham on 3/19/17.
 */
public interface DelayProtocol {
    default long appiumStartDelay() {
        return 5000;
    }

    default long backNavigationDelay() {
        return 1000;
    }
}
