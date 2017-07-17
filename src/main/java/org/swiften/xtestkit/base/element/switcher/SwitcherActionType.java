package org.swiften.xtestkit.base.element.switcher;

/**
 * Created by haipham on 5/30/17.
 */

import io.reactivex.FlowableTransformer;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.rx.HPReactives;
import org.swiften.javautilities.util.HPLog;

/**
 * This interface provides methods to handle switcher
 * {@link org.openqa.selenium.WebElement}.
 */
public interface SwitcherActionType {
    /**
     * Get the switcher's current value.
     * @param element {@link WebElement} instance.
     * @return {@link String} value.
     */
    @NotNull String switcherValue(@NotNull WebElement element);

    /**
     * Get the switcher's associated value when it is off.
     * @return {@link String} value.
     */
    @NotNull String switcherOffValue();

    /**
     * Get the switcher's associated value when it is on.
     * @return {@link String} value.
     */
    @NotNull String switcherOnValue();

    /**
     * Check if a switch is on/off.
     * @param element {@link WebElement} instance.
     * @return {@link Boolean} value.
     * @see #switcherOnValue()
     * @see #switcherValue(WebElement)
     */
    default boolean isSwitchOn(@NotNull WebElement element) {
        return switcherValue(element).equals(switcherOnValue());
    }

    /**
     * Toggle the switcher on or off. If switcher's state has already been
     * set to the target value, do nothing.
     * @param element {@link WebElement} instance.
     * @param on {@link Boolean} value.
     * @see #switcherValue(WebElement)
     * @see #switcherOffValue()
     * @see #switcherOnValue()
     */
    default void toggleSwitch(@NotNull WebElement element, boolean on) {
        String currentValue = switcherValue(element);
        String target = on ? switcherOnValue() : switcherOffValue();
        HPLog.printft("Switcher value: %s, target: %s", currentValue, target);

        if (!currentValue.equals(target)) {
            element.click();
        }
    }

    /**
     * Toggle the switcher on or off.
     * @param ON {@link Boolean} value.
     * @return {@link FlowableTransformer} instance.
     * @see #toggleSwitch(WebElement, boolean)
     */
    @NotNull
    default FlowableTransformer<WebElement, Boolean> toggleSwitchFn(final boolean ON) {
        final SwitcherActionType THIS = this;

        return upstream -> upstream
            .compose(HPReactives.completableFn(a -> THIS.toggleSwitch(a, ON)))
            .map(HPBooleans::toTrue)
            .defaultIfEmpty(true);
    }
}
