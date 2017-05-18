package org.swiften.xtestkit.system.network;

import io.reactivex.schedulers.Schedulers;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.type.RetryType;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.string.StringUtil;
import org.swiften.xtestkit.system.network.type.*;
import org.swiften.xtestkit.system.process.ProcessRunner;
import org.swiften.xtestkit.system.network.param.GetProcessNameParam;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haipham on 4/7/17.
 */
public class NetworkHandler implements NetworkHandlerType {
    @NotNull private static Collection<Integer> USED_PORTS;

    static {
        USED_PORTS = new HashSet<>();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final ProcessRunner PROCESS_RUNNER;

    /**
     * This {@link AtomicBoolean} is used when
     * {@link #rxCheckUntilPortAvailable(PortType)} is called. Basically
     * it only allows for one checking process to run at a time, no matter
     * how many are running in parallel. This is to avoid duplicate ports
     * marked as used.
     */
    @NonNull private final AtomicBoolean ATOMIC_PORT_FLAG;

    NetworkHandler() {
        PROCESS_RUNNER = ProcessRunner.builder().build();
        ATOMIC_PORT_FLAG = new AtomicBoolean(false);
    }

    //region Getters
    /**
     * Return a {@link ProcessRunner} instance from {@link #PROCESS_RUNNER}.
     * @return A {@link ProcessRunner} instance.
     * @see #PROCESS_RUNNER
     */
    @NotNull
    @Override
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    /**
     * Get all ports that are marked as used in {@link #USED_PORTS}.
     * @return A {@link Collection} of {@link Integer}.
     * @see Collections#unmodifiableCollection(Collection)
     * @see #USED_PORTS
     */
    @NotNull
    public synchronized Collection<Integer> usedPorts() {
        return Collections.unmodifiableCollection(USED_PORTS);
    }

    /**
     * Clear all used ports.
     * @see #USED_PORTS
     */
    public synchronized void clearUsedPorts() {
        USED_PORTS.clear();
    }

    /**
     * Check if a port has yet to be marked as used.
     * @param port The port to be checked. An {@link Integer} value.
     * @return A {@link Boolean} value.
     * @see #USED_PORTS
     */
    public boolean isPortAvailable(int port) {
        return !USED_PORTS.contains(port);
    }

    /**
     * Check if all ports in a {@link Collection} has been marked as used.
     * @param ports A {@link Collection} of {@link Integer}.
     * @return A {@link Boolean} value.
     * @see #USED_PORTS
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
     * @see #USED_PORTS
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
     * @see #processRunner()
     * @see ProcessRunner#rxExecute(String)
     * @see #cmListAllPorts()
     * @see #isPortAvailable(String, int)
     */
    @NotNull
    public <T extends PortType & RetryType>
    Flowable<Boolean> rxCheckPortAvailable(@NonNull final T PARAM) {
        final NetworkHandler THIS = this;
        ProcessRunner processRunner = processRunner();
        String command = cmListAllPorts();

        return processRunner
            .rxExecute(command)
            .map(a -> THIS.isPortAvailable(a, PARAM.port()))
            .retry(PARAM.retries());
    }

    /**
     * Use regular expression to check the output of {@link #cmListAllPorts()}.
     * @param output A {@link String} value. This shoudl be the output from
     *               {@link #cmListAllPorts()}.
     * @param port An {@link Integer} value.
     * @return A {@link Boolean} value.
     * @see #USED_PORTS
     */
    public synchronized boolean isPortAvailable(@NotNull String output, int port) {
        if (USED_PORTS.contains(port)) {
            return false;
        } else {

            /* The output we are looking for is *.${PORT} (LISTEN).
             * E.g., *.4723 (LISTEN) */
            String regex = String.format("\\*.%d( \\(LISTEN\\))?", port);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(output);
            return !matcher.find();
        }
    }

    /**
     * Recursively check ports until one is available. Everytime a port is
     * not available, we increment it by one and call this method again.
     * @param PARAM A {@link P} instance. This {@link P} shall contain the
     *              initial port to be checked.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxCheckPortAvailable(PortType)
     * @see P#port()
     * @see P#retries()
     */
    @NotNull
    public <P extends PortType & MaxPortType & PortStepType & RetryType>
    Flowable<Integer> rxCheckUntilPortAvailable(@NonNull final P PARAM) {
        final NetworkHandler THIS = this;

        /* Temporary param that handles both port values and checks */
        class CheckPort implements PortType, MaxPortType, PortStepType, RetryType {
            private final int PORT;

            @SuppressWarnings("WeakerAccess")
            CheckPort(int port) {
                PORT = port;
            }

            @Override
            public int port() {
                return PORT;
            }

            @Override
            public int maxPort() {
                return PARAM.maxPort();
            }

            @Override
            public int portStep() {
                return PARAM.portStep();
            }

            @Override
            public int retries() {
                return PARAM.retries();
            }

            @NotNull
            @SuppressWarnings("WeakerAccess")
            Flowable<Integer> check() {
                final int PORT = this.PORT;
                final int STEP = portStep();

                if (PORT < maxPort()) {
                    return THIS.rxCheckPortAvailable(this)
                        .flatMap(a -> {
                            if (BooleanUtil.isTrue(a)) {
                                return Flowable.just(PORT);
                            } else {
                                int newPort = PORT + STEP;
                                CheckPort newParam = new CheckPort(newPort);
                                return newParam.check();
                            }
                        });
                } else {
                    return RxUtil.error(NO_PORT_AVAILABLE);
                }
            }
        }

        return Flowable.just(ATOMIC_PORT_FLAG)
            .observeOn(Schedulers.computation())
            .map(AtomicBoolean::get)
            .filter(BooleanUtil::isFalse)
            .switchIfEmpty(RxUtil.error())
            .retry()
            .doOnNext(a -> ATOMIC_PORT_FLAG.getAndSet(true))
            .flatMap(a -> new CheckPort(PARAM.port()).check())
            .doOnNext(THIS::markPortAsUsed)
            .doFinally(() -> ATOMIC_PORT_FLAG.getAndSet(false));
    }

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
