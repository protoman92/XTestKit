package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 4/6/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Address;

/**
 * This interface provide errors for
 * {@link Address}.
 */
public interface AddressErrorType {
    @NotNull String SERVER_RUNNER_UNAVAILABLE = "Server runner not set";
}
