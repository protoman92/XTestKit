package com.swiften.xtestkit.engine.mobile.android.protocol;

/**
 * Created by haipham on 4/8/17.
 */
public interface ADBDelayProtocol {
    default long emulatorBootFinishDelay() {
        return 2000;
    }

    default long emulatorBootTimeout() {
        return 50000;
    }

    default long emulatorBootRetryDelay() {
        return 1000;
    }
}
