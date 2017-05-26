package org.swiften.xtestkit.base;

/**
 * Created by haipham on 3/19/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.javautilities.string.StringUtil;
import org.swiften.xtestkit.base.capability.type.CapType;
import org.swiften.xtestkit.base.element.action.checkbox.BaseCheckBoxActionType;
import org.swiften.xtestkit.base.element.action.choice.BaseChoiceSelectorType;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.action.date.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.general.BaseActionType;
import org.swiften.xtestkit.base.element.action.input.BaseInputActionType;
import org.swiften.xtestkit.base.element.action.input.BaseKeyboardActionType;
import org.swiften.xtestkit.base.element.action.password.BasePasswordActionType;
import org.swiften.xtestkit.base.element.action.swipe.BaseSwipeType;
import org.swiften.xtestkit.base.element.action.tap.BaseTapType;
import org.swiften.xtestkit.base.element.action.visibility.BaseVisibilityActionType;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.element.property.BaseElementPropertyType;
import org.swiften.xtestkit.base.type.*;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.system.network.NetworkHandler;
import org.swiften.xtestkit.system.network.type.PortType;
import org.swiften.xtestkit.system.process.ProcessRunner;
import org.swiften.xtestkit.test.TestListenerType;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * A base class for platform-specific implementations. Each Different platform
 * should extend this class and provide its own wrappers for Appium methods.
 */
