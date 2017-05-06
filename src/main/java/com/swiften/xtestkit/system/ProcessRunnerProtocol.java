package com.swiften.xtestkit.system;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Created by haipham on 4/6/17.
 */
public interface ProcessRunnerProtocol {
    /**
     * Execute a command on the main thread.
     * @param args A {@link String} command to be executed.
     * @return A {@link String} output value.
     * @throws IOException The command could fail and throw this
     * {@link IOException}.
     */
    @NotNull
    String execute(@NotNull String args) throws IOException;

    /**
     * Execute a command reactively.
     * @param args A {@link String} command to be executed.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<String> rxExecute(@NotNull String args);
}
