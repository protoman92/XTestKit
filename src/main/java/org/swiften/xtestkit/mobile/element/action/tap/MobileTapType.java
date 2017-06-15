package org.swiften.xtestkit.mobile.element.action.tap;

/**
 * Created by haipham on 5/15/17.
 */

import io.appium.java_client.MobileDriver;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.tap.TapParamType;
import org.swiften.xtestkit.base.element.tap.TapType;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.general.MobileTouchActionType;

/**
 * This interface provides methods to handle mobile tap actions.
 * @param <D> Generics parameter that extends {@link MobileDriver}.
 */
public interface MobileTapType<D extends MobileDriver> extends
    TapType<D>, MobileActionType<D>
{
    /**
     * Override to use {@link MobileTouchActionType#tap(MobileDriver, TapParamType)}.
     * @param param {@link TapParamType} instance.
     * @param <P> Generics parameter.
     * @see #driver()
     * @see #touchAction()
     * @see MobileTouchActionType#tap(MobileDriver, TapParamType)
     */
    @Override
    default <P extends TapParamType & RetryType> void tap(@NotNull P param) {
        touchAction().tap(driver(), param);
    }
}