public abstract class Engine<D extends WebDriver> implements
    BaseActionType<D>,
    BaseClickActionType,
    BaseChoiceSelectorType<D>,
    BaseCheckBoxActionType,
    BaseDateActionType<D>,
    BaseElementPropertyType,
    BaseInputActionType<D>,
    BaseLocatorType<D>,
    BaseKeyboardActionType<D>,
    BasePasswordActionType<D>,
    BaseTapType<D>,
    BaseSwipeType<D>,
    BaseVisibilityActionType<D>,
    EngineErrorType,
    EngineDelayType,
    DistinctiveType,
    TestListenerType
{
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;

    @Nullable private D driver;
    @Nullable CapType capability;
    @Nullable private WeakReference<LocalizerType> localizer;

    @NotNull String browserName;
    @NotNull Address address;
    @NotNull TestMode testMode;

    /**
     * This {@link AtomicBoolean} should be used with
     * {@link #startAppiumOnNewThread(String)} to sequentially start new
     * Appium servers.
     */
    @NotNull private final AtomicBoolean ATOMIC_START_APPIUM;

    public Engine() {
        ATOMIC_START_APPIUM = new AtomicBoolean(false);
        PROCESS_RUNNER = ProcessRunner.builder().build();
        NETWORK_HANDLER = NetworkHandler.builder().build();
        browserName = "";
        testMode = TestMode.SIMULATED;
        address = Address.defaultInstance();
    }

    //region Getters
    public CapType capabilityType() {
        if (ObjectUtil.nonNull(capability)) {
            return capability;
        } else {
            throw new RuntimeException(CAPABILITY_UNAVAILABLE);
        }
    }

    /**
     * Return {@link #address}.
     * @return {@link String} value.
     * @see #address
     */
    @NotNull
    public Address address() {
        return address;
    }

    /**
     * Get the {@link #address()} uri address.
     * @return {@link String} value.
     * @see Address#uri()
     * @see #address
     */
    @NotNull
    public String serverUri() {
        return address().uri();
    }

    /**
     * Return {@link #browserName}.
     * @return {@link String} value.
     * @see #browserName
     */
    @NotNull
    public String browserName() {
        return browserName;
    }

    /**
     * Get the associated {@link org.swiften.xtestkit.base.type.PlatformType}
     * name.
     * @return {@link String} value.
     * @see PlatformType#value()
     * @see #platform()
     */
    @NotNull
    @Override
    public String platformName() {
        return platform().value();
    }

    /**
     * Return {@link #testMode}. This can be stubbed out for custom
     * implementation.
     * @return The specified {@link #testMode} {@link TestMode}.
     */
    @NotNull
    public TestMode testMode() {
        return testMode;
    }

    /**
     * Get the associated {@link LocalizerType} instance.
     * @return {@link LocalizerType} instance.
     * @see ObjectUtil#nonNull(Object)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public LocalizerType localizer() {
        WeakReference<LocalizerType> weak = localizer;
        LocalizerType ref;

        if (ObjectUtil.nonNull(weak) && ObjectUtil.nonNull((ref = weak.get()))) {
            return ref;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Return {@link #PROCESS_RUNNER}. This method can be used to stub out
     * {@link #PROCESS_RUNNER}.
     * @return {@link #PROCESS_RUNNER}.
     * @see #PROCESS_RUNNER
     */
    @NotNull
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    /**
     * Return {@link #NETWORK_HANDLER}. This method can be used to stub out
     * {@link #NETWORK_HANDLER}.
     * @return {@link #NETWORK_HANDLER}.
     * @see #NETWORK_HANDLER
     */
    @NotNull
    public NetworkHandler networkHandler() {
        return NETWORK_HANDLER;
    }

    /**
     * Get the active {@link D} {@link #driver}.
     * @return {@link D} {@link #driver}.
     * @see ObjectUtil#nonNull(Object)
     * @see #driver
     */
    @NotNull
    public D driver() {
        if (ObjectUtil.nonNull(driver)) {
            return driver;
        } else {
            throw new RuntimeException(DRIVER_UNAVAILABLE);
        }
    }

    /**
     * Get page source from {@link WebDriver}.
     * @return {@link String} value.
     * @see #driver()
     * @see WebDriver#getPageSource()
     */
    @NotNull
    public String pageSource() {
        return driver().getPageSource();
    }

    /**
     * Get the associated {@link PlatformView}.
     * @return {@link PlatformView} instance.
     */
    @NotNull
    public abstract PlatformView platformView();
    //endregion

    //region Setters
    /**
     * Set {@link #localizer}. Usually this is set when {@link Engine} is
     * added to {@link org.swiften.xtestkit.kit.TestKit}.
     * @param delegate {@link LocalizerType} instance.
     * @see #localizer
     */
    public void setLocalizer(@NotNull LocalizerType delegate) {
        localizer = new WeakReference<>(delegate);
    }
    //endregion

    //region DistinctiveType
    /**
     * This should be used with {@link Flowable#distinct(Function)}.
     * @return {@link Object} instance.
     * @see Flowable#distinct(Function)
     */
    @NotNull
    @Override
    public Object comparisonObject() {
        return getClass();
    }
    //endregion

    //region TestListenerType
    @NotNull
    @Override
    public Flowable<Boolean> rx_onFreshStart() {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rx_onBatchStarted(@NotNull final int[] INDEXES) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rx_onBatchFinished(@NotNull final int[] INDEXES) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rx_onAllTestsFinished() {
        return Flowable.just(true);
    }
    //endregion

    //region Test Setup
    /**
     * Convenience method for {@link org.testng.annotations.BeforeClass}.
     * This method will be called by
     * {@link org.swiften.xtestkit.kit.TestKit#rxBeforeClass(BeforeClassParam)}.
     * Subclasses of {@link Engine} should provide their own
     * implementations.
     * @param param {@link BeforeClassParam} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rx_beforeClass(@NotNull BeforeClassParam param) {
        if (address().isLocalInstance()) {
            return rx_startLocalAppium(param);
        }

        return Flowable.just(true);
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterClass}.
     * This method will be called by
     * {@link org.swiften.xtestkit.kit.TestKit#rxAfterClass(AfterClassParam)}.
     * Subclasses of {@link Engine} should provide their own
     * implementations.
     * @param param {@link AfterClassParam} instance.
     * @return {@link Flowable} instance.
     * @see NetworkHandler#markPortAsAvailable(int)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rx_afterClass(@NotNull AfterClassParam param) {
        NetworkHandler HANDLER = networkHandler();
        Address ADDRESS = address();

        Flowable<Boolean> reusePort = Completable
            .fromAction(() -> HANDLER.markPortAsAvailable(ADDRESS.port()))
            .toFlowable()
            .map(BooleanUtil::toTrue)
            .defaultIfEmpty(true);

        Flowable<Boolean> stopServer;

        if (ADDRESS.isLocalInstance()) {
            stopServer = rx_stopLocalAppium();
        } else {
            stopServer = Flowable.just(true);
        }

        return Flowable
            .concatArray(reusePort, stopServer)
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }

    /**
     * Convenience method for {@link org.testng.annotations.BeforeMethod}.
     * This method will be called by
     * {@link org.swiften.xtestkit.kit.TestKit#rxBeforeMethod(BeforeParam)}.
     * Subclasses of {@link Engine} should provide their own
     * implementations.
     * @param param {@link BeforeParam} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rx_beforeMethod(@NotNull BeforeParam param) {
        return Flowable.just(true);
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterMethod}.
     * This method will be called by
     * {@link org.swiften.xtestkit.kit.TestKit#rxAfterMethod(AfterParam)}.
     * Subclasses of {@link Engine} should provide their own
     * implementations.
     * @param param {@link AfterParam} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rx_afterMethod(@NotNull AfterParam param) {
        return Flowable.just(true);
    }

    /**
     * Start appium with a specified {@link #serverUri()}.
     * @param PARAM {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see #cm_whichAppium()
     * @see #cm_fallBackAppium()
     * @see #appiumStartDelay()
     * @see #startAppiumOnNewThread(String)
     * @see #processRunner()
     * @see BooleanUtil#toTrue(Object)
     * @see RetryType#retries()
     */
    @NotNull
    public Flowable<Boolean> rx_startLocalAppium(@NotNull final RetryType PARAM) {
        final ProcessRunner RUNNER = processRunner();
        String whichAppium = cm_whichAppium();
        long delay = appiumStartDelay();

        return RUNNER.rxExecute(whichAppium)
            .filter(StringUtil::isNotNullOrEmpty)
            .map(a -> a.replace("\n", ""))
            .switchIfEmpty(RxUtil.error(APPIUM_NOT_INSTALLED))
            .onErrorReturnItem(cm_fallBackAppium())
            .doOnNext(this::startAppiumOnNewThread)
            .map(BooleanUtil::toTrue)
            .delay(delay, TimeUnit.MILLISECONDS)
            .retry(PARAM.retries());
    }

    /**
     * Start a new local Appium instance. This will be run in a different
     * thread.
     * @param CLI The path to Appium CLI. {@link String} value.
     * @see #cm_startLocalAppium(String, int)
     * @see NetworkHandler#rxCheckUntilPortAvailable(PortType)
     * @see Address#setPort(int)
     */
    @SuppressWarnings("unchecked")
    public void startAppiumOnNewThread(@NotNull final String CLI) {
        final ProcessRunner RUNNER = processRunner();
        final Address ADDRESS = address();
        final NetworkHandler NETWORK_HANDLER = networkHandler();

        NETWORK_HANDLER.rxCheckUntilPortAvailable(ADDRESS)
            .doOnNext(ADDRESS::setPort)
            .doOnNext(a -> {
                final String COMMAND = cm_startLocalAppium(CLI, a);

                new Thread(() -> {
                    for (;;) {
                        if (!ATOMIC_START_APPIUM.get()) {
                            ATOMIC_START_APPIUM.getAndSet(true);

                            /* Need to start on a new Thread, or else it will
                             * block */
                            new Thread(() -> {
                                try {
                                    RUNNER.execute(COMMAND);
                                } catch (Exception e) {
                                    LogUtil.println(e);
                                }
                            }).start();

                            /* Sleep for a while to straddle the initialization
                             * of Appium servers */
                            try {
                                TimeUnit.MILLISECONDS.sleep(2000);
                            } catch (InterruptedException e) {
                                LogUtil.println(e);
                            } finally {
                                ATOMIC_START_APPIUM.getAndSet(false);
                            }

                            break;
                        }
                    }
                }).start();
            })
            .map(BooleanUtil::toTrue)
            .subscribe();
    }

    /**
     * Stop all local appium instances.
     * @return {@link Flowable} instance.
     * @see NetworkHandler#rxKillWithPort(RetryType, Predicate)
     */
    @NotNull
    public Flowable<Boolean> rx_stopLocalAppium() {
        NetworkHandler handler = networkHandler();
        Address address = address();
        return handler.rxKillWithPort(address, this::isAppiumProcess);
    }

    /**
     * Check if a process is potentially an Appium instance. This is used to
     * detect whether it should be killed by
     * {@link #rx_stopLocalAppium()}
     * @param name The process' name. {@link String} value.
     * @return {@link Boolean} instance.
     * @see #rx_stopLocalAppium()
     */
    public boolean isAppiumProcess(@NotNull String name) {
        return name.contains("node");
    }
    //endregion

    //region CLI commands
    /**
     * Command to detect where appium is installed.
     * @return {@link String} value.
     */
    @NotNull
    public String cm_whichAppium() {
        return "which appium";
    }

    /**
     * Fall back Appium path if {@link #cm_whichAppium()} fails.
     * @return {@link String} value.
     */
    @NotNull
    public String cm_fallBackAppium() {
        return "/usr/local/bin/appium";
    }

    /**
     * Command to start an Appium instance.
     * @param cli The path to Appium CLI. {@link String} value.
     * @param port The port to be used to start a new Appium instance. An
     *             {@link Integer} value.
     * @return {@link String} value.
     */
    @NotNull
    public String cm_startLocalAppium(@NotNull String cli, int port) {
        return AppiumCommand.builder().withBase(cli).withPort(port).build().command();
    }
    //endregion

    //region Appium Setup
    /**
     * Get {@link Map} of capabilities to pass to Appium driver.
     * @return {@link Map} instance.
     */
    @NotNull
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = new HashMap<String,Object>();
        capabilities.put(CapabilityType.BROWSER_NAME, browserName());
        return capabilities;
    }
    //endregion

    //region Driver Methods
    /**
     * Create {@link D} instance in order to navigate UI test.
     * @return {@link D} instance.
     */
    @NotNull
    protected D driver(@NotNull String serverUrl, @NotNull DesiredCapabilities caps) {
        throw new RuntimeException(DRIVER_UNAVAILABLE);
    }

    /**
     * Start the Appium driver. If {@link CapType#isComplete(Map)}
     * returns false, throw {@link Exception}.
     * @param PARAM {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see CapType#isComplete(Map)
     * @see CapType#distill(Map)
     * @see #driver(String, DesiredCapabilities)
     */
    @NotNull
    public Flowable<Boolean> rx_startDriver(@NotNull final RetryType PARAM) {
        CapType capType = capabilityType();
        Map<String,Object> caps = capabilities();

        if (capType.isComplete(caps)) {
            final Map<String,Object> distilled = capType.distill(caps);
            final DesiredCapabilities CAPS = new DesiredCapabilities(distilled);
            final String SERVER_URL = serverUri();
            LogUtil.println(distilled);

            return Completable
                .fromAction(() -> driver = driver(SERVER_URL, CAPS))
                .<Boolean>toFlowable()
                .defaultIfEmpty(true)
                .retry(PARAM.retries());
        } else {
            return RxUtil.error(INSUFFICIENT_SETTINGS);
        }
    }

    /**
     * Quit the active Appium driver. If it is null, throw {@link Exception}
     * instead.
     * @return {@link Flowable} instance.
     * @see WebDriver#quit()
     */
    @NotNull
    public Flowable<Boolean> rx_stopDriver() {
        return Completable.fromAction(driver()::quit)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link Engine}.
     * @param <T> Generics parameter that extends {@link Engine}.
     */
    public static abstract class Builder<T extends Engine> {
        @NotNull final protected T ENGINE;
        @NotNull final protected CapType.Builder CAP_BUILDER;

        protected Builder(@NotNull T engine,
                          @NotNull CapType.Builder capBuilder) {
            ENGINE = engine;
            CAP_BUILDER = capBuilder;
        }

        /**
         * Set the {@link #ENGINE#browserName} value. This is useful for
         * Web app testing.
         * @param name {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withBrowserName(@NotNull String name) {
            ENGINE.browserName = name;
            return this;
        }

        /**
         * Set the {@link #ENGINE#address}. This {@link String} represents
         * the Appium server address.
         * @param address {@link Address} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withServerUrl(@NotNull Address address) {
            ENGINE.address = address;
            return this;
        }

        /**
         * Set the {@link #ENGINE#testMode} value. This variable specifies
         * which test environment to be used.
         * @param mode {@link TestMode} instance.
         * @return The current {@link Builder} instance.
         * @see CapType.Builder#withTestMode(TestMode)
         */
        @NotNull
        public Builder<T> withTestMode(@NotNull TestMode mode) {
            ENGINE.testMode = mode;
            CAP_BUILDER.withTestMode(mode);
            return this;
        }

        @NotNull
        public T build() {
            ENGINE.capability = CAP_BUILDER.build();
            return ENGINE;
        }
    }
    //endregion
}