package org.swiften.xtestkit.base.element.general;

/**
 * Created by haipham on 1/6/17.
 */

/**
 * This interface provides delay duration for {@link BaseActionType}.
 */
public interface BaseActionDelayType {
    /**
     * Delay duration between the time the dialog is dismissed and the time
     * it disappears from the screen.
     * @return {@link Long} value.
     */
    default long alertDismissDelay() {
        return 2000;
    }
}
