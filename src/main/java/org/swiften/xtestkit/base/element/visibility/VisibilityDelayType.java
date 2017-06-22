package org.swiften.xtestkit.base.element.visibility;

/**
 * Created by haipham on 22/6/17.
 */

/**
 * This interface provides delay duration for {@link VisibilityActionType}.
 */
public interface VisibilityDelayType {
    /**
     * Provide the delay between two consecutive checks for
     * {@link org.openqa.selenium.WebElement} visibility.
     * @return {@link Long} value.
     */
    default long consecutiveVisibilityCheckDelay() {
        return 1000;
    }
}
