package com.swiften.engine.base.protocol;

/**
 * Created by haipham on 3/19/17.
 */
public interface EngineDelay {
    default long backNavigationDelay() {
        return 1000;
    }
}
