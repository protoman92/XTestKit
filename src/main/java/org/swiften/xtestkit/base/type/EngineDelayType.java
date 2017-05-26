package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 3/19/17.
 */

import org.swiften.xtestkit.base.Engine;

/**
 * This interface provides delay durations for {@link Engine}.
 */
public interface EngineDelayType {
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
