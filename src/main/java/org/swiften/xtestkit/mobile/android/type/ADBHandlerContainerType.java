package org.swiften.xtestkit.mobile.android.type;

/**
 * Created by haipham on 5/15/17.
 */

import org.swiften.xtestkit.mobile.android.adb.ADBHandler;

/**
 * This interface provides a
 * {@link org.swiften.xtestkit.mobile.android.adb.ADBHandler} instance.
 */
public interface ADBHandlerContainerType {
    /**
     * Get {@link ADBHandler} instance.
     * @return {@link ADBHandler} instance.
     */
    ADBHandler adbHandler();
}
