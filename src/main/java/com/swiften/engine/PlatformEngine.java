package com.swiften.engine;

/**
 * Created by haipham on 3/19/17.
 */

import com.swiften.engine.param.ByXPath;
import com.swiften.engine.param.HintParam;
import com.swiften.engine.param.TextParam;
import com.swiften.engine.param.NavigateBack;
import com.swiften.engine.protocol.*;
import com.swiften.util.CollectionUtil;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A base class for platform-specific implementations. Each Different platform
 * should extend this class and provide its own wrappers for Appium methods.
 */
public abstract class PlatformEngine<T extends WebDriver> implements
    EngineDelay,
    EngineError {
    @Nullable protected T driver;
    @Nullable protected PlatformView platformView;

    @NotNull protected String app;
    @NotNull protected String appPackage;
    @NotNull protected String appiumVersion;
    @NotNull protected String automationName;
    @NotNull protected String browserName;
    @NotNull protected String deviceName;
    @NotNull protected String platformName;
    @NotNull protected String platformVersion;
    @NotNull protected String serverUrl;
    protected long deviceReadyTimeout;

    public PlatformEngine() {
        app = "";
        appPackage = "";
        appiumVersion = "1.6.3";
        automationName = "";
        browserName = "";
        deviceName = "";
        deviceReadyTimeout = 10000;
        platformName = "";
        platformVersion = "";
        serverUrl = "http://localhost:4723/wd/hub";
    }

    /**
     * Get the current {@link PlatformView}, or throw an {@link Exception} if
     * it is not found.
     * @return A {@link PlatformView} instance.
     */
    @NotNull
    protected PlatformView platformView() {
        if (platformView != null) {
            return platformView;
        }

        throw new RuntimeException(PLATFORM_VIEW_UNAVAILABLE);
    }

    /**
     * Get a {@link List} of required capabilities. We convert all values to
     * {@link String} and check if all values are non-empty. If a value is
     * not a {@link String}, but is falsy (e.g. a zero value), we replace
     * it with an empty {@link String}. Subclasses of {@link PlatformEngine}
     * can append to this {@link List}.
     * @return A {@link List} of {@link String}
     */
    @NotNull
    protected List<String> requiredCapabilities() {
        List<String> required = Arrays.asList(
            app,
            appPackage,
            automationName,
            deviceName,
            deviceName,
            platformName,
            platformVersion,
            serverUrl
        );

        return new ArrayList<>(required);
    }

    /**
     * If this method returns false, we should throw an {@link Exception}.
     * @return A {@link Boolean} value.
     */
    protected boolean hasAllRequiredInformation() {
        List<String> required = requiredCapabilities();
        return required.stream().allMatch(String::isEmpty);
    }

    /**
     * Get a {@link Map} of capabilities to pass to Appium driver.
     * @return A {@link Map} instance.
     */
    @NotNull
    private Map<String,Object> capabilities() {
        Map<String,Object> capabilities = new HashMap<String,Object>();
        capabilities.put("app", app);
        capabilities.put("appPackage", appPackage);
        capabilities.put("appium-version", appiumVersion);
        capabilities.put("automationName", appiumVersion);
        capabilities.put("deviceName", deviceName);
        capabilities.put("deviceReadyTimeout", deviceReadyTimeout);
        capabilities.put("platformName", platformName);
        capabilities.put("platformVersion", platformVersion);
        return capabilities;
    }

    /**
     * Get a {@link DesiredCapabilities} instance from {@link #capabilities()}.
     * @return A {@link DesiredCapabilities} instance.
     */
    protected DesiredCapabilities desiredCapabilities() {
        Map<String,Object> capabilities = capabilities();
        return new DesiredCapabilities(capabilities);
    }

    //region DriverProtocol
    /**
     * Start the Appium driver. If {@link #hasAllRequiredInformation()}
     * returns false, throw an {@link Exception}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStartDriver() {
        if (hasAllRequiredInformation()) {
            return Completable
                .fromAction(() -> driver = createDriverInstance())
                .toFlowable();
        }

        return Flowable.error(new Exception(INSUFFICIENT_SETTINGS));
    }

    /**
     * Quit the active Appium driver. If it is null, throw an {@link Exception}
     * instead.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStopDriver() {
        if (Objects.nonNull(driver)) {
            return Completable.fromAction(() -> driver.quit()).toFlowable();
        }

        return Flowable.error(new Exception(DRIVER_UNAVAILABLE));
    }
    //endregion

    //region DeviceProtocol
    /**
     * Navigate backwards for certain number of times.
     * @param param A {@link NavigateBack} object.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxNavigateBack(@NotNull NavigateBack param) {
        final T DRIVER = driver;
        final int TIMES = param.times;
        final long DELAY = BACK_NAVIGATION_DELAY;

        if (Objects.nonNull(DRIVER)) {
            class Back {
                /**
                 * Loop the operation until a stopping point is reached.
                 * finished navigating back x times.
                 */
                @NotNull
                Completable back(final int ITERATION) {
                    if (ITERATION < TIMES) {
                        return Completable
                            .fromAction(() -> DRIVER.navigate().back())
                            .delay(DELAY, TimeUnit.MILLISECONDS)
                            .andThen(new Back().back(ITERATION + 1));
                    }

                    return Completable.complete();
                }
            }

            return new Back().back(0).toFlowable().map(a -> true);
        }

        return Flowable.error(new Error(DRIVER_UNAVAILABLE));
    }
    //endregion

    //region By XPath
    /**
     * Convenience method to create a new {@link XPath.Builder} instance.
     * @return A {@link XPath.Builder} instance.
     */
    @NotNull
    protected XPath.Builder newXPathBuilderInstance() {
        Optional<Platform> platform = Platform.fromValue(platformName);

        if (platform.isPresent()) {
            return XPath.newBuilder(platform.get());
        }

        throw new RuntimeException(new Error(PLATFORM_UNAVAILABLE));
    }

    /**
     * Find all elements that satisfies an {@link XPath} request.
     * @param param A {@link ByXPath} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<List<WebElement>> rxElementsByXPath(@NotNull ByXPath param) {
        if (Objects.nonNull(driver)) {
            final T DRIVER = driver;
            final String XPATH = param.xPath;
            List<View> classes = param.classes;

            return Flowable.fromIterable(classes)
                .map(cls -> String.format("//%s%s", cls.className(), XPATH))
                .map(query -> DRIVER.findElements(By.xpath(query)))
                .onErrorReturnItem(Collections.emptyList())
                .reduce(CollectionUtil::unify)
                .toFlowable();
        }

        return Flowable.error(new Error(DRIVER_UNAVAILABLE));
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
        Flowable<List<WebElement>> source;

        if (Objects.nonNull(PARAM.parent)) {
            source = PARAM.parent;
        } else {
            source = rxElementsByXPath(PARAM);
        }

        return source
            .filter(a -> !a.isEmpty())
            .map(a -> a.get(0))
            .filter(Objects::nonNull)
            .onErrorResumeNext(Flowable.error(new Exception(PARAM.error)));
    }
    //endregion

    //region With Text
    /**
     * Get all {@link View#hasText()} {@link WebElement} that are displaying
     * a text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<List<WebElement>>
    rxElementsWithText(@NotNull TextParam param) {
        String xPath = newXPathBuilderInstance()
            .hasText(param.text)
            .build()
            .getAttribute();

        ByXPath query = ByXPath.newBuilder()
            .withClasses(platformView().hasText())
            .withError(noElementsWithText(param.text))
            .withXPath(xPath)
            .build();

        return rxElementsByXPath(query);
    }

    /**
     * Get a {@link View#hasText()} {@link WebElement} that is displaying
     * a text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<WebElement> rxElementWithText(@NotNull TextParam param) {
        ByXPath query = ByXPath.newBuilder()
            .withParent(rxElementsWithText(param))
            .withError(noElementsWithText(param.text))
            .build();

        return rxElementByXPath(query);
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
    public Flowable<List<WebElement>>
    rxElementsContainingText(@NotNull TextParam param) {
        String xPath = newXPathBuilderInstance()
            .containsText(param.text)
            .build()
            .getAttribute();

        ByXPath query = ByXPath.newBuilder()
            .withClasses(platformView().hasText())
            .withError(noElementsContainingText(param.text))
            .withXPath(xPath)
            .build();

        return rxElementsByXPath(query);
    }

    /**
     * Get a {@link View#hasText()} {@link WebElement} whose text contains
     * another text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<WebElement>
    rxElementContainingText(@NotNull TextParam param) {
        ByXPath query = ByXPath.newBuilder()
            .withParent(rxElementsContainingText(param))
            .withError(noElementsContainingText(param.text))
            .build();

        return rxElementByXPath(query);
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
    public Flowable<List<WebElement>>
    rxElementsWithHint(@NotNull HintParam param) {
        String xPath = newXPathBuilderInstance()
            .hasHint(param.hint)
            .build()
            .getAttribute();

        ByXPath query = ByXPath.newBuilder()
            .withClasses(platformView().isEditable())
            .withError(noElementsWithHint(param.hint))
            .withXPath(xPath)
            .build();

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
        ByXPath query = ByXPath.newBuilder()
            .withParent(rxElementsWithHint(param))
            .withError(noElementsWithHint(param.hint))
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
    public Flowable<List<WebElement>>
    rxElementsContainingHint(@NotNull HintParam param) {
        String xPath = newXPathBuilderInstance()
            .containsHint(param.hint)
            .build()
            .getAttribute();

        ByXPath query = ByXPath.newBuilder()
            .withClasses(platformView().isEditable())
            .withError(noElementContainingHint(param.hint))
            .withXPath(xPath)
            .build();

        return rxElementsByXPath(query);
    }

    /**
     * Get a {@link View#isEditable()} {@link WebElement} whose hint contains
     * another hint.
     * @param param A {@link HintParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<WebElement>
    rxElementContainingHint(@NotNull HintParam param) {
        ByXPath query = ByXPath.newBuilder()
            .withParent(rxElementsContainingHint(param))
            .withError(noElementContainingHint(param.hint))
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
        ByXPath query = ByXPath.newBuilder()
            .withClasses(platformView().isEditable())
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
            .flatMap(a -> Completable.fromAction(a::clear).toFlowable())
            .map(a -> true);
    }
    //endregion

    //region Clickable Elements
    /**
     * Get all {@link View#isClickable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<List<WebElement>> rxAllClickableElements() {
        ByXPath query = ByXPath.newBuilder()
            .withClasses(platformView().isClickable())
            .build();

        return rxElementsByXPath(query);
    }
    //endregion

    @NotNull
    protected abstract T createDriverInstance();

    public static abstract class Builder<T extends PlatformEngine> {
        @NotNull final protected T ENGINE;

        protected Builder() {
            ENGINE = createEngineInstance();
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
         * Set the {@link #ENGINE#appiumVersion} value.
         * @param version The Appium version that will run the tests.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withAppiumVersion(@NotNull String version) {
            ENGINE.appiumVersion = version;
            return this;
        }

        /**
         * Set the {@link #ENGINE#app} value. We assume that the app is
         * placed in {currentProject}/app folder.
         * @param app The app's file name.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withApp(@NotNull String app) {
            String path = System.getProperty("user.dir");
            ENGINE.app = String.format("%s/%s", path, app);
            return this;
        }

        /**
         * Set the {@link #ENGINE#appPackage} value.
         * @param appPackage The app's package name.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withAppPackage(@NotNull String appPackage) {
            ENGINE.appPackage = appPackage;
            return this;
        }

        /**
         * Set the {@link #ENGINE#automationName} value.
         * @param automationName The app's automation name. For e.g., Android
         *                       SDK 16 or less should specify Selendroid,
         *                       and Appium otherwise. In order to minimize
         *                       platform differences, we should use
         *                       {@link XPath} as much as possible for
         *                       locator operations.
         * @return The current {@link Builder} instance.
         */
        public Builder<T> withAutomationName(@NotNull String automationName) {
            ENGINE.automationName = automationName;
            return this;
        }

        /**
         * Set the {@link #ENGINE#browserName} value.
         * @param browser The browser name for Web tests. Android/iOS tests
         *                should leave this blank.
         * @return The current {@link Builder} instance.
         */
        public Builder<T> withBrowserName(@NotNull String browser) {
            ENGINE.browserName = browser;
            return this;
        }

        /**
         * Set the {@link #ENGINE#deviceName} value.
         * @param name The name of the device on which tests will be run.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withDeviceName(@NotNull String name) {
            ENGINE.deviceName = name;
            return this;
        }

        /**
         * Set the {@link #ENGINE#deviceReadyTimeout} value. This value
         * specifies how long Appium should wait until the device is ready.
         * If the app takes to long to boot up, Appium will terminate the
         * running tests.
         * @param timeout A {@link Long} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withDeviceReadyTimeout(long timeout) {
            ENGINE.deviceReadyTimeout = timeout;
            return this;
        }

        /**
         * Set the {@link #ENGINE#platformName} value.
         * @param name The name of the platform for which tests are executed.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withPlatformName(@NotNull String name) {
            ENGINE.platformName = name;
            return this;
        }

        @NotNull
        public Builder<T> withPlatform(@NotNull Platform platform) {
            return withPlatformName(platform.value());
        }

        /**
         * Set the {@link #ENGINE#platformVersion} value.
         * @param version The platform version to be executed.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withPlatformVersion(@NotNull String version) {
            ENGINE.platformVersion = version;
            return this;
        }

        /**
         * Set the {@link #ENGINE#serverUrl}. This {@link String} represents
         * the Appium server address.
         * @param url The server url.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder<T> withServerUrl(@NotNull String url) {
            ENGINE.serverUrl = url;
            return this;
        }

        @NotNull
        public T build() {
            return ENGINE;
        }

        @NotNull
        protected abstract T createEngineInstance();
    }
}