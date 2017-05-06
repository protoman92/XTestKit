package org.swiften.xtestkit.engine.base;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/23/17.
 */
public interface RetryProtocol {
    @NotNull RetryProtocol DEFAULT = new RetryProtocol() {};

    /**
     * Use this retry count if we are running an operation that is not expected
     * to throw an {@link Exception}.
     * @return An {@link Integer} value.
     */
    default int retries() {
        return 10;
    }
}
