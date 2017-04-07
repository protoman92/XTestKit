package com.swiften.xtestkit.system;

import com.swiften.xtestkit.system.protocol.ProcessRunnerProtocol;
import com.swiften.xtestkit.system.protocol.NetworkHandlerError;
import com.swiften.xtestkit.util.BooleanUtil;
import com.swiften.xtestkit.util.Log;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haipham on 4/7/17.
 */
public class NetworkHandler implements NetworkHandlerError {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Nullable private WeakReference<ProcessRunnerProtocol> processRunner;

    NetworkHandler() {}

    /**
     * Return a {@link ProcessRunnerProtocol} instance from
     * {@link #processRunner}.
     * @return A {@link ProcessRunnerProtocol} instance.
     */
    @NotNull
    public ProcessRunnerProtocol processRunner() {
        ProcessRunnerProtocol runner;

        if
            (Objects.nonNull(processRunner) &&
            (Objects.nonNull(runner = processRunner.get())))
        {
            return runner;
        }

        throw new RuntimeException(PROCESS_RUNNER_UNAVAILABLE);
    }

    /**
     * Check if a port is available.
     * @param PORT An {@link Integer} value representing the port to be
     *             checked.
     * @return A {@link Flowable} instance.
     * @see #cmListAllPorts()
     * @see #isPortAvailable(String, int)
     */
    @NotNull
    public Flowable<Boolean> rxCheckPortAvailable(final int PORT) {
        ProcessRunnerProtocol processRunner = processRunner();
        String command = cmListAllPorts();

        return processRunner
            .rxExecute(command)
            .map(a -> isPortAvailable(a, PORT));
    }

    /**
     * Use regular expression to check the output of {@link #cmListAllPorts()}.
     * @param output A {@link String} value. This shoudl be the output from
     *               {@link #cmListAllPorts()}.
     * @param port An {@link Integer} value.
     * @return A {@link Boolean} value.
     */
    public boolean isPortAvailable(@NotNull String output, int port) {
        /* The output we are looking for is *.${PORT} (LISTEN).
         * E.g., *.4723 (LISTEN) */
        String regex = String.format("\\*.%d( \\(LISTEN\\))?", port);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(output);
        return !matcher.find();
    }

    /**
     * Recursively check ports until one is available. Everytime a port is
     * not available, we increment it by one and call this method again.
     * @param PORT The port to be checked. An {@link Integer} value.
     * @return A {@link Flowable} instance.
     * @see #rxCheckPortAvailable(int)
     */
    @NotNull
    public Flowable<Integer> rxCheckUntilPortAvailable(final int PORT) {
        return rxCheckPortAvailable(PORT)
            /* If we use filter() and switchIfEmpty() here, a StackOverflow
             * error will be thrown. The solution below may not look as nice
             * but it works correctly */
            .flatMap(a -> {
                if (BooleanUtil.isTrue(a)) {
                    return Flowable.just(PORT);
                }

                /* Keep checking ports until one is available */
                return rxCheckUntilPortAvailable(PORT + 1);
            });
    }

    /**
     * Check if there is any available port between two port numbers.
     * @param PORT The current port being checked. An {@link Integer} value.
     * @param LIMIT The limit value. An {@link Integer} value.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Integer> rxCheckUntilPortAvailable(final int PORT, final int LIMIT) {
        if (PORT <= LIMIT) {
            return rxCheckPortAvailable(PORT)
                .flatMap(a -> {
                    if (BooleanUtil.isTrue(a)) {
                        return Flowable.just(PORT);
                    }

                    return rxCheckUntilPortAvailable(PORT + 1, LIMIT);
                });
        }

        return Flowable.error(new Exception(NO_PORT_AVAILABLE));
    }

    /**
     * Command to list all used ports.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmListAllPorts() {
        return "lsof -i";
    }

    public static final class Builder {
        @NotNull private final NetworkHandler HANDLER;

        Builder() {
            HANDLER = new NetworkHandler();
        }

        /**
         * Set the {@link #HANDLER#processRunner} instance. We wrap it in
         * a {@link WeakReference} to avoid circular references, since it is
         * likely that an {@link Object} that implements
         * {@link ProcessRunnerProtocol} also has this {@link NetworkHandler}
         * as an instance variable.
         * @param runner A {@link ProcessRunnerProtocol} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withProcessRunner(@NotNull ProcessRunnerProtocol runner) {
            HANDLER.processRunner = new WeakReference<>(runner);
            return this;
        }

        @NotNull
        public NetworkHandler build() {
            return HANDLER;
        }
    }
}
