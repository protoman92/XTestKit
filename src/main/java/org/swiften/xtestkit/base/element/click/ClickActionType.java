package org.swiften.xtestkit.base.element.click;

import io.reactivex.FlowableTransformer;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.rx.HPReactives;
import org.swiften.javautilities.util.HPLog;
import org.swiften.xtestkit.base.element.property.ElementPropertyType;
import org.swiften.xtestkit.base.element.tap.TapType;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.xtestkitcomponents.coordinate.RLPoint;
import org.swiften.xtestkitcomponents.coordinate.RLPositionType;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides methods to click on {@link WebElement}.
 * @param <D> Generics parameter.
 */
public interface ClickActionType<D extends WebDriver> extends
    ErrorProviderType,
    TapType<D>,
    ElementPropertyType
{
    /**
     * Perform a click action for {@link WebElement}.
     * @param element {@link WebElement} instance.
     * @see WebElement#click()
     * @see RLPoint#MID
     * @see #coordinate(WebElement, RLPositionType, RLPositionType)
     * @see #tap(Point)
     */
    default void click(@NotNull WebElement element) {
        HPLog.printft("Clicking on %s", element);
        element.click();
    }

    /**
     * Send a click event to {@link WebElement} with {@link WebElement#click()}.
     * @return {@link FlowableTransformer} instance.
     * @see #click(WebElement)
     */
    @NotNull
    default FlowableTransformer<WebElement, Boolean> clickFn() {
        final ClickActionType<?> THIS = this;

        return upstream -> upstream
            .compose(HPReactives.completableFn(THIS::click))
            .map(HPBooleans::toTrue)
            .defaultIfEmpty(true);
    }
}
