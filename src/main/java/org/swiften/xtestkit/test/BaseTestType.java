package org.swiften.xtestkit.test;

/**
 * Created by haipham on 5/7/17.
 */

import org.jetbrains.annotations.NotNull;
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
}
