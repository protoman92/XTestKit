package org.swiften.xtestkit.base.element.action.choice;

/**
 * Created by haipham on 25/5/17.
 */

import org.openqa.selenium.WebDriver;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.action.input.BaseInputActionType;
import org.swiften.xtestkit.base.element.action.swipe.BaseSwipeType;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
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
