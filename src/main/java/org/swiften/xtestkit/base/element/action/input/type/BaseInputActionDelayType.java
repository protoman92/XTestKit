package org.swiften.xtestkit.base.element.action.input.type;

/**
 * Created by haipham on 5/15/17.
 */

import org.openqa.selenium.WebElement;

/**
 * This interface provides delay in
 * {@link java.util.concurrent.TimeUnit#MILLISECONDS} for
 * {@link BaseInputActionType}.
 */
public interface BaseInputActionDelayType {
    /**
     * Get the delay interval for
     * {@link BaseInputActionType#rx_toggleNextOrDoneInput(WebElement)}
     * so that the app has time to adjust its views.
     * @return {@link Long} value.
     * @see BaseInputActionType#rx_toggleNextOrDoneInput(WebElement)
     */
    default long consecutiveNextToggleDelay() {
        return 1000;
    }
}
