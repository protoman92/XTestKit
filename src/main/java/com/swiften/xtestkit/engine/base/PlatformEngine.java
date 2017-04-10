package com.swiften.xtestkit.engine.base;

/**
 * Created by haipham on 3/19/17.
 */

import com.swiften.xtestkit.engine.base.param.*;
import com.swiften.xtestkit.engine.base.param.BeforeClassParam;
import com.swiften.xtestkit.engine.base.param.protocol.Distinctive;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.base.protocol.*;
import com.swiften.xtestkit.engine.base.xpath.XPath;
import com.swiften.xtestkit.engine.mobile.MobileEngine;
import com.swiften.xtestkit.kit.TestKit;
import com.swiften.xtestkit.system.NetworkHandler;
import com.swiften.xtestkit.system.ProcessRunner;
import com.swiften.xtestkit.system.protocol.ProcessRunnerProtocol;
import com.swiften.xtestkit.test.protocol.TestListener;
import com.swiften.xtestkit.util.BooleanUtil;
import com.swiften.xtestkit.util.CollectionUtil;
import com.swiften.xtestkit.util.LogUtil;
import com.swiften.xtestkit.util.StringUtil;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * A base class for platform-specific implementations. Each Different platform
 * should extend this class and provide its own wrappers for Appium methods.
 */
