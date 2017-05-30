package org.swiften.xtestkit.base.element.action.switcher;

/**
 * Created by haipham on 5/30/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

/**
 * This interface provides methods to handle switcher
 * {@link org.openqa.selenium.WebElement}.
 */
public interface BaseSwitcherActionType {
    /**
     * Get the switcher's current value.
     * @param element {@link WebElement} instance.
     * @return {@link String} value.
     * @see WebElement#getAttribute(String)
     */
    @NotNull
    default String getSwitcherValue(@NotNull WebElement element) {
        return element.getAttribute("value");
    }

    /**
     * Get the switcher's associated value when it is off.
     * @return {@link String} value.
     */
    @NotNull
    default String switcherOffValue() {
        return "0";
    }

    /**
     * Get the switcher's associated value when it is on.
     * @return {@link String} value.
     */
    @NotNull
    default String switcherOnValue() {
        return "1";
    }

    /**
     * Toggle the switcher on or off. If switcher's state has already been
     * set to the target value, do nothing.
     * @param element {@link WebElement} instance.
     * @param on {@link Boolean} value.
     * @see WebElement#click()
     * @see #getSwitcherValue(WebElement)
     * @see #switcherOffValue()
     * @see #switcherOnValue()
     */
    default void toggleSwitch(@NotNull WebElement element, boolean on) {
        String currentValue = getSwitcherValue(element);
        String target = on ? switcherOnValue() : switcherOffValue();

        if (!currentValue.equals(target)) {
            element.click();
        }
    }

    /**
     * Toggle the switcher on or off.
     * @param ELEMENT {@link WebElement} instance.
     * @param ON {@link Boolean} value.
     * @return {@link Flowable} instance.
     * @see #toggleSwitch(WebElement, boolean)
     */
    @NotNull
    default Flowable<WebElement> rxa_toggleSwitch(@NotNull final WebElement ELEMENT,
                                                  final boolean ON) {
        final BaseSwitcherActionType THIS = this;

        return Completable
            .fromAction(() -> THIS.toggleSwitch(ELEMENT, ON))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }
}
