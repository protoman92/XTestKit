package org.swiften.xtestkit.base.element.action.choice.type;

/**
 * Created by haipham on 25/5/17.
 */

import org.openqa.selenium.WebDriver;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.action.input.type.BaseInputActionType;
import org.swiften.xtestkit.base.element.action.swipe.type.BaseSwipeType;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.element.property.type.BaseElementPropertyType;
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
