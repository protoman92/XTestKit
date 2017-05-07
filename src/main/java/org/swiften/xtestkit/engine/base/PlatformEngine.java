package org.swiften.xtestkit.engine.base;

/**
 * Created by haipham on 3/19/17.
 */

import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.engine.base.capability.TestCapabilityType;
import org.swiften.xtestkit.engine.base.param.*;
import org.swiften.xtestkit.engine.base.type.*;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.engine.base.xpath.XPath;
import org.swiften.xtestkit.engine.mobile.MobileEngine;
import org.swiften.xtestkit.kit.TestKit;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.system.NetworkHandler;
import org.swiften.xtestkit.system.ProcessRunner;
import org.swiften.xtestkit.test.TestListenerType;
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
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.collection.CollectionUtil;
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
public abstract class PlatformEngine<T extends WebDriver> implements
    PlatformDelayType,
    DistinctiveType,
    PlatformErrorType,
    TestListenerType
{
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;

    @Nullable private WeakReference<TextDelegate> textDelegate;

    @Nullable private T driver;
    @Nullable PlatformView platformView;
    @Nullable TestCapabilityType capability;

    @NotNull String browserName;
    @NotNull String platformName;
    @NotNull ServerAddress serverAddress;
    @NotNull TestMode testMode;

    public PlatformEngine() {
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
     * Subclasses of {@link PlatformEngine} should provide their own
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
     * @param PARAM A {@link RetriableType} instance.
     * @return A {@link Flowable} instance.
     * @see #cmWhichAppium()
     * @see #processRunner()
     */
    @NotNull
    public Flowable<Boolean> rxStartLocalAppium(@NotNull final RetriableType PARAM) {
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
     * @see NetworkHandler#rxKillProcessWithPort(RetriableType, Predicate)
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
    public TestCapabilityType capabilityType() {
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
     * Return a {@link Platform} instance based on {@link #platformName()}.
     * @return A {@link Platform} instance.
     * @see #platformName()
     */
    @NotNull
    public Platform platform() {
        Optional<Platform> platform = Platform.fromValue(platformName());

        if (platform.isPresent()) {
            return platform.get();
        } else {
            throw new RuntimeException(PLATFORM_UNAVAILABLE);
        }
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
     * Get the associated {@link TextDelegate} instance.
     * @return A {@link TextDelegate} instance.
     */
    @NotNull
    public TextDelegate localizer() {
        WeakReference<TextDelegate> td = textDelegate;

        TextDelegate ref;

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
     * Get the active {@link T} {@link #driver}.
     * @return {@link T} {@link #driver}.
     */
    @NotNull
    public T driver() {
        if (ObjectUtil.nonNull(driver)) {
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
     * Create a {@link T} instance in order to navigate UI test.
     * @return A {@link T} instance.
     */
    @NotNull
    protected abstract T driver(@NotNull String serverUrl,
                                @NotNull DesiredCapabilities caps);

    /**
     * Start the Appium driver. If {@link TestCapabilityType#isComplete(Map)}
     * returns false, throw an {@link Exception}.
     * @param PARAM A {@link RetriableType} instance.
     * @return A {@link Flowable} instance.
     * @see TestCapabilityType#isComplete(Map)
     * @see TestCapabilityType#distill(Map)
     * @see #driver(String, DesiredCapabilities)
     */
    @NotNull
    public Flowable<Boolean> rxStartDriver(@NotNull final RetriableType PARAM) {
        TestCapabilityType capType = capabilityType();
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
        List<ViewType> classes = param.classes();
        List<WebElement> elements = new ArrayList<>();

        return Flowable.fromIterable(classes)
            .map(cls -> String.format("//%s%s", cls.className(), XPATH))
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

        if (ObjectUtil.isNull(source)) {
            source = rxElementsByXPath(PARAM);
        }

        return source
            .filter(a -> !a.isEmpty())
            .map(a -> a.get(0))
            .filter(ObjectUtil::nonNull)
            .switchIfEmpty(Flowable.error(new Exception(PARAM.error())));
    }
    //endregion

    //region With Text
    /**
     * Get all {@link ViewType#hasText()} {@link WebElement} that are displaying
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
     * Get a {@link ViewType#hasText()} {@link WebElement} that is displaying
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
     * Get all {@link ViewType#hasText()} {@link WebElement} whose texts contain
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
     * Get a {@link ViewType#hasText()} {@link WebElement} whose text contains
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
     * Get all {@link ViewType#isEditable()} {@link WebElement} that have a
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
     * Get a {@link ViewType#isEditable()} {@link WebElement} that has a certain
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
     * Get all {@link ViewType#isEditable()} {@link WebElement} whose hints
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
     * Get a {@link ViewType#isEditable()} {@link WebElement} whose hint contains
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
     * Get all {@link ViewType#isEditable()} {@link WebElement}.
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
     * Clear all {@link ViewType#isEditable()} {@link WebElement}.
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
     * Get all {@link ViewType#isClickable()} {@link WebElement}.
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
        @NotNull final protected TestCapabilityType.Builder CAP_BUILDER;

        protected Builder(@NotNull T engine,
                          @NotNull TestCapabilityType.Builder capBuilder) {
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
         * Same as above, but use a {@link Platform} instance.
         * @param platform A {@link Platform} instance.
         * @return The current {@link MobileEngine.Builder} instance.
         * @see TestCapabilityType.Builder#withPlatform(Platform)
         */
        @NotNull
        public Builder<T> withPlatform(@NotNull Platform platform) {
            ENGINE.platformName =  platform.value();
            CAP_BUILDER.withPlatform(platform);
            return this;
        }

        /**
         * Set the {@link #ENGINE#testMode} value. This variable specifies
         * which test environment to be used.
         * @param mode A {@link TestMode} instance.
         * @return The current {@link Builder} instance.
         * @see TestCapabilityType.Builder#withTestMode(TestMode)
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