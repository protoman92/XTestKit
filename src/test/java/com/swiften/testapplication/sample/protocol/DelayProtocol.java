package com.swiften.testapplication.sample.protocol;

/**
 * Created by haipham on 3/25/17.
 */
public interface DelayProtocol {
    default long splashDelay() {
        return 2000;
    }

    default long generalDelay() {
        return 1000;
    }
}
