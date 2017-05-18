package org.swiften.xtestkit.system.network.type;

/**
 * Created by haipham on 5/18/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.string.StringUtil;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.system.network.param.GetProcessNameParam;
import org.swiften.xtestkit.system.process.ProcessRunner;
import org.swiften.xtestkit.system.type.ProcessRunnerHolderType;

import java.util.function.Predicate;

/**
 * This interface provides methods to handle network/system processes.
 */
public interface NetworkHandlerType extends
    ProcessRunnerHolderType,
    NetworkHandlerErrorType
{
    /**
     * Command to kill all instances of a process name.
     * @param name A {@link String} value.
     * @return A {@link String} value.
     */
    @NotNull
    default String cmKillAll(@NotNull String name) {
        return String.format("killall %s", name);
    }

    /**
     * Command to list all used ports.
     * @return A {@link String} value.
     */
    @NotNull
    default String cmListAllPorts() {
        return "lsof -i";
    }

    /**
     * Command to get a PID process that is listing to a port.
     * @param port An {@link Integer} value.
     * @return A {@link String} value.
     */
    @NotNull
    default String cmFindPID(int port) {
        return String.format("lsof -t -i:%d", port);
    }

    /**
     * Command to stop a PID process.
     * @param pid The PID the process. A {@link String} value.
     * @return A {@link String} value.
     */
    @NotNull
    default String cmKillPID(@NotNull String pid) {
        return String.format("kill %s", pid);
    }

    /**
     * Command to find a process PID using its name.
     * @param name The name of the process. A {@link String} value.
     * @return A {@link String} value.
     */
    @NotNull
    default String cmFindPID(@NotNull String name) {
        return String.format("pgrep %s", name);
    }

    /**
     * Find process name using its PID.
     * @param pid A {@link String} value.
     * @return A {@link String} value.
     */
    @NotNull
    default String cmFindProcessName(@NotNull String pid) {
        return String.format("ps -p %s -o comm=", pid);
    }

    /**
     * Get a process' name using its PID value.
     * @param param A {@link T} instance.
     * @param <T> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #processRunner()
     * @see #cmFindProcessName(String)
     * @see ProcessRunner#rxExecute(String)
     * @see T#pid()
     * @see T#retries()
     */
    @NotNull
    default  <T extends PIDIdentifiableType & RetryType>
    Flowable<String> rxGetProcessName(@NotNull T param) {
        ProcessRunner runner = processRunner();
        String command = cmFindProcessName(param.pid());
        return runner.rxExecute(command).retry(param.retries());
    }

    /**
     * Kill a process using its PID value.
     * @param pid A {@link String} value.
     * @return A {@link Flowable} instance.
     * @see #processRunner()
     * @see #cmKillPID(String)
     * @see #NO_SUCH_PROCESS
     */
    @NotNull
    default Flowable<Boolean> rxKillWithPID(@NotNull String pid) {
        ProcessRunner runner = processRunner();
        String command = cmKillPID(pid);

        return runner.rxExecute(command)
            .onErrorResumeNext(t -> {
                /* If 'No such process' is thrown, we skip the error */
                if (t.getMessage().contains(NO_SUCH_PROCESS)) {
                    return Flowable.empty();
                }

                return Flowable.error(t);
            })
            .map(BooleanUtil::toTrue)
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
     * @see #processRunner()
     * @see ProcessRunner#rxExecute(String)
     * @see StringUtil#isNotNullOrEmpty(String)
     * @see #rxGetProcessName(PIDIdentifiableType)
     * @see T#port()
     * @see T#retries()
     */
    @NotNull
    default  <T extends RetryType & PortType>
    Flowable<Boolean> rxKillWithPort(@NotNull final T PARAM,
                                     @NotNull final Predicate<String> NP) {
        return processRunner()
            .rxExecute(cmFindPID(PARAM.port()))
            .filter(StringUtil::isNotNullOrEmpty)
            .map(a -> a.split("\n"))
            .flatMap(Flowable::fromArray)
            .map(a -> GetProcessNameParam.builder()
                .withPID(a)
                .withRetryType(PARAM)
                .build())

            /* Here we have the opportunity to check whether a process can
             * be killed. This can be useful when lsof returns multiple PIDs,
             * one or more of which we do not want to kill */
            .flatMap(gp -> this
                .rxGetProcessName(gp)
                .filter(NP::test)
                .flatMap(a -> this.rxKillWithPID(gp.pid())))
            .defaultIfEmpty(true)
            .retry(PARAM.retries())
            .onErrorReturnItem(true)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }

    /**
     * Kill a process using its name.
     * @param name A {@link String} value.
     * @return A {@link Flowable} instance.
     * @see #processRunner()
     * @see ProcessRunner#rxExecute(String)
     * @see #cmFindPID(String)
     * @see #rxKillWithPID(String)
     * @see BooleanUtil#isTrue(boolean)
     */
    @NotNull
    default Flowable<Boolean> rxKillWithName(@NotNull String name) {
        return processRunner()
            .rxExecute(cmFindPID(name))
            .filter(StringUtil::isNotNullOrEmpty)
            .map(a -> a.split("\n"))
            .flatMap(Flowable::fromArray)
            .flatMap(this::rxKillWithPID)
            .defaultIfEmpty(true)
            .onErrorReturnItem(true)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }

    /**
     * Kill all instances of a process.
     * @param name The process' name. A {@link String} value.
     * @return A {@link Flowable} instance.
     * @see #processRunner()
     * @see ProcessRunner#rxExecute(String)
     * @see #cmKillAll(String)
     * @see BooleanUtil#toTrue(Object)
     */
    @NotNull
    default Flowable<Boolean> rxKillAll(@NotNull String name) {
        return processRunner().rxExecute(cmKillAll(name)).map(BooleanUtil::toTrue);
    }
}