package org.swiften.xtestkit.engine.base;

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

    default long startDriverDelay() {
        return 3000;
    }

    default long stopDriverDelay() {
        return 3000;
    }
}