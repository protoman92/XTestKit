package org.swiften.xtestkit.system.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.system.process.ProcessRunner;

/**
 * Created by haipham on 5/18/17.
 */

/**
 * This interface provides a {@link ProcessRunner}.
 */
@FunctionalInterface
public interface ProcessRunnerHolderType {
    /**
     * Get the associated {@link ProcessRunner} instance.
     * @return A {@link ProcessRunner} instance.
     */
    @NotNull
    ProcessRunner processRunner();
}
