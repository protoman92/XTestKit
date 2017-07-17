package org.swiften.xtestkit.base.element.input;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides delay in
 * {@link java.util.concurrent.TimeUnit#MILLISECONDS} for
 * {@link InputActionType}.
 */
public interface InputActionDelayType {
    /**
     * Get the delay interval for {@link InputActionType#toggleNextOrFinishInputFn()}
     * so that the app has time to adjust its views.
     * @return {@link Long} value.
     */
    default long consecutiveNextToggleDelay() {
        return 1000;
    }
}
