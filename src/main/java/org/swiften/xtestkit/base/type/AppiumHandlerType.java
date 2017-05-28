package org.swiften.xtestkit.base.type;

/**
 * Created by haipham on 5/28/17.
 */

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.javautilities.string.StringUtil;
import org.swiften.xtestkit.base.Address;
import org.swiften.xtestkit.base.AppiumCommand;
import org.swiften.xtestkit.system.network.NetworkHandler;
import org.swiften.xtestkit.system.network.type.NetworkHandlerHolderType;
import org.swiften.xtestkit.system.network.type.PortType;
import org.swiften.xtestkit.system.process.ProcessRunner;
import org.swiften.xtestkit.system.process.ProcessRunnerHolderType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This interface provides methods to initialize Appium, possible on multiple
 * threads.
 */
public interface AppiumHandlerType extends
    AddressHolderType,
    NetworkHandlerHolderType,
    ProcessRunnerHolderType
{
    /**
     * This {@link AtomicBoolean} should be used with
     * {@link #rxa_startAppiumOnNewThread(String)} to sequentially start new
     * Appium servers.
     */
    @NotNull AtomicBoolean AVAILABLE_TO_START_APPIUM = new AtomicBoolean(true);

    /**
     * Command to detect where appium is installed.
     * @return {@link String} value.
     */
    @NotNull
    default String cm_whichAppium() {
        return "which appium";
    }

    /**
     * Fall back Appium path if {@link #cm_whichAppium()} fails.
     * @return {@link String} value.
     */
    @NotNull
    default String cm_fallBackAppium() {
        return "/usr/local/bin/appium";
    }

    /**
     * Check if a process is potentially an Appium instance. This is used to
     * detect whether it should be killed by
     * {@link #rx_stopLocalAppium()}
     * @param name The process' name. {@link String} value.
     * @return {@link Boolean} instance.
     * @see #rx_stopLocalAppium()
     */
    default boolean isAppiumProcess(@NotNull String name) {
        return name.contains("node");
    }

    /**
     * Command to start an Appium instance.
     * @param cli The path to Appium CLI. {@link String} value.
     * @param port The port to be used to start a new Appium instance. An
     *             {@link Integer} value.
     * @return {@link String} value.
     */
    @NotNull
    default String cm_startLocalAppium(@NotNull String cli, int port) {
        return AppiumCommand.builder().withBase(cli).withPort(port).build().command();
    }

    /**
     * Start appium with a specified server uri.
     * @param param {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#toTrue(Object)
     * @see RetryType#retries()
     * @see StringUtil#isNotNullOrEmpty(String)
     * @see #processRunner()
     * @see #rxa_startAppiumOnNewThread(String)
     * @see #cm_whichAppium()
     * @see #cm_fallBackAppium()
     */
    @NotNull
    default Flowable<Boolean> rxa_startLocalAppium(@NotNull RetryType param) {
        final AppiumHandlerType THIS = this;
        final ProcessRunner RUNNER = processRunner();
        String whichAppium = cm_whichAppium();

        return RUNNER.rxa_execute(whichAppium)
            .filter(StringUtil::isNotNullOrEmpty)
            .map(a -> a.replace("\n", ""))
            .switchIfEmpty(RxUtil.error())

            /* No matter the error, we want to use the fallback command to
             * try starting the Appium server */
            .onErrorReturnItem(cm_fallBackAppium())
            .flatMap(THIS::rxa_startAppiumOnNewThread)
            .map(BooleanUtil::toTrue)
            .retry(param.retries());
    }

    /**
     * Start a new local Appium instance. This will be run in a different
     * thread.
     * @param CLI The path to Appium CLI. {@link String} value.
     * @return {@link Flowable} instance.
     * @see Address#setPort(int)
     * @see BooleanUtil#toTrue(Object)
     * @see BooleanUtil#isFalse(boolean)
     * @see NetworkHandler#rxa_checkUntilPortAvailable(PortType)
     * @see ProcessRunner#execute(String, Consumer, Consumer)
     * @see #address()
     * @see #networkHandler()
     * @see #processRunner()
     * @see #cm_startLocalAppium(String, int)
     * @see #AVAILABLE_TO_START_APPIUM
     */
    @SuppressWarnings("unchecked")
    default Flowable<Boolean> rxa_startAppiumOnNewThread(@NotNull final String CLI) {
        final AppiumHandlerType THIS = this;
        final ProcessRunner RUNNER = processRunner();
        final Address ADDRESS = address();
        final NetworkHandler NETWORK_HANDLER = networkHandler();

        return NETWORK_HANDLER.rxa_checkUntilPortAvailable(ADDRESS)
            .doOnNext(ADDRESS::setPort)
            .flatMap(a -> Flowable.<Boolean>create(o -> {
                final String COMMAND = THIS.cm_startLocalAppium(CLI, a);

                new Thread(() -> {
                    for (;;) {
                        if (AVAILABLE_TO_START_APPIUM.getAndSet(false)) {
                            LogUtil.println("Executing", COMMAND);
                            /* We need to start a new thread because this
                             * operation blocks */
                            new Thread(() -> {
                                Consumer<String> cs = LogUtil::println;
                                RUNNER.execute(COMMAND, cs, o::onError);
                            }).start();

                            /* Sleep for a while to straddle the initialization
                             * of Appium instances */
                            try {
                                TimeUnit.MILLISECONDS.sleep(5000);
                            } catch (InterruptedException e) {
                                o.onError(e);
                            } finally {
                                AVAILABLE_TO_START_APPIUM.set(true);
                                o.onNext(true);
                                o.onComplete();
                            }

                            break;
                        }
                    }
                }).start();
            }, BackpressureStrategy.BUFFER))
            .serialize();
    }

    /**
     * Stop all local appium instances.
     * @return {@link Flowable} instance.
     * @see NetworkHandler#rxa_killWithPort(RetryType, Predicate)
     * @see #isAppiumProcess(String)
     */
    @NotNull
    default Flowable<Boolean> rx_stopLocalAppium() {
        NetworkHandler handler = networkHandler();
        Address address = address();
        return handler.rxa_killWithPort(address, this::isAppiumProcess);
    }
}