package org.swiften.xtestkit.mobile.element.action.password.type;

/**
 * Created by haipham on 5/15/17.
 */

import io.appium.java_client.MobileDriver;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.action.password.type.BasePasswordActionType;
import org.swiften.xtestkit.base.element.action.tap.type.BaseTapType;
import org.swiften.xtestkit.mobile.element.action.tap.type.MobileTapType;

/**
 * This interface provides methods to handle password fields for mobile apps.
 * @param <D> Generics parameter that extends {@link MobileDriver}.
 */
public interface MobilePasswordActionType<D extends MobileDriver> extends
    BasePasswordActionType<D>,
    MobileTapType<D> {}
