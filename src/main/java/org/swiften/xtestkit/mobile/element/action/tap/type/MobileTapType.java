package org.swiften.xtestkit.mobile.element.action.tap.type;

/**
 * Created by haipham on 5/15/17.
 */

import io.appium.java_client.MobileDriver;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.tap.type.TapType;
import org.swiften.xtestkit.base.element.action.tap.type.BaseTapType;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileTouchActionType;

/**
 * This interface provides methods to handle mobile tap actions.
 * @param <D> Generics parameter that extends {@link MobileDriver}.
 */
public interface MobileTapType<D extends MobileDriver> extends
    BaseTapType<D>, MobileActionType<D>
{
    /**
     * Override to use {@link MobileTouchActionType#tap(MobileDriver, TapType)}.
     * @param param A {@link TapType} instance.
     * @param <P> Generics parameter.
     * @see #driver()
     * @see #touchAction()
     * @see MobileTouchActionType#tap(MobileDriver, TapType)
     */
    @Override
    default <P extends TapType & RetryType> void tap(@NotNull P param) {
        touchAction().tap(driver(), param);
    }
}
