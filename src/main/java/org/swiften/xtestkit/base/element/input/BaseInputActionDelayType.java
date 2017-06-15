package org.swiften.xtestkit.base.element.input;

/**
 * Created by haipham on 5/15/17.
 */

import org.openqa.selenium.WebElement;

/**
 * This interface provides delay in
 * {@link java.util.concurrent.TimeUnit#MILLISECONDS} for
 * {@link InputActionType}.
 */
public interface BaseInputActionDelayType {
    /**
     * Get the delay interval for
     * {@link InputActionType#rxa_toggleNextOrFinishInput(WebElement)}
     * so that the app has time to adjust its views.
     * @return {@link Long} value.
     * @see InputActionType#rxa_toggleNextOrFinishInput(WebElement)
     */
    default long consecutiveNextToggleDelay() {
        return 1000;
    }
}
