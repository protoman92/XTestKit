package org.swiften.xtestkit.base.element.action.tap.type;

/**
 * Created by haipham on 5/15/17.
 */

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.swiften.xtestkit.base.element.action.password.type.TapType;
import org.swiften.xtestkit.base.element.action.tap.param.TapParam;
import org.swiften.xtestkit.base.type.RetryType;

import java.awt.*;

/**
 * This interface provides methods to handle taps.
 */
public interface BaseTapType<D extends WebDriver> {
    /**
     * Perform a tap action.
     * @param param A {@link TapType} instance.
     * @param <P> Generics parameter.
     */
    <P extends TapType & RetryType> void tap(@NotNull P param);

    /**
     * Same as above, but uses a default {@link TapParam}.
     * @param x The tap's x coordinate, an {@link Integer} value.
     * @param y The tap's y coordinate, an {@link Integer} value.
     * @see #tap(TapType)
     */
    default void tap(int x, int y) {
        tap(TapParam.builder().withX(x).withY(y).build());
    }
}
