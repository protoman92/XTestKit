package org.swiften.xtestkit.base.element.action.input.type;

/**
 * Created by haipham on 5/15/17.
 */

import org.swiften.xtestkit.mobile.android.element.action.input.AndroidInputActionType;

/**
 * This interface provides delay in
 * {@link java.util.concurrent.TimeUnit#MILLISECONDS} for
 * {@link BaseInputActionType}.
 */
public interface BaseInputActionDelayType {
    /**
     * Get the delay interval for {@link BaseInputActionType#rxToggleNextInput()}
     * so that the app has time to adjust its views.
     * @return A {@link Long} value.
     * @see BaseInputActionType#rxToggleNextInput()
     */
    default long consecutiveNextToggleDelay() {
        return 1000;
    }
}
