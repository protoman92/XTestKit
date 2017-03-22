package com.swiften.engine.mobile.android.protocol;

/**
 * Created by haipham on 3/22/17.
 */
public interface AndroidDelay {
    default long emulatorBootRetryDelay() {
        return 1000;
    }
}
