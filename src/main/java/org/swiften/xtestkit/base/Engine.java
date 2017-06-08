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
import org.swiften.xtestkit.base.capability.EngineCapabilityType;
import org.swiften.xtestkit.base.element.checkbox.CheckBoxActionType;
import org.swiften.xtestkit.base.element.choice.ChoiceSelectorType;
import org.swiften.xtestkit.base.element.click.ClickActionType;
import org.swiften.xtestkit.base.element.date.DateActionType;
import org.swiften.xtestkit.base.element.general.BaseActionType;
import org.swiften.xtestkit.base.element.input.BaseInputActionType;
import org.swiften.xtestkit.base.element.input.BaseKeyboardActionType;
import org.swiften.xtestkit.base.element.locator.type.BaseLocatorType;
import org.swiften.xtestkit.base.element.password.BasePasswordActionType;
import org.swiften.xtestkit.base.element.property.BaseElementPropertyType;
import org.swiften.xtestkit.base.element.search.SearchActionType;
import org.swiften.xtestkit.base.element.swipe.BaseSwipeType;
import org.swiften.xtestkit.base.element.switcher.BaseSwitcherActionType;
import org.swiften.xtestkit.base.element.tap.BaseTapType;
import org.swiften.xtestkit.base.element.visibility.BaseVisibilityActionType;
import org.swiften.xtestkit.base.type.AppiumHandlerType;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.test.TestListenerType;
import org.swiften.xtestkitcomponents.common.DistinctiveType;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.system.network.NetworkHandler;
import org.swiften.xtestkitcomponents.system.process.ProcessRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * A base class for platform-specific implementations. Each Different platform
 * should extend this class and provide its own wrappers for Appium methods.
 */
