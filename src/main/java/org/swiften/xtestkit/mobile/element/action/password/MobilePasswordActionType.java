package org.swiften.xtestkit.mobile.element.action.password;

/**
 * Created by haipham on 5/15/17.
 */

import io.appium.java_client.MobileDriver;
import org.swiften.xtestkit.base.element.password.BasePasswordActionType;
import org.swiften.xtestkit.mobile.element.action.tap.MobileTapType;

/**
 * This interface provides methods to handle password fields for mobile apps.
 * @param <D> Generics parameter that extends {@link MobileDriver}.
 */
public interface MobilePasswordActionType<D extends MobileDriver> extends
    BasePasswordActionType<D>,
    MobileTapType<D> {}
