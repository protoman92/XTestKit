package com.swiften.engine.base.param.protocol;

/**
 * Created by haipham on 3/23/17.
 */
public interface RetryProtocol {
    default int retriesOnError() {
        return 3;
    }
}
