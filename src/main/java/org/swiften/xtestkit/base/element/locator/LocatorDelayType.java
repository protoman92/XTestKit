package org.swiften.xtestkit.base.element.locator;

/**
 * Created by haipham on 19/6/17.
 */

import org.openqa.selenium.By;

/**
 * This interface provides delay durations for {@link LocatorType}.
 */
public interface LocatorDelayType {
    /**
     * Get the timeout duration for
     * {@link org.openqa.selenium.WebDriver#findElements(By)}.
     * @return {@link Long} value.
     */
    default long elementLocateTimeout() {
        return 5000;
    }
}
