package org.swiften.xtestkit.android.type;

/**
 * Created by haipham on 5/15/17.
 */

import org.swiften.xtestkit.android.adb.ADBHandler;

/**
 * This interface provides a
 * {@link org.swiften.xtestkit.android.adb.ADBHandler} instance.
 */
public interface ADBHandlerProviderType {
    /**
     * Get {@link ADBHandler} instance.
     * @return {@link ADBHandler} instance.
     */
    ADBHandler adbHandler();
}
