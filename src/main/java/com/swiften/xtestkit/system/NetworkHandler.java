package com.swiften.xtestkit.system;

import com.swiften.xtestkit.system.protocol.ProcessRunnerProtocol;
import com.swiften.xtestkit.system.protocol.NetworkHandlerError;
import com.swiften.xtestkit.util.BooleanUtil;
import com.swiften.xtestkit.util.Log;
import com.swiften.xtestkit.util.StringUtil;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haipham on 4/7/17.
 */
public class NetworkHandler implements NetworkHandlerError {
    @NotNull private static Collection<Integer> USED_PORTS;

    static {
        USED_PORTS = new HashSet<>();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final ProcessRunner PROCESS_RUNNER;

    NetworkHandler() {
        PROCESS_RUNNER = ProcessRunner.builder().build();
    }

    /**
     * Return a {@link ProcessRunnerProtocol} instance from
     * {@link #PROCESS_RUNNER}.
     * @return A {@link ProcessRunnerProtocol} instance.
     */
    @NotNull
    public ProcessRunnerProtocol processRunner() {
        return PROCESS_RUNNER;
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
    public synchronized boolean isPortAvailable(@NotNull String output, int port) {
        if (USED_PORTS.contains(port)) {
            return false;
        }

        /* The output we are looking for is *.${PORT} (LISTEN).
         * E.g., *.4723 (LISTEN) */
        String regex = String.format("\\*.%d( \\(LISTEN\\))?", port);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(output);

        if (!matcher.find()) {
            USED_PORTS.add(port);
            return true;
        }

        return false;
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
                    Log.printf("Port %d is currently available", PORT);
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
     * Kill a process using its PID value.
     * @param pid A {@link String} value.
     * @return A {@link Flowable} instance.
     * @see #cmKillPID(String)
     */
    @NotNull
    public Flowable<Boolean> rxKillProcessWithPid(@NotNull String pid) {
        ProcessRunnerProtocol runner = processRunner();
        String command = cmKillPID(pid);
        return runner.rxExecute(command).map(a -> true);
    }

    /**
     * Kill a process that is listening to a port.
     * @param port An {@link Integer} value.
     * @return A {@link Flowable} instance.
     * @see #cmFindPID(int)
     * @see #rxKillProcessWithPid(String)
     */
    @NotNull
    public Flowable<Boolean> rxKillProcessWithPort(int port) {
        return processRunner()
            .rxExecute(cmFindPID(port))
            .filter(StringUtil::isNotNullOrEmpty)
            .map(a -> a.replace("\n", ""))
            .flatMap(this::rxKillProcessWithPid)
            .defaultIfEmpty(true);
    }

    /**
     * Kill a process using its name.
     * @param name A {@link String} value.
     * @return A {@link Flowable} instance.
     * @see #cmFindPID(String)
     */
    @NotNull
    public Flowable<Boolean> rxKillProcessWithName(@NotNull String name) {
        return processRunner()
            .rxExecute(cmFindPID(name))
            .filter(StringUtil::isNotNullOrEmpty)
            .map(a -> a.replace("\n", ""))
            .flatMap(this::rxKillProcessWithPid)
            .defaultIfEmpty(true);
    }

    /**
     * Kill all instances of a process.
     * @param name The process' name. A {@link String} value.
     * @return A {@link Flowable} instance.
     * @see #cmKillAll(String)
     */
    @NotNull
    public Flowable<Boolean> rxKillAll(@NotNull String name) {
        return processRunner().rxExecute(cmKillAll(name)).map(a -> true);
    }

    //region CLI
    /**
     * Command to kill all instances of a process name.
     * @param name A {@link String} value.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmKillAll(@NotNull String name) {
        return String.format("killall %s", name);
    }

    /**
     * Command to list all used ports.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmListAllPorts() {
        return "lsof -i";
    }

    /**
     * Command to get a PID process that is listing to a port.
     * @param port An {@link Integer} value.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmFindPID(int port) {
        return String.format("lsof -t -i:%d", port);
    }

    /**
     * Command to stop a PID process.
     * @param pid The PID the process. A {@link String} value.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmKillPID(@NotNull String pid) {
        return String.format("kill %s", pid);
    }

    /**
     * Command to find a process PID using its name.
     * @param name The name of the process. A {@link String} value.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmFindPID(@NotNull String name) {
        return String.format("pgrep %s", name);
    }
    //endregion

    //region Builder
    public static final class Builder {
        @NotNull private final NetworkHandler HANDLER;

        Builder() {
            HANDLER = new NetworkHandler();
        }

        @NotNull
        public NetworkHandler build() {
            return HANDLER;
        }
    }
    //endregion
}