public abstract class PlatformEngine<T extends WebDriver> implements
    DelayProtocol,
    Distinctive,
    ErrorProtocol,
    ProcessRunnerProtocol,
    TestListener {
    @NotNull private static final Queue<String> SERVER_QUEUE;

    static {
        SERVER_QUEUE = new ConcurrentLinkedQueue<>();
    }

    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;

    @Nullable private WeakReference<TextDelegate> textDelegate;

    @Nullable private T driver;
    @Nullable PlatformView platformView;

    @NotNull String browserName;
    @NotNull String platformName;
    @NotNull ServerAddress serverAddress;

    public PlatformEngine() {
        PROCESS_RUNNER = ProcessRunner.builder().build();
        NETWORK_HANDLER = NetworkHandler.builder().build();
        browserName = "";
        platformName = "";
        serverAddress = ServerAddress.defaultInstance();
    }

    //region Distinctive
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

    //region ProcessRunnerProtocol
    @NotNull
    @Override
    public String execute(@NotNull String args) throws IOException {
        return processRunner().execute(args);
    }

    @NotNull
    @Override
    public Flowable<String> rxExecute(@NotNull String args) {
        return processRunner().rxExecute(args);
    }
    //endregion

    //region TestListener
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
     * Subclasses of {@link PlatformEngine} should provide their own
     * implementations.
     * @param param A {@link BeforeClassParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
        if (serverAddress().isLocalInstance()) {
            return rxStartLocalAppiumInstance(param);
        }

        return Flowable.just(true);
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterClass}.
     * This method will be called by
     * {@link TestKit#rxAfterClass(AfterClassParam)}.
     * Subclasses of {@link PlatformEngine} should provide their own
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
     * Subclasses of {@link PlatformEngine} should provide their own
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
     * Subclasses of {@link PlatformEngine} should provide their own
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
     * @param PARAM A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #cmWhichAppium()
     * @see #processRunner()
     */
    @NotNull
    public Flowable<Boolean>
    rxStartLocalAppiumInstance(@NotNull final RetryProtocol PARAM) {
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
                LogUtil.printf("Set port %d for %s", a, this);

                final String COMMAND = cmStartLocalAppiumInstance(CLI, a);
                final Queue<String> SERVER_QUEUE = serverQueue();
                SERVER_QUEUE.offer(COMMAND);

                /* Need to start on a new Thread, or else it will block */
                new Thread(() -> {
                    for (;;) {
                        String top = SERVER_QUEUE.peek();

                        if (Objects.isNull(top) || top.equals(COMMAND)) {
                            new Thread(() -> {
                                try {
                                    RUNNER.execute(COMMAND);
                                } catch (Exception e) {
                                    LogUtil.println(e);
                                }
                            }).start();

                            synchronized (SERVER_QUEUE) {
                                SERVER_QUEUE.poll();
                            }

                            break;
                        }
                    }
                }).start();
            })
            .map(a -> true)
            .subscribe();
    }

    /**
     * Stop all local appium instances.
     * @return A {@link Flowable} instance.
     * @see NetworkHandler#rxKillProcessWithPort(RetryProtocol)
     */
    @NotNull
    public Flowable<Boolean> rxStopLocalAppiumInstance() {
        ServerAddress address = serverAddress();

        LogUtil.printf(
            "Stopping appium instance at port %d for %s",
            address.port(), this);

        return networkHandler().rxKillProcessWithPort(address);
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
    /**
     * Return {@link #SERVER_QUEUE}.
     * @return A {@link Queue} instance.
     */
    @NotNull
    public Queue<String> serverQueue() {
        return SERVER_QUEUE;
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
     * Return a {@link Platform} instance based on {@link #platformName()}.
     * @return A {@link Platform} instance.
     * @see #platformName()
     */
    @NotNull
    public Platform platform() {
        Optional<Platform> platform = Platform.fromValue(platformName());

        if (platform.isPresent()) {
            return platform.get();
        }

        throw new RuntimeException(PLATFORM_UNAVAILABLE);
    }

    @NotNull
    public TextDelegate localizer() {
        TextDelegate delegate;

        if
            (Objects.nonNull(textDelegate) &&
            (Objects.nonNull((delegate = textDelegate.get()))))
        {
            return delegate;
        }

        throw new RuntimeException(TEXT_DELEGATE_UNAVAILABLE);
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
     * Get the active {@link T} {@link #driver}.
     * @return {@link T} {@link #driver}.
     */
    @NotNull
    public T driver() {
        if (Objects.nonNull(driver)) {
            return driver;
        }

        throw new RuntimeException(DRIVER_UNAVAILABLE);
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
    public void setTextDelegate(@NotNull TextDelegate delegate) {
        textDelegate = new WeakReference<>(delegate);
    }
    //endregion

    //region Appium Setup
    /**
     * Get a {@link List} of required capabilities. We convert all values to
     * {@link String} and check if all values are non-empty. If a value is
     * not a {@link String}, but is falsy (e.g. a zero value), we replace
     * it with an empty {@link String}. Subclasses of {@link PlatformEngine}
     * can append to this {@link List}.
     * @return A {@link List} of {@link String}.
     */
    @NotNull
    public List<String> requiredCapabilities() {
        List<String> required = Collections.singletonList(
            serverAddress.uri()
        );

        return new ArrayList<>(required);
    }

    /**
     * If this method returns false, we should throw an {@link Exception}.
     * @return A {@link Boolean} value.
     */
    public boolean hasAllRequiredInformation() {
        List<String> required = requiredCapabilities();
        return required.stream().noneMatch(String::isEmpty);
    }

    /**
     * Same as above, but returns a {@link Flowable} for easier chaining
     * and composition.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxHasAllRequiredInformation() {
        boolean correct = hasAllRequiredInformation();

        if (correct) {
            return Flowable.just(true);
        }

        return Flowable.error(new Exception(INSUFFICIENT_SETTINGS));
    }

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

    /**
     * Get a {@link DesiredCapabilities} instance from {@link #capabilities()}.
     * @return A {@link DesiredCapabilities} instance.
     */
    @NotNull
    public DesiredCapabilities desiredCapabilities() {
        Map<String,Object> capabilities = capabilities();
        LogUtil.printf("Desired capabilities for %1$s: %2$s", this, capabilities);
        return new DesiredCapabilities(capabilities);
    }
    //endregion

    //region Driver Methods
    /**
     * Create a {@link T} instance in order to navigate UI test.
     * @return A {@link T} instance.
     */
    @NotNull
    protected abstract T createDriverInstance();

    /**
     * Start the Appium driver. If {@link #hasAllRequiredInformation()}
     * returns false, throw an {@link Exception}.
     * @param PARAM A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see #hasAllRequiredInformation()
     * @see #rxHasAllRequiredInformation()
     * @see #createDriverInstance()
     */
    @NotNull
    public Flowable<Boolean> rxStartDriver(@NotNull final RetryProtocol PARAM) {
        LogUtil.printf("Starting driver at %1$s for %2$s", serverAddress.uri(), this);

        return rxHasAllRequiredInformation()
            .delay(startDriverDelay(), TimeUnit.MILLISECONDS)
            .flatMapCompletable(a -> Completable.fromAction(() -> {
                driver = createDriverInstance();
            }))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true)
            .retry(PARAM.retries());
    }

    /**
     * Quit the active Appium driver. If it is null, throw an {@link Exception}
     * instead.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStopDriver() {
        LogUtil.printf("Stopping driver at %1$s for %2$s", serverAddress().uri(), this);

        return Completable.fromAction(() -> driver().quit())
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Device Methods
    /**
     * Navigate backwards for certain number of times.
     * @param param A {@link NavigateBack} object.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxNavigateBack(@NotNull NavigateBack param) {
        final T DRIVER;

        try {
            DRIVER = driver();
        } catch (Exception e) {
            return Flowable.error(e);
        }

        final int TIMES = param.times();

        @SuppressWarnings("WeakerAccess")
        final long DELAY = backNavigationDelay();

        final WebDriver.Navigation NAVIGATION = DRIVER.navigate();

        class Back {
            /**
             * Loop the operation until a stopping point is reached.
             * finished navigating back x times.
             */
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Completable back(final int ITERATION) {
                if (ITERATION < TIMES) {
                    return Completable
                        .fromAction(NAVIGATION::back)
                        .delay(DELAY, TimeUnit.MILLISECONDS)
                        .andThen(new Back().back(ITERATION + 1));
                }

                return Completable.complete();
            }
        }

        return new Back().back(0).<Boolean>toFlowable().defaultIfEmpty(true);
    }

    /**
     * Same as above, but uses a default {@link NavigateBack} instance.
     * @return A {@link Flowable} instance.
     * @see #rxNavigateBack(NavigateBack)
     */
    @NotNull
    public Flowable<Boolean> rxNavigateBackOnce() {
        NavigateBack param = NavigateBack.builder().withTimes(1).build();
        return rxNavigateBack(param);
    }

    /**
     * Dismiss a currently active alert. Either accept or reject.
     * @param PARAM An {@link AlertParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxDismissAlert(@NotNull final AlertParam PARAM) {
        return Flowable.just(driver().switchTo().alert())
            .flatMapCompletable(a -> Completable.fromAction(() -> {
                if (PARAM.shouldAccept()) {
                    a.accept();
                } else {
                    a.dismiss();
                }
            }))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Same as avove, but uses a default {@link AlertParam}.
     * @return A {@link Flowable} instance.
     * @see #rxDismissAlert(AlertParam)
     */
    @NotNull
    public Flowable<Boolean> rxAcceptAlert() {
        AlertParam param = AlertParam.builder().accept().build();
        return rxDismissAlert(param);
    }

    /**
     * Same as avove, but uses a default {@link AlertParam}.
     * @return A {@link Flowable} instance.
     * @see #rxDismissAlert(AlertParam)
     */
    @NotNull
    public Flowable<Boolean> rxRejectAlert() {
        AlertParam param = AlertParam.builder().reject().build();
        return rxDismissAlert(param);
    }
    //endregion

    //region By XPath
    /**
     * Convenience method to create a new {@link XPath.Builder} instance.
     * @return A {@link XPath.Builder} instance.
     */
    @NotNull
    protected XPath.Builder newXPathBuilderInstance() {
        return XPath.builder(platform());
    }

    /**
     * Find all elements that satisfies an {@link XPath} request.
     * @param param A {@link ByXPath} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<List<WebElement>> rxElementsByXPath(@NotNull ByXPath param) {
        final T DRIVER;

        try {
            DRIVER = driver();
        } catch (Exception e) {
            return Flowable.error(e);
        }

        final String XPATH = param.xPath();
        List<View> classes = param.classes();
        List<WebElement> elements = new ArrayList<>();

        return Flowable.fromIterable(classes)
            .map(cls -> String.format("//%s%s", cls.className(), XPATH))
            .doOnNext(a -> {
                LogUtil.println(String.format("Searching for \"%s\"", a));
            })
            .map(path -> {
                try {
                    /* Check for error here just to be certain */
                    return DRIVER.findElements(By.xpath(path));
                } catch (Exception e) {
                    return Collections.<WebElement>emptyList();
                }
            })
            .reduce(elements, (a, b) -> CollectionUtil.unify(a, b))
            .toFlowable();
    }

    /**
     * Get a single {@link WebElement} that satisfies an {@link XPath}
     * request. If there are multiple such elements, take the first one.
     * Throw an {@link Exception} if none is found.
     * @param PARAM A {@link ByXPath} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<WebElement> rxElementByXPath(@NotNull final ByXPath PARAM) {
        Flowable<List<WebElement>> source = PARAM.parent();

        if (Objects.isNull(source)) {
            source = rxElementsByXPath(PARAM);
        }

        return source
            .filter(a -> !a.isEmpty())
            .map(a -> a.get(0))
            .filter(Objects::nonNull)
            .switchIfEmpty(Flowable.error(new Exception(PARAM.error())));
    }
    //endregion

    //region With Text
    /**
     * Get all {@link View#hasText()} {@link WebElement} that are displaying
     * a text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    public Flowable<List<WebElement>> rxElementsWithText(@NotNull TextParam param) {
        XPath xPath = newXPathBuilderInstance()
            .hasText(localizer().localize(param.text()))
            .build();

        ByXPath query = ByXPath.builder().withXPath(xPath).build();
        return rxElementsByXPath(query);
    }

    /**
     * Same as above, but uses a default {@link TextParam} with a specified
     * text.
     * @param text A {@link String} value.
     * @return A {@link Flowable} instance.
     * @see #rxElementsWithText(TextParam)
     */
    @NotNull
    public Flowable<List<WebElement>> rxElementsWithText(@NotNull String text) {
        TextParam param = TextParam.builder().withText(text).build();
        return rxElementsWithText(param);
    }

    /**
     * Get a {@link View#hasText()} {@link WebElement} that is displaying
     * a text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<WebElement> rxElementWithText(@NotNull TextParam param) {
        ByXPath query = ByXPath.builder()
            .withParent(rxElementsWithText(param))
            .withError(noElementsWithText(localizer().localize(param.text())))
            .build();

        return rxElementByXPath(query);
    }

    /**
     * Same as above, but uses a default {@link TextParam} instance.
     * @param text The {@link String} to be found.
     * @return A {@link Flowable} instance.
     * @see #rxElementWithText(TextParam)
     */
    @NotNull
    public Flowable<WebElement> rxElementWithText(@NotNull String text) {
        TextParam param = TextParam.builder().withText(text).build();
        return rxElementWithText(param);
    }
    //endregion

    //region Contains Text
    /**
     * Get all {@link View#hasText()} {@link WebElement} whose texts contain
     * another text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<List<WebElement>> rxElementsContainingText(@NotNull TextParam param) {
        XPath xPath = newXPathBuilderInstance()
            .containsText(localizer().localize(param.text()))
            .build();

        ByXPath query = ByXPath.builder().withXPath(xPath).build();
        return rxElementsByXPath(query);
    }

    /**
     * Get a {@link View#hasText()} {@link WebElement} whose text contains
     * another text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<WebElement> rxElementContainingText(@NotNull TextParam param) {
        ByXPath query = ByXPath.builder()
            .withParent(rxElementsContainingText(param))
            .withError(noElementsContainingText(localizer().localize(param.text())))
            .build();

        return rxElementByXPath(query);
    }

    /**
     * Same as above, but uses a default {@link TextParam}.
     * @param text The text to be found.
     * @return A {@link Flowable} instance.
     * @see #rxElementWithText(TextParam)
     */
    @NotNull
    public Flowable<WebElement> rxElementContainingText(@NotNull String text) {
        TextParam param = TextParam.builder().withText(text).build();
        return rxElementContainingText(param);
    }
    //endregion

    //region With Hint
    /**
     * Get all {@link View#isEditable()} {@link WebElement} that have a
     * certain hint.
     * @param param A {@link HintParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<List<WebElement>> rxElementsWithHint(@NotNull HintParam param) {
        XPath xPath = newXPathBuilderInstance()
            .hasHint(localizer().localize(param.hint()))
            .build();

        ByXPath query = ByXPath.builder().withXPath(xPath).build();
        return rxElementsByXPath(query);
    }

    /**
     * Get a {@link View#isEditable()} {@link WebElement} that has a certain
     * hint.
     * @param param A {@link HintParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<WebElement> rxElementWithHint(@NotNull HintParam param) {
        ByXPath query = ByXPath.builder()
            .withParent(rxElementsWithHint(param))
            .withError(noElementsWithHint(localizer().localize(param.hint())))
            .build();

        return rxElementByXPath(query);
    }
    //endregion

    //region Contains Hint
    /**
     * Get all {@link View#isEditable()} {@link WebElement} whose hints
     * contain another hint.
     * @param param A {@link HintParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<List<WebElement>> rxElementsContainingHint(@NotNull HintParam param) {
        XPath xPath = newXPathBuilderInstance()
            .containsHint(localizer().localize(param.hint()))
            .build();

        ByXPath query = ByXPath.builder().withXPath(xPath).build();
        return rxElementsByXPath(query);
    }

    /**
     * Get a {@link View#isEditable()} {@link WebElement} whose hint contains
     * another hint.
     * @param param A {@link HintParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<WebElement> rxElementContainingHint(@NotNull HintParam param) {
        ByXPath query = ByXPath.builder()
            .withParent(rxElementsContainingHint(param))
            .withError(noElementsContainingHint(localizer().localize(param.hint())))
            .build();

        return rxElementByXPath(query);
    }
    //endregion

    //region Editable Elements
    /**
     * Get all {@link View#isEditable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<List<WebElement>> rxAllEditableElements() {
//        XPath xPath = newXPathBuilderInstance().isEditable(true).build();

        ByXPath query = ByXPath.builder()
            .withClasses(platformView().isEditable())
//            .withXPath(xPath)
            .build();

        return rxElementsByXPath(query);
    }

    /**
     * Clear all {@link View#isEditable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxClearAllEditableElements() {
        return rxAllEditableElements()
            .flatMap(Flowable::fromIterable)
            .flatMapCompletable(a -> Completable.fromAction(a::clear))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Clickable Elements
    /**
     * Get all {@link View#isClickable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<List<WebElement>> rxAllClickableElements() {
        ByXPath query = ByXPath.builder()
            .withXPath(newXPathBuilderInstance().isClickable(true).build())
            .build();

        return rxElementsByXPath(query);
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
    public static abstract class Builder<T extends PlatformEngine> {
        @NotNull final protected T ENGINE;

        protected Builder() {
            ENGINE = createEngineInstance();
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
         * Set the {@link #ENGINE#platformName} value.
         * @param name The name of the platform for which test are executed.
         * @return The current {@link MobileEngine.Builder} instance.
         */
        @NotNull
        public Builder<T> withPlatformName(@NotNull String name) {
            ENGINE.platformName = name;
            return this;
        }

        /**
         * Same as above, but use a {@link Platform} instance.
         * @param platform A {@link Platform} instance.
         * @return The current {@link MobileEngine.Builder} instance.
         */
        @NotNull
        public Builder<T> withPlatform(@NotNull Platform platform) {
            return withPlatformName(platform.value());
        }

        @NotNull
        public T build() {
            return ENGINE;
        }

        @NotNull
        protected abstract T createEngineInstance();
    }
    //endregion

    /**
     * Implement this interface to localize text when needed
     */
    public interface TextDelegate {
        /**
         * Localize reactively.
         * @param text The text to be localized.
         * @return A {@link Flowable} instance.
         */
        @NotNull Flowable<String> rxLocalize(@NotNull String text);

        /**
         * Localize a text.
         * @param text The text to be localized.
         * @return A {@link String} value.
         */
        @NotNull String localize(@NotNull String text);
    }
}