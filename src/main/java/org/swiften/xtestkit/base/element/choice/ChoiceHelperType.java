package org.swiften.xtestkit.base.element.choice;

/**
 * Created by haipham on 25/5/17.
 */

import org.openqa.selenium.WebDriver;
import org.swiften.xtestkit.base.element.click.ClickActionType;
import org.swiften.xtestkit.base.element.input.InputActionType;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.swiften.xtestkit.base.element.locator.LocatorType;
import org.swiften.xtestkit.base.element.property.ElementPropertyType;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkitcomponents.platform.PlatformProviderType;

/**
 * This interface provides helper methods to conveniently select an item
 * from a list of choices.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface ChoiceHelperType<D extends WebDriver> extends
    ClickActionType<D>,
    ElementPropertyType,
    InputActionType<D>,
    LocatorType<D>,
    SwipeType<D>,
    InputHelperType,
    PlatformProviderType {}
