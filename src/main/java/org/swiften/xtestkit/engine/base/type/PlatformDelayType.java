package org.swiften.xtestkit.engine.base.type;

/**
 * Created by haipham on 3/19/17.
 */
public interface PlatformDelayType {
    default long appiumStartDelay() {
        return 5000;
    }

    default long startDriverDelay() {
        return 3000;
    }

    default long stopDriverDelay() {
        return 3000;
    }
}
