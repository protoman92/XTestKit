package org.swiften.xtestkit.engine.base;

/**
 * Created by haipham on 3/19/17.
 */

import org.openqa.selenium.*;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.engine.base.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.engine.base.action.general.type.BaseActionType;
import org.swiften.xtestkit.engine.base.capability.CapType;
import org.swiften.xtestkit.engine.base.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.engine.base.type.*;
import org.swiften.xtestkit.engine.mobile.*;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.TestKit;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.system.NetworkHandler;
import org.swiften.xtestkit.system.ProcessRunner;
import org.swiften.xtestkit.test.type.TestListenerType;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.string.StringUtil;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * A base class for platform-specific implementations. Each Different platform
 * should extend this class and provide its own wrappers for Appium methods.
 */
public abstract class BaseEngine<D extends WebDriver> implements
    BaseActionType<D>,
    BaseDateActionType<D>,
    BaseLocatorType<D>,
    BaseEngineErrorType,
    EngineDelayType,
    DistinctiveType,
    TestListenerType
{
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;

    @Nullable private WeakReference<LocalizerType> textDelegate;

    @Nullable private D driver;
    @Nullable PlatformView platformView;
    @Nullable
    CapType capability;

    @NotNull String browserName;
    @NotNull String platformName;
    @NotNull ServerAddress serverAddress;
    @NotNull TestMode testMode;

    public BaseEngine() {
        PROCESS_RUNNER = ProcessRunner.builder().build();
        NETWORK_HANDLER = NetworkHandler.builder().build();
        browserName = "";
        platformName = "";
        testMode = TestMode.SIMULATED;
        serverAddress = ServerAddress.defaultInstance();
    }

    //region DistinctiveType
    /**
     * This should be used with {@link Flowable#distinct(Function)}.
     * @return An {@link Object} instance.
     * @see Flowable#distinct(Function)
     */
    @NotNull
    @Override
    public Object getComparisonObject() {
        return getClass();
    }
    //endregion

    //region TestListenerType
    @NotNull
    @Override
    public Flowable<Boolean> rxOnFreshStart() {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxOnBatchStarted(@NotNull final int[] INDEXES) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxOnBatchFinished(@NotNull final int[] INDEXES) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxOnAllTestsFinished() {
        return Flowable.just(true);
    }
    //endregion

    //region Test Setup
    /**
     * Convenience method for {@link org.testng.annotations.BeforeClass}.
     * This method will be called by
     * {@link TestKit#rxBeforeClass(BeforeClassParam)}.
     * Subclasses of {@link BaseEngine} should provide their own
     * implementations.
     * @param param A {@link BeforeClassParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
        if (serverAddress().isLocalInstance()) {
            return rxStartLocalAppium(param);
        }

        return Flowable.just(true);
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterClass}.
     * This method will be called by
     * {@link TestKit#rxAfterClass(AfterClassParam)}.
     * Subclasses of {@link BaseEngine} should provide their own
     * implementations.
     * @param param An {@link AfterClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see NetworkHandler#markPortAsAvailable(int)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        NetworkHandler HANDLER = networkHandler();
        ServerAddress ADDRESS = serverAddress();

        Flowable<Boolean> reusePort = Completable
            .fromAction(() -> HANDLER.markPortAsAvailable(ADDRESS.port()))
            .toFlowable()
            .map(a -> true)
            .defaultIfEmpty(true);

        Flowable<Boolean> stopServer;

        if (ADDRESS.isLocalInstance()) {
            stopServer = rxStopLocalAppiumInstance();
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
     * This method will be
     * called by {@link TestKit#rxBeforeMethod(BeforeParam)}.
     * Subclasses of {@link BaseEngine} should provide their own
     * implementations.
     * @param param A {@link BeforeParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxBeforeMethod(@NotNull BeforeParam param) {
        return Flowable.just(true);
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterMethod}.
     * This method will be called by {@link TestKit#rxAfterMethod(AfterParam)}.
     * Subclasses of {@link BaseEngine} should provide their own
     * implementations.
     * @param param A {@link AfterParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxAfterMethod(@NotNull AfterParam param) {
        return Flowable.just(true);
    }

    /**
     * Start appium with a specified {@link #serverUri()}.
     * @param PARAM A {@link RetryType} instance.
     * @return A {@link Flowable} instance.
     * @see #cmWhichAppium()
     * @see #processRunner()
     */
    @NotNull
    public Flowable<Boolean> rxStartLocalAppium(@NotNull final RetryType PARAM) {
        final ProcessRunner RUNNER = processRunner();
        String whichAppium = cmWhichAppium();

        return RUNNER.rxExecute(whichAppium)
            .filter(StringUtil::isNotNullOrEmpty)
            .map(a -> a.replace("\n", ""))
            .switchIfEmpty(Flowable.error(new Exception(APPIUM_NOT_INSTALLED)))
            .onErrorReturnItem(cmFallBackAppium())
            .doOnNext(this::startAppiumOnNewThread)
            .map(a -> true)
            .delay(appiumStartDelay(), TimeUnit.MILLISECONDS)
            .retry(PARAM.retries());
    }

    /**
     * Start a new local Appium instance. This will be run in a different
     * thread.
     * @param CLI The path to Appium CLI. A {@link String} value.
     * @see #cmStartLocalAppiumInstance(String, int)
     */
    @SuppressWarnings("unchecked")
    public void startAppiumOnNewThread(@NotNull final String CLI) {
        final ProcessRunner RUNNER = processRunner();
        final ServerAddress ADDRESS = serverAddress();
        final NetworkHandler NETWORK_HANDLER = networkHandler();

        NETWORK_HANDLER.rxCheckUntilPortAvailable(ADDRESS)
            .doOnNext(NETWORK_HANDLER::markPortAsUsed)
            .doOnNext(ADDRESS::setPort)
            .doOnNext(a -> {
                final String COMMAND = cmStartLocalAppiumInstance(CLI, a);

                /* Need to start on a new Thread, or else it will block */
                new Thread(() -> {
                    try {
                        RUNNER.execute(COMMAND);
                    } catch (Exception e) {
                        LogUtil.println(e);
                    }
                }).start();
            })
            .map(a -> true)
            .subscribe();
    }

    /**
     * Stop all local appium instances.
     * @return A {@link Flowable} instance.
     * @see NetworkHandler#rxKillProcessWithPort(RetryType, Predicate)
     */
    @NotNull
    public Flowable<Boolean> rxStopLocalAppiumInstance() {
        NetworkHandler handler = networkHandler();
        ServerAddress address = serverAddress();
        return handler.rxKillProcessWithPort(address, this::isAppiumProcess);
    }

    /**
     * Check if a process is potentially an Appium instance. This is used to
     * detect whether it should be killed by
     * {@link #rxStopLocalAppiumInstance()}
     * @param name The process' name. A {@link String} value.
     * @return A {@link Boolean} instance.
     * @see #rxStopLocalAppiumInstance()
     */
    public boolean isAppiumProcess(@NotNull String name) {
        return name.contains("node");
    }
    //endregion

    //region CLI commands
    /**
     * Command to detect where appium is installed.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmWhichAppium() {
        return "which appium";
    }

    /**
     * Fall back Appium path if {@link #cmWhichAppium()} fails.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmFallBackAppium() {
        return "/usr/local/bin/appium";
    }

    /**
     * Command to start an Appium instance.
     * @param cli The path to Appium CLI. A {@link String} value.
     * @param port The port to be used to start a new Appium instance. An
     *             {@link Integer} value.
     * @return A {@link String} value.
     */
    @NotNull
    public String cmStartLocalAppiumInstance(@NotNull String cli, int port) {
        return AppiumCommand.builder()
            .withBase(cli)
            .withPort(port)
            .build()
            .command();
    }
    //endregion

    //region Getters
    public CapType capabilityType() {
        if (ObjectUtil.nonNull(capability)) {
            return capability;
        } else {
            throw new RuntimeException(CAPABILITY_UNAVAILABLE);
        }
    }

    /**
     * Return {@link #serverAddress}.
     * @return A {@link String} value.
     */
    @NotNull
    public ServerAddress serverAddress() {
        return serverAddress;
    }

    /**
     * Get the {@link #serverAddress()} uri address.
     * @return A {@link String} value.
     */
    @NotNull
    public String serverUri() {
        return serverAddress().uri();
    }

    /**
     * Return {@link #browserName}.
     * @return A {@link String} value.
     */
    @NotNull
    public String browserName() {
        return browserName;
    }

    /**
     * Return {@link #platformName}.
     * @return A {@link String} value.
     */
    @NotNull
    public String platformName() {
        return platformName;
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
     * @return A {@link LocalizerType} instance.
     */
    @NotNull
    public LocalizerType localizer() {
        WeakReference<LocalizerType> td = textDelegate;

        LocalizerType ref;

        if (ObjectUtil.nonNull(td) && ObjectUtil.nonNull((ref = td.get()))) {
            return ref;
        } else {
            throw new RuntimeException(TEXT_DELEGATE_UNAVAILABLE);
        }
    }

    /**
     * Return {@link #PROCESS_RUNNER}. This method can be used to stub out
     * {@link #PROCESS_RUNNER}.
     * @return {@link #PROCESS_RUNNER}.
     */
    @NotNull
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    /**
     * Return {@link #NETWORK_HANDLER}. This method can be used to stub out
     * {@link #NETWORK_HANDLER}.
     * @return {@link #NETWORK_HANDLER}.
     */
    @NotNull
    public NetworkHandler networkHandler() {
        return NETWORK_HANDLER;
    }

    /**
     * Get the active {@link D} {@link #driver}.
     * @return {@link D} {@link #driver}.
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
     * Get the current {@link PlatformView}, or throw an {@link Exception} if
     * it is not found.
     * @return A {@link PlatformView} instance.
     */
    @NotNull
    public PlatformView platformView() {
        if (platformView != null) {
            return platformView;
        }

        throw new RuntimeException(PLATFORM_VIEW_UNAVAILABLE);
    }
    //endregion

    //region Setters
    public void setLocalizer(@NotNull LocalizerType delegate) {
        textDelegate = new WeakReference<>(delegate);
    }
    //endregion

    //region Appium Setup
    /**
     * Get a {@link Map} of capabilities to pass to Appium driver.
     * @return A {@link Map} instance.
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
     * Create a {@link D} instance in order to navigate UI test.
     * @return A {@link D} instance.
     */
    @NotNull
    protected abstract D driver(@NotNull String serverUrl,
                                @NotNull DesiredCapabilities caps);

    /**
     * Start the Appium driver. If {@link CapType#isComplete(Map)}
     * returns false, throw an {@link Exception}.
     * @param PARAM A {@link RetryType} instance.
     * @return A {@link Flowable} instance.
     * @see CapType#isComplete(Map)
     * @see CapType#distill(Map)
     * @see #driver(String, DesiredCapabilities)
     */
    @NotNull
    public Flowable<Boolean> rxStartDriver(@NotNull final RetryType PARAM) {
        CapType capType = capabilityType();
        Map<String,Object> caps = capabilities();

        if (capType.isComplete(caps)) {
            final Map<String,Object> distilled = capType.distill(caps);
            final DesiredCapabilities CAPS = new DesiredCapabilities(distilled);
            final String SERVER_URL = serverUri();

            return Completable.fromAction(() -> {
                    driver = driver(SERVER_URL, CAPS);
                })
                .<Boolean>toFlowable()
                .defaultIfEmpty(true)
                .retry(PARAM.retries());
        } else {
            return Flowable.error(new Exception(INSUFFICIENT_SETTINGS));
        }
    }

    /**
     * Quit the active Appium driver. If it is null, throw an {@link Exception}
     * instead.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStopDriver() {
        return Completable.fromAction(() -> driver().quit())
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Element Actions
    /**
     * Send a certain {@link String} key to a {@link WebElement}.
     * @param ELEMENT The {@link WebElement} that will receive the key.
     * @param TEXT The {@link String} to be sent.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxSendKey(@NotNull final WebElement ELEMENT,
                                       @NotNull final String...TEXT) {
        return Completable
            .fromAction(() -> ELEMENT.sendKeys(TEXT))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Send a click event to a {@link WebElement} with
     * {@link WebElement#click()}.
     * @param ELEMENT The {@link WebElement} to be clicked.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxClick(@NotNull final WebElement ELEMENT) {
        return Completable
            .fromAction(ELEMENT::click)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link BaseEngine}.
     * @param <T> Generics parameter that extends {@link BaseEngine}.
     */
    public static abstract class Builder<T extends BaseEngine> {
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
         * @param name A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withBrowserName(@NotNull String name) {
            ENGINE.browserName = name;
            return this;
        }

        /**
         * Set the {@link #ENGINE#platformView} value.
         * @param platformView A {@link PlatformView} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withPlatformView(@NotNull PlatformView platformView) {
            ENGINE.platformView = platformView;
            return this;
        }

        /**
         * Set the {@link #ENGINE#serverAddress}. This {@link String} represents
         * the Appium server address.
         * @param address A {@link ServerAddress} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withServerUrl(@NotNull ServerAddress address) {
            ENGINE.serverAddress = address;
            return this;
        }

        /**
         * Same as above, but use a {@link org.swiften.xtestkit.engine.mobile.Platform} instance.
         * @param platform A {@link PlatformType} instance.
         * @return The current {@link MobileEngine.Builder} instance.
         * @see CapType.Builder#withPlatform(PlatformType)
         */
        @NotNull
        public Builder<T> withPlatform(@NotNull PlatformType platform) {
            ENGINE.platformName =  platform.value();
            CAP_BUILDER.withPlatform(platform);
            return this;
        }

        /**
         * Set the {@link #ENGINE#testMode} value. This variable specifies
         * which test environment to be used.
         * @param mode A {@link TestMode} instance.
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