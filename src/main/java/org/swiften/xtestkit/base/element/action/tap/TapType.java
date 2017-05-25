package org.swiften.xtestkit.base.element.action.tap;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.RetryType;

import java.awt.*;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides arguments for a tap action.
 */
public interface TapType {
    /**
     * The tap's x position.
     * @return {@link Integer} value.
     */
    int x();

    /**
     * The tap's y position.
     * @return {@link Integer} value.
     */
    int y();
}
