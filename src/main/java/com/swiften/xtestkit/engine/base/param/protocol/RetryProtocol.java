package com.swiften.xtestkit.engine.base.param.protocol;

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
    default int minRetries() {
        return 3;
    }

    /**
     * Use this retry count for operations that are expected to throw many
     * {@link Exception} instances before succeeding. For e.g., polling for
     * an emulator to be fully booted.
     * @return An {@link Integer} value.
     */
    default int maxRetries() {
        return 100;
    }
}
