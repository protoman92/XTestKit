package com.swiften.xtestkit.system;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.system.param.GetProcessNameParam;
import com.swiften.xtestkit.system.protocol.PIDProtocol;
import com.swiften.xtestkit.system.protocol.PortProtocol;
import com.swiften.xtestkit.system.protocol.ProcessRunnerProtocol;
import com.swiften.xtestkit.system.protocol.NetworkHandlerError;
import com.swiften.xtestkit.util.BooleanUtil;
import com.swiften.xtestkit.util.LogUtil;
import com.swiften.xtestkit.util.StringUtil;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;
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

    //region Getters
    /**
     * Return a {@link ProcessRunnerProtocol} instance from
     * {@link #PROCESS_RUNNER}.
     * @return A {@link ProcessRunnerProtocol} instance.
     */
    @NotNull
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    /**
     * Check if a port has yet to be marked as used.
     * @param port The port to be checked. An {@link Integer} value.
     * @return A {@link Boolean} value.
     */
    public boolean isPortAvailable(int port) {
        return !USED_PORTS.contains(port);
    }

    /**
     * Check if all ports in a {@link Collection} has been marked as used.
     * @param ports A {@link Collection} of {@link Integer}.
     * @return A {@link Boolean} value.
     */
    public boolean checkPortsMarkedAsUsed(@NotNull Collection<Integer> ports) {
        return USED_PORTS.containsAll(ports);
    }
    //endregion

    //region Setters
    /**
     * Mark a port as used by adding it to {@link #USED_PORTS}.
     * @param port The port to be marked as used. An {@link Integer} value.
     */
    public synchronized void markPortAsUsed(int port) {
        USED_PORTS.add(port);
    }

    /**
     * Mark a port as available by removing it from {@link #USED_PORTS}.
     * @param port The port to be marked as available. An {@link Integer}
     *             value.
     */
    public synchronized void markPortAsAvailable(int port) {
        USED_PORTS.remove(port);
    }
    //endregion

    /**
     * Check if a port is available.
     * @param PARAM A {@link T} instance.
     * @param <T> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #cmListAllPorts()
     * @see #isPortAvailable(String, int)
     */
    @NotNull
    public <T extends PortProtocol & RetryProtocol>
    Flowable<Boolean> rxCheckPortAvailable(@NonNull final T PARAM) {
        ProcessRunnerProtocol processRunner = processRunner();
        String command = cmListAllPorts();

        return processRunner
            .rxExecute(command)
            .map(a -> isPortAvailable(a, PARAM.port()))
            .retry(PARAM.retries());
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
        return !matcher.find();
    }

    /**
     * Get a process' name using its PID value.
     * @param param A {@link T} instance.
     * @param <T> Generics parameter.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public <T extends PIDProtocol & RetryProtocol>
    Flowable<String> rxGetProcessName(@NotNull T param) {
        ProcessRunner runner = processRunner();
        String command = cmFindProcessName(param.pid());
        return runner.rxExecute(command).retry(param.retries());
    }

    /**
     * Recursively check ports until one is available. Everytime a port is
     * not available, we increment it by one and call this method again.
     * @param PARAM A {@link T} instance. This {@link T} shall contain the
     *              initial port to be checked.
     * @param <T> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxCheckPortAvailable(PortProtocol)
     */
    @NotNull
    public <T extends PortProtocol & RetryProtocol>
    Flowable<Integer> rxCheckUntilPortAvailable(@NonNull final T PARAM) {
        return rxCheckPortAvailable(PARAM)
            /* If we use filter() and switchIfEmpty() here, a StackOverflow
             * error will be thrown. The solution below may not look as nice
             * but it works correctly */
            .flatMap(a -> {
                if (BooleanUtil.isTrue(a)) {
                    LogUtil.printf("Port %d is currently available", PARAM.port());
                    return Flowable.just(PARAM.port());
                }

                /* Create a temporary parameter class to check a new port */
                class Param implements PortProtocol, RetryProtocol {
                    @Override
                    public int port() {
                        return PARAM.port() + 1;
                    }

                    @Override
                    public int retries() {
                        return PARAM.retries();
                    }
                }

                /* Keep checking ports until one is available */
                return rxCheckUntilPortAvailable(new Param());
            });
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

        return runner
            .rxExecute(command)
            .onErrorResumeNext(t -> {
                /* If 'No such process' is thrown, we skip the error */
                if (t.getMessage().contains(NO_SUCH_PROCESS)) {
                    return Flowable.empty();
                }

                return Flowable.error(t);
            })
            .map(a -> true)
            .defaultIfEmpty(true);
    }

    /**
     * Kill a process that is listening to a port.
     * @param PARAM A {@link T} instance.
     * @param NP A {@link Predicate} instance that checks whether a process
     *           should be terminated. It accepts a {@link String} that
     *           represents the process' name.
     * @param <T> Generics parameter.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public <T extends RetryProtocol & PortProtocol>
    Flowable<Boolean> rxKillProcessWithPort(@NotNull final T PARAM,
                                            @NotNull final Predicate<String> NP) {
        return processRunner()
            .rxExecute(cmFindPID(PARAM.port()))
            .filter(StringUtil::isNotNullOrEmpty)
            .map(a -> a.split("\n"))
            .flatMap(Flowable::fromArray)
            .map(a -> GetProcessNameParam.builder()
                .withPID(a)
                .withRetryProtocol(PARAM)
                .build())

            /* Here we have the opportunity to check whether a process can
             * be killed. This can be useful when lsof returns multiple PIDs,
             * one or more of which we do not want to kill */
            .flatMap(gp -> this
                .rxGetProcessName(gp)
                .filter(NP::test)
                .flatMap(a -> this.rxKillProcessWithPid(gp.pid())))
            .defaultIfEmpty(true)
            .retry(PARAM.retries())
            .onErrorResumeNext(Flowable.just(true))
            .all(BooleanUtil::isTrue)
            .toFlowable();
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
            .map(a -> a.split("\n"))
            .flatMap(Flowable::fromArray)
            .flatMap(this::rxKillProcessWithPid)
            .defaultIfEmpty(true)
            .onErrorResumeNext(Flowable.just(true))
            .all(BooleanUtil::isTrue)
            .toFlowable();
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

    /**
     * Find process name using its PID.
     * @param pid A {@link String} value.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmFindProcessName(@NotNull String pid) {
        return String.format("ps -p %s -o comm=", pid);
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
