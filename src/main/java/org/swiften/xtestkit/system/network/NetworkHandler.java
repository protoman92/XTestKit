package org.swiften.xtestkit.system.network;

import io.reactivex.BackpressureStrategy;
import io.reactivex.schedulers.Schedulers;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.type.RetryType;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.system.network.type.*;
import org.swiften.xtestkit.system.process.ProcessRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haipham on 4/7/17.
 */
public class NetworkHandler implements NetworkHandlerType {
    @NotNull private static Collection<Integer> USED_PORTS;

    /**
     * This {@link AtomicBoolean} is used when
     * {@link #rxa_checkUntilPortAvailable(PortType)} is called. Basically
     * it only allows for one checking process to run at a time, no matter
     * how many are running in parallel. This is to avoid duplicate ports
     * marked as used.
     */
    @NonNull private static final AtomicBoolean AVAILABLE_TO_POLL_PORT;

    static {
        USED_PORTS = new HashSet<>();
        AVAILABLE_TO_POLL_PORT = new AtomicBoolean(true);
    }

    @NotNull private final ProcessRunner PROCESS_RUNNER;

    public NetworkHandler() {
        PROCESS_RUNNER = new ProcessRunner();
    }

    //region Getters
    /**
     * Return {@link ProcessRunner} instance from {@link #PROCESS_RUNNER}.
     * @return {@link ProcessRunner} instance.
     * @see #PROCESS_RUNNER
     */
    @NotNull
    @Override
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    /**
     * Get all ports that are marked as used in {@link #USED_PORTS}.
     * @return {@link Collection} of {@link Integer}.
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
     * @param port The port to be checked. {@link Integer} value.
     * @return {@link Boolean} value.
     * @see #USED_PORTS
     */
    public synchronized boolean isPortAvailable(int port) {
        return !USED_PORTS.contains(port);
    }

    /**
     * Check if all ports in {@link Collection} has been marked as used.
     * @param ports {@link Collection} of {@link Integer}.
     * @return {@link Boolean} value.
     * @see #USED_PORTS
     */
    public synchronized boolean checkPortsUsed(@NotNull Collection<Integer> ports) {
        return USED_PORTS.containsAll(ports);
    }
    //endregion

    //region Setters
    /**
     * Mark a port as used by adding it to {@link #USED_PORTS}.
     * @param port The port to be marked as used. {@link Integer} value.
     * @see #USED_PORTS
     */
    public synchronized void markPortUsed(int port) {
        USED_PORTS.add(port);
    }

    /**
     * Mark a port as available by removing it from {@link #USED_PORTS}.
     * @param port The port to be marked as available. {@link Integer}
     *             value.
     * @see #USED_PORTS
     */
    public synchronized void markPortAvailable(int port) {
        USED_PORTS.remove(port);
    }
    //endregion

    /**
     * Check if a port is available.
     * @param PARAM {@link T} instance.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #processRunner()
     * @see ProcessRunner#rxa_executeStream(String)
     * @see #cmListAllPorts()
     * @see #isPortAvailable(String, int)
     */
    @NotNull
    public <T extends PortType & RetryType>
    Flowable<Boolean> rxa_checkPortAvailable(@NonNull final T PARAM) {
        final NetworkHandler THIS = this;
        ProcessRunner processRunner = processRunner();
        String command = cmListAllPorts();

        return processRunner
            .rxa_execute(command)
            .map(a -> THIS.isPortAvailable(a, PARAM.port()))
            .retry(PARAM.retries());
    }

    /**
     * Use regular expression to check the output of {@link #cmListAllPorts()}.
     * @param output {@link String} value. This shoudl be the output from
     *               {@link #cmListAllPorts()}.
     * @param port {@link Integer} value.
     * @return {@link Boolean} value.
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
     * @param PARAM {@link P} instance. This {@link P} shall contain the
     *              initial port to be checked.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#isTrue(boolean)
     * @see BooleanUtil#isFalse(boolean)
     * @see P#port()
     * @see P#retries()
     * @see #rxa_checkPortAvailable(PortType)
     * @see #NO_PORT_AVAILABLE
     */
    @NotNull
    public <P extends PortType & MaxPortType & PortStepType & RetryType>
    Flowable<Integer> rxa_checkUntilPortAvailable(@NonNull final P PARAM) {
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
            private Flowable<Integer> check() {
                final int PORT = this.PORT;
                final int STEP = portStep();

                if (PORT < maxPort()) {
                    return THIS.rxa_checkPortAvailable(this)
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

        return Flowable
            .create(o -> {
                new Thread(() -> {
                    for (;;) {
                        if (AVAILABLE_TO_POLL_PORT.getAndSet(false)) {
                            o.onNext(true);
                            o.onComplete();
                            break;
                        }
                    }
                }).start();
            }, BackpressureStrategy.BUFFER)
            .serialize()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map(BooleanUtil::toTrue)
            .flatMap(a -> new CheckPort(PARAM.port()).check())
            .doOnNext(THIS::markPortUsed)
            .doFinally(() -> AVAILABLE_TO_POLL_PORT.set(true));
    }
}
