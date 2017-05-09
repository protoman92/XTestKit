package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 4/6/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provide errors for
 * {@link org.swiften.xtestkit.base.ServerAddress}.
 */
public interface ServerAddressErrorType {
    @NotNull String SERVER_RUNNER_UNAVAILABLE = "Server runner not set";
}
