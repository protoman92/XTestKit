package org.swiften.xtestkit.engine.base.type;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/23/17.
 */
public interface RetryType {
    @NotNull
    RetryType DEFAULT = new RetryType() {};

    /**
     * Use this retry count if we are running an operation that is not expected
     * to throw an {@link Exception}.
     * @return An {@link Integer} value.
     */
    default int retries() {
        return 10;
    }
}
