package org.swiften.xtestkit.base.element.choice;

/**
 * Created by haipham on 25/5/17.
 */

import org.openqa.selenium.WebDriver;
import org.swiften.xtestkit.base.element.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.input.BaseInputActionType;
import org.swiften.xtestkit.base.element.swipe.BaseSwipeType;
import org.swiften.xtestkit.base.element.locator.type.BaseLocatorType;
import org.swiften.xtestkit.base.element.property.BaseElementPropertyType;
import org.swiften.xtestkit.base.type.PlatformContainerType;

/**
 * This interface provides helper methods to conveniently select an item
 * from a list of choices.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface ChoiceHelperType<D extends WebDriver> extends
    BaseClickActionType,
    BaseElementPropertyType,
    BaseInputActionType<D>,
    BaseLocatorType<D>,
    BaseSwipeType<D>,
    PlatformContainerType {}
