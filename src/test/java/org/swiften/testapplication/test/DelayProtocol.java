package org.swiften.testapplication.test;

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
