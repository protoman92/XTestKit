package org.swiften.xtestkit.base.element.click;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.property.ElementPropertyType;
import org.swiften.xtestkit.base.element.tap.TapType;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
import org.swiften.xtestkitcomponents.coordinate.RLPosition;
import org.swiften.xtestkitcomponents.coordinate.RLPositionType;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides methods to click on {@link WebElement}.
 * @param <D> Generics parameter.
 */
public interface ClickActionType<D extends WebDriver> extends
    BaseErrorType,
    TapType<D>,
    ElementPropertyType
{
    /**
     * Perform a click action for {@link WebElement}.
     * @param element {@link WebElement} instance.
     * @see WebElement#click()
     * @see RLPosition#MID
     * @see #coordinate(WebElement, RLPositionType, RLPositionType)
     * @see #tap(Point)
     */
    default void click(@NotNull WebElement element) {
        LogUtil.printft("Clicking on %s", element);
//        tap(coordinate(element, RLPosition.MID, RLPosition.MID));
        element.click();
    }

    /**
     * Send a click event to {@link WebElement} with
     * {@link WebElement#click()}.
     * @param ELEMENT The {@link WebElement} to be clicked.
     * @return {@link Flowable} instance.
     * @see #click(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rxa_click(@NotNull final WebElement ELEMENT) {
        final ClickActionType<?> THIS = this;

        return Completable
            .fromAction(() -> THIS.click(ELEMENT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }
}
