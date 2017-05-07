package org.swiften.xtestkit.test;

/**
 * Created by haipham on 5/7/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.kit.TestKit;

/**
 * UI tests should implement this interface to access some convenient methods.
 */
public interface BaseTestType {
    /**
     * Get the currently active {@link TestKit}.
     * @return A {@link TestKit} instance.
     */
    @NotNull TestKit testKit();

    /**
     * Get the current {@link org.swiften.xtestkit.engine.base.PlatformEngine}
     * index.
     * @return An {@link Integer} value.
     */
    int currentIndex();

    @NotNull
    default PlatformEngine currentEngine() {
        TestKit testKit = testKit();
        int index = currentIndex();
        return testKit.engine(index);
    }
}
