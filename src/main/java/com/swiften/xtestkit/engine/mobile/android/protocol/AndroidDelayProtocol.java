package com.swiften.xtestkit.engine.mobile.android.protocol;

/**
 * Created by haipham on 3/22/17.
 */
public interface AndroidDelayProtocol {
    default long emulatorBootFinishDelay() {
        return 2000;
    }

    default long emulatorBoothTimeout() {
        return 30000;
    }

    default long emulatorBootRetryDelay() {
        return 1000;
    }
}