public abstract class Engine<D extends WebDriver> implements
    AppiumHandlerType,
    BaseActionType<D>,
    ClickActionType,
    ChoiceSelectorType<D>,
    CheckBoxActionType,
    DateActionType<D>,
    BaseElementPropertyType,
    BaseInputActionType<D>,
    BaseLocatorType<D>,
    BaseKeyboardActionType<D>,
    BasePasswordActionType<D>,
    BaseTapType<D>,
    SearchActionType,
    BaseSwipeType<D>,
    BaseSwitcherActionType,
    BaseVisibilityActionType<D>,
    DistinctiveType,
    TestListenerType
{
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;

    @Nullable private D driver;
    @Nullable
    EngineCapabilityType capability;
    @Nullable private LocalizerType localizer;

    @NotNull String browserName;
    @NotNull Address address;
    @NotNull TestMode testMode;

    public Engine() {
        PROCESS_RUNNER = new ProcessRunner();
        NETWORK_HANDLER = new NetworkHandler();
        browserName = "";
        testMode = TestMode.SIMULATED;
        address = Address.defaultInstance();
    }

    //region Getters
    /**
     * Get {@link #capability}.
     * @return {@link EngineCapabilityType} instance.
     * @see ObjectUtil#nonNull(Object)
     * @see #capability
     * @see #NOT_AVAILABLE
     */
    public EngineCapabilityType capabilityType() {
        if (ObjectUtil.nonNull(capability)) {
            return capability;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
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
     * Get the associated {@link PlatformType}
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
     * @see #localizer
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public LocalizerType localizer() {
        if (ObjectUtil.nonNull(localizer)) {
            return localizer;
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
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public D driver() {
        if (ObjectUtil.nonNull(driver)) {
            return driver;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
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
     * @param localizer {@link LocalizerType} instance.
     * @see #localizer
     */
    public void setLocalizer(@NotNull LocalizerType localizer) {
        this.localizer = localizer;
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
    public Flowable<Boolean> rxa_onFreshStart() {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_onBatchStarted(@NotNull final int[] INDEXES) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_onBatchFinished(@NotNull final int[] INDEXES) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_onAllTestsFinished() {
        return Flowable.just(true);
    }
    //endregion

    //region Test Setup
    /**
     * Convenience method for {@link org.testng.annotations.BeforeClass}.
     * This method will be called by
     * {@link org.swiften.xtestkit.kit.TestKit#rxa_beforeClass(BeforeClassParam)}.
     * Subclasses of {@link Engine} should provide their own
     * implementations.
     * @param param {@link BeforeClassParam} instance.
     * @return {@link Flowable} instance.
     * @see Address#isLocalInstance()
     * @see #address()
     */
    @NotNull
    public Flowable<Boolean> rxa_beforeClass(@NotNull BeforeClassParam param) {
        if (address().isLocalInstance()) {
            return rxa_startLocalAppium(param);
        } else {
            return Flowable.just(true);
        }
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterClass}.
     * This method will be called by
     * {@link org.swiften.xtestkit.kit.TestKit#rxa_afterClass(AfterClassParam)}.
     * Subclasses of {@link Engine} should provide their own
     * implementations.
     * @param param {@link AfterClassParam} instance.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#isTrue(boolean)
     * @see BooleanUtil#toTrue(Object)
     * @see NetworkHandler#markPortAvailable(int)
     * @see #address()
     * @see #networkHandler()
     * @see #rxa_stopLocalAppium()
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxa_afterClass(@NotNull AfterClassParam param) {
        NetworkHandler HANDLER = networkHandler();
        Address ADDRESS = address();

        Flowable<Boolean> reusePort = Completable
            .fromAction(() -> HANDLER.markPortAvailable(ADDRESS.port()))
            .toFlowable()
            .map(BooleanUtil::toTrue)
            .defaultIfEmpty(true);

        Flowable<Boolean> stopServer;

        if (ADDRESS.isLocalInstance()) {
            stopServer = rxa_stopLocalAppium();
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
     * {@link org.swiften.xtestkit.kit.TestKit#rxa_beforeMethod(BeforeParam)}.
     * Subclasses of {@link Engine} should provide their own
     * implementations.
     * @param param {@link BeforeParam} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxa_beforeMethod(@NotNull BeforeParam param) {
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
    public Flowable<Boolean> rxa_afterMethod(@NotNull AfterParam param) {
        return Flowable.just(true);
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
     * @see #NOT_AVAILABLE
     */
    @NotNull
    protected D driver(@NotNull String serverUrl, @NotNull DesiredCapabilities caps) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Start the Appium driver. If {@link EngineCapabilityType#isComplete(Map)}
     * returns false, throw {@link Exception}.
     * @param PARAM {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see EngineCapabilityType#isComplete(Map)
     * @see EngineCapabilityType#distill(Map)
     * @see RetryType#retries()
     * @see #driver(String, DesiredCapabilities)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public Flowable<Boolean> rxa_startDriver(@NotNull final RetryType PARAM) {
        EngineCapabilityType capType = capabilityType();
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
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Quit the active Appium driver. If it is null, throw {@link Exception}
     * instead.
     * @return {@link Flowable} instance.
     * @see WebDriver#quit()
     * @see #driver()
     */
    @NotNull
    public Flowable<Boolean> rxa_stopDriver() {
        final WebDriver DRIVER = driver();

        return Completable
            .fromAction(DRIVER::quit)
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
        @NotNull final protected EngineCapabilityType.Builder CB;

        protected Builder(@NotNull T engine,
                          @NotNull EngineCapabilityType.Builder cb) {
            ENGINE = engine;
            CB = cb;
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
         * @see EngineCapabilityType.Builder#withTestMode(TestMode)
         */
        @NotNull
        public Builder<T> withTestMode(@NotNull TestMode mode) {
            ENGINE.testMode = mode;
            CB.withTestMode(mode);
            return this;
        }

        @NotNull
        public T build() {
            ENGINE.capability = CB.build();
            return ENGINE;
        }
    }
    //endregion
}