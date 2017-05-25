package org.swiften.xtestkit.test;

/**
 * Created by haipham on 5/7/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.kit.TestKit;
import org.swiften.xtestkit.util.EngineContainerType;

/**
 * UI tests should implement this interface to access some convenient methods.
 */
public interface BaseTestType extends EngineContainerType {
    /**
     * Get the currently active {@link TestKit}.
     * @return {@link TestKit} instance.
     */
    @NotNull TestKit testKit();

    /**
     * Get the current {@link Engine} index.
     * @return {@link Integer} value.
     */
    int currentIndex();

    /**
     * Get the currently active {@link Engine}.
     * @return {@link Engine} instance.
     * @see #testKit()
     * @see #currentIndex()
     * @see TestKit#engine(int)
     */
    @NotNull
    @Override
    default Engine<?> engine() {
        TestKit testKit = testKit();
        int index = currentIndex();
        return testKit.engine(index);
    }
}
