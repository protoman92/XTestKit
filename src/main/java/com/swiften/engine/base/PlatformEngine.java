package com.swiften.engine.base;

/**
 * Created by haipham on 3/19/17.
 */

import com.swiften.engine.base.param.*;
import com.swiften.engine.base.protocol.*;
import com.swiften.util.CollectionUtil;
import com.swiften.util.ProcessRunner;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
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
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @Nullable private T driver;
    @Nullable PlatformView platformView;

    @NotNull protected String browserName;
    @NotNull protected String serverUrl;

    public PlatformEngine() {
        PROCESS_RUNNER = ProcessRunner.newBuilder().build();
        browserName = "";
        serverUrl = "http://localhost:4723/wd/hub";
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

    //region Appium Setup.
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
            serverUrl
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
     * Get a {@link Map} of capabilities to pass to Appium driver.
     * @return A {@link Map} instance.
     */
    @NotNull
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = new HashMap<String,Object>();
        capabilities.put(CapabilityType.BROWSER_NAME, browserName);
        return capabilities;
    }

    /**
     * Get a {@link DesiredCapabilities} instance from {@link #capabilities()}.
     * @return A {@link DesiredCapabilities} instance.
     */
    @NotNull
    public DesiredCapabilities desiredCapabilities() {
        Map<String,Object> capabilities = capabilities();
        return new DesiredCapabilities(capabilities);
    }
    //endregion

    //region Driver Methods.

    /**
     * Create a {@link T} instance in order to navigate UI tests.
     * @return A {@link T} instance.
     */
    @NotNull
    protected abstract T createDriverInstance();

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
        return Completable.fromAction(() -> driver().quit()).toFlowable();
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

        return new Back().back(0).toFlowable().map(a -> true);
    }
    //endregion

    //region By XPath
    /**
     * Convenience method to create a new {@link XPath.Builder} instance.
     * @return A {@link XPath.Builder} instance.
     */
    @NotNull
    protected abstract XPath.Builder newXPathBuilderInstance();

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
     */
    @NotNull
    public Flowable<List<WebElement>> rxElementsWithText(@NotNull TextParam param) {
        String xPath = newXPathBuilderInstance()
            .hasText(param.text())
            .build()
            .getAttribute();

        ByXPath query = ByXPath.newBuilder()
            .withClasses(platformView().hasText())
            .withError(noElementsWithText(param.text()))
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
            .withError(noElementsWithText(param.text()))
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
    public Flowable<List<WebElement>> rxElementsContainingText(@NotNull TextParam param) {
        String xPath = newXPathBuilderInstance()
            .containsText(param.text())
            .build()
            .getAttribute();

        ByXPath query = ByXPath.newBuilder()
            .withClasses(platformView().hasText())
            .withError(noElementsContainingText(param.text()))
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
    public Flowable<WebElement> rxElementContainingText(@NotNull TextParam param) {
        ByXPath query = ByXPath.newBuilder()
            .withParent(rxElementsContainingText(param))
            .withError(noElementsContainingText(param.text()))
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
    public Flowable<List<WebElement>> rxElementsWithHint(@NotNull HintParam param) {
        String xPath = newXPathBuilderInstance()
            .hasHint(param.hint())
            .build()
            .getAttribute();

        ByXPath query = ByXPath.newBuilder()
            .withClasses(platformView().isEditable())
            .withError(noElementsWithHint(param.hint()))
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
            .withError(noElementsWithHint(param.hint()))
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
        String xPath = newXPathBuilderInstance()
            .containsHint(param.hint())
            .build()
            .getAttribute();

        ByXPath query = ByXPath.newBuilder()
            .withClasses(platformView().isEditable())
            .withError(noElementsContainingHint(param.hint()))
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
    public Flowable<WebElement> rxElementContainingHint(@NotNull HintParam param) {
        ByXPath query = ByXPath.newBuilder()
            .withParent(rxElementsContainingHint(param))
            .withError(noElementsContainingHint(param.hint()))
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

    /**
     * Start the test environment. E.g. if we are testing mobile apps, start
     * the emulator.
     * @param param A {@link StartEnvParam} instance.
     * @return An {@link Flowable} instance.
     */
    @NotNull
    public abstract Flowable<Boolean> rxStartTestEnvironment(@NotNull StartEnvParam param);

    /**
     * Stop the test environement, e.g. by shutting down an emulator.
     * @param param A {@link StopEnvParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public abstract Flowable<Boolean> rxStopTestEnvironment(@NotNull StopEnvParam param);

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