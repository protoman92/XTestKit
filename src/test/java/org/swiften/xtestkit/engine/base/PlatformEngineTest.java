package org.swiften.xtestkit.engine.base;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.swiften.xtestkit.engine.base.capability.BaseCap;
import org.swiften.xtestkit.engine.base.capability.TestCapabilityType;
import org.swiften.xtestkit.engine.base.param.*;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.system.ProcessRunner;
import org.swiften.xtestkit.engine.base.xpath.Attribute;
import org.swiften.xtestkit.engine.base.xpath.XPath;
import org.swiften.xtestkit.system.NetworkHandler;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import static org.testng.Assert.*;

import org.swiften.javautilities.number.NumberTestUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by haipham on 3/20/17.
 */
public final class PlatformEngineTest implements PlatformErrorType {
    @NotNull private final WebDriver DRIVER;
    @NotNull private final TestCapabilityType CAPABILITY;
    @NotNull private final MockEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final PlatformEngine.TextDelegate LOCALIZER;
    @NotNull private final Alert ALERT;
    @NotNull private final WebDriver.Navigation NAVIGATION;
    @NotNull private final WebDriver.TargetLocator TARGET_LOCATOR;
    @NotNull private final MockPlatformView PLATFORM_VIEWS;
    @NotNull private final String LOCALIZED_TEXT;
    @NotNull private final RetryProtocol RETRY;
    private final int ELEMENT_COUNT, TRIES;

    {
        ENGINE = spy(new MockEngine.Builder()
            .withPlatformView(mock(PlatformView.class))
            .build());

        /* Return this capability when we cann ENGINE.capabilityType() */
        CAPABILITY = mock(TestCapabilityType.class);

        /* Return this processRunner when we call ENGINE.processRunner() */
        PROCESS_RUNNER = spy(ProcessRunner.builder().build());

        /* Return this networkHandler when we call ENGINE.networkHandler().
         * On the other hand, return ENGINE when we call
         * NETWORK_HANDLER.processRunner() */
        NETWORK_HANDLER = spy(NetworkHandler.builder().build());

        /* Return this localizer when we call ENGINE.localizer() */
        LOCALIZER = mock(PlatformEngine.TextDelegate.class);
        LOCALIZED_TEXT = "Localized Result";

        /* We initialize a driver here in order to access a common mock that
         * stores call counts on its methods. Tests that require the driver()
         * method to throw an Exception should stub the method themselves */
        DRIVER = mock(WebDriver.class);

        /* We initialize a Navigate object and ask the driver to return it
         * every time driver.navigate() is called */
        NAVIGATION = mock(WebDriver.Navigation.class);

        /* This PlatformView object will be returned by ENGINE. We return
         * an implementation whose getViews() method returns a list of mock
         * WebElement, instead of a mock PlatformView. This way, we do not
         * have to rewrite view login for PlatformView */
        PLATFORM_VIEWS = spy(new MockPlatformView());

        /* Return this mock when the driver requests switchTo() */
        TARGET_LOCATOR = mock(WebDriver.TargetLocator.class);

        /*  */
        ALERT = mock(Alert.class);

        /* The number of elements to return for a DRIVER.findElement request */
        ELEMENT_COUNT = 2;

        /* Use this parameter when a RetryProtocol is needed */
        RETRY = mock(RetryProtocol.class);

        /* The number of tries for certain test */
        TRIES = 10;
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(CAPABILITY).when(ENGINE).capabilityType();
        doReturn(DRIVER).when(ENGINE).driver();
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();
        doReturn(NETWORK_HANDLER).when(ENGINE).networkHandler();
        doReturn(PLATFORM_VIEWS).when(ENGINE).platformView();
        doReturn(LOCALIZED_TEXT).when(LOCALIZER).localize(anyString());
        doReturn(Flowable.just(LOCALIZED_TEXT)).when(LOCALIZER).rxLocalize(anyString());
        doReturn(LOCALIZER).when(ENGINE).localizer();
        doReturn(TRIES).when(RETRY).retries();
        when(DRIVER.navigate()).thenReturn(NAVIGATION);
        when(DRIVER.switchTo()).thenReturn(TARGET_LOCATOR);
        when(TARGET_LOCATOR.alert()).thenReturn(ALERT);

        when(DRIVER.findElements(any(By.class))).thenReturn(
            Arrays
                .stream(new Object[ELEMENT_COUNT])
                .map(a -> mock(WebElement.class))
                .collect(Collectors.toList())
        );
    }

    @AfterMethod
    public void afterMethod() {
        reset(
            CAPABILITY,
            DRIVER,
            ENGINE,
            PROCESS_RUNNER,
            NETWORK_HANDLER,
            NAVIGATION,
            PLATFORM_VIEWS
        );
    }

    //region Appium Server
    @Test
    @SuppressWarnings("unchecked")
    public void test_startAppiumServerWithoutCLI_shouldEmitFallback() {
        try {
            // Setup
            doReturn("").when(PROCESS_RUNNER).execute(contains("which appium"));
            doNothing().when(ENGINE).startAppiumOnNewThread(anyString());
            ArgumentCaptor<String> appiumCaptor = ArgumentCaptor.forClass(String.class);
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ENGINE.rxStartLocalAppium(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).processRunner();
            verify(ENGINE).cmWhichAppium();
            verify(ENGINE).cmFallBackAppium();
            verify(ENGINE).appiumStartDelay();
            verify(ENGINE).rxStartLocalAppium(any());
            verify(ENGINE).startAppiumOnNewThread(appiumCaptor.capture());
            verifyNoMoreInteractions(ENGINE);
            assertTrue(appiumCaptor.getValue().contains("appium"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_startAppiumServer_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());

            doReturn(Flowable.just(true))
                .when(NETWORK_HANDLER).rxCheckPortAvailable(any());

            doReturn(100L).when(ENGINE).appiumStartDelay();
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ENGINE.rxStartLocalAppium(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).serverAddress();
            verify(ENGINE).networkHandler();
            verify(ENGINE).cmWhichAppium();
            verify(ENGINE).cmStartLocalAppiumInstance(anyString(), anyInt());
            verify(ENGINE).cmFallBackAppium();
            verify(ENGINE).startAppiumOnNewThread(anyString());
            verify(ENGINE).rxStartLocalAppium(any());
            verify(ENGINE, times(2)).processRunner();
            verify(ENGINE).appiumStartDelay();
            verify(NETWORK_HANDLER, atLeastOnce()).rxCheckUntilPortAvailable(any());
            verify(NETWORK_HANDLER).markPortAsUsed(anyInt());
            verifyNoMoreInteractions(ENGINE);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_startAppiumServers_shouldExecuteSequentially() {
        // Setup
        int tries = 10;
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(1, tries)
            .flatMap(a -> ENGINE.rxStartLocalAppium(RETRY))
            .compose(RxUtil.withCommonSchedulers())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_stopAppiumServer_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ENGINE.rxStopLocalAppiumInstance().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).serverAddress();
            verify(ENGINE).rxStopLocalAppiumInstance();
            verify(ENGINE).networkHandler();
            verifyNoMoreInteractions(ENGINE);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Start Driver
    @Test
    @SuppressWarnings("unchecked")
    public void test_startDriverWithWrongConfigs_shouldThrow() {
        // Setup
        doReturn(false).when(CAPABILITY).isComplete(any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxStartDriver(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(INSUFFICIENT_SETTINGS);
        subscriber.assertNotComplete();
        verify(ENGINE, never()).driver(any(), any());
        verify(ENGINE).rxStartDriver(any());
        verify(ENGINE).capabilityType();
        verify(ENGINE).capabilities();
        verify(ENGINE).browserName();
        verifyNoMoreInteractions(ENGINE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_unableToStartDriver_shouldThrow() {
        // Setup
        doReturn(true).when(CAPABILITY).isComplete(any());
        doThrow(new RuntimeException(DRIVER_UNAVAILABLE)).when(ENGINE).driver(any(), any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxStartDriver(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(DRIVER_UNAVAILABLE);
        subscriber.assertNotComplete();
        verify(ENGINE, times(TRIES + 1)).driver(any(), any());
        verify(ENGINE).rxStartDriver(any());
        verify(ENGINE).capabilityType();
        verify(ENGINE).capabilities();
        verify(ENGINE).browserName();
        verify(ENGINE).serverAddress();
        verify(ENGINE).serverUri();
        verifyNoMoreInteractions(ENGINE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_startDriver_shouldSucceed() {
        // Setup
        doReturn(true).when(CAPABILITY).isComplete(any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxStartDriver(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
        verify(ENGINE).rxStartDriver(any());
        verify(ENGINE).serverAddress();
        verify(ENGINE).serverUri();
        verify(ENGINE).driver(any(), any());
        verify(ENGINE).driver(any(), any());
        verify(ENGINE).capabilities();
        verify(ENGINE).capabilityType();
        verify(ENGINE).browserName();
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion

    //region Alert
    @Test
    @SuppressWarnings("unchecked")
    public void test_acceptAlert_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxAcceptAlert().subscribe(subscriber);

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
        verify(ENGINE).driver();
        verify(ENGINE).rxAcceptAlert();
        verify(ENGINE).rxDismissAlert(any(AlertParam.class));
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion

    //region Stop Driver
    @Test
    @SuppressWarnings("unchecked")
    public void test_stopUnavailableDriver_shouldThrow() {
        // Setup
        doThrow(new RuntimeException(DRIVER_UNAVAILABLE)).when(ENGINE).driver();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxStopDriver().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(DRIVER_UNAVAILABLE);
        subscriber.assertNotComplete();
        verify(ENGINE).rxStopDriver();
        verify(ENGINE).driver();
        verifyNoMoreInteractions(ENGINE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_stopAvailableDriver_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxStopDriver().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
    }
    //endregion

    //region Navigate Back
    @Test
    @SuppressWarnings("unchecked")
    public void test_navigateBackWithNoDriver_shouldThrow() {
        // Setup
        doThrow(new RuntimeException(DRIVER_UNAVAILABLE))
            .when(ENGINE)
            .driver();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        NavigateBack param = NavigateBack.builder()
            .withTimes(1)
            .build();

        // When
        ENGINE.rxNavigateBack(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertErrorMessage(DRIVER_UNAVAILABLE);
        subscriber.assertNoValues();
        subscriber.assertNotComplete();
        verify(DRIVER, never()).navigate();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_navigateBack_shouldSucceed() {
        // Setup
        /* Stub out backNavigationDelay() to avoid long wait */
        long delay = 100;
        doReturn(delay).when(ENGINE).backNavigationDelay();

        int times = NumberTestUtil.randomBetween(1, 5);
        NavigateBack param = NavigateBack.builder().withTimes(times).build();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxNavigateBack(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
        verify(ENGINE).rxNavigateBack(any());
        verify(ENGINE).driver();
        verify(ENGINE).backNavigationDelay();
        verify(NAVIGATION, times(times)).back();
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion

    //region Element By XPATH
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsByXPathWithNoDriver_shouldThrow() {
        // Setup
        doThrow(new RuntimeException(DRIVER_UNAVAILABLE)).when(ENGINE).driver();
        ByXPath param = ByXPath.builder().build();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxElementsByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(DRIVER_UNAVAILABLE);
        subscriber.assertNoValues();
        subscriber.assertNotComplete();
        verify(ENGINE).driver();
        verify(DRIVER, never()).findElements(any(By.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_failToFindElements_shouldReturnEmptyList() {
        // Setup
        when(DRIVER.findElements(any(By.ByXPath.class)))
            .thenThrow(new RuntimeException());

        List<View> views = PLATFORM_VIEWS.allViews();

        ByXPath param = ByXPath.builder()
            .withClasses(views)
            .withXPath(XPath.EMPTY)
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxElementsByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(RxTestUtil.<List>getFirstNextEvent(subscriber).size(), 0);
        views.forEach(a -> verify(a).className());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsByXPath_shouldSucceed() {
        // Setup
        List<View> views = PLATFORM_VIEWS.allViews();

        ByXPath param = ByXPath.builder()
            .withClasses(views)
            .withXPath(XPath.EMPTY)
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxElementsByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();

        assertEquals(
            RxTestUtil.<List>getFirstNextEvent(subscriber).size(),
            PLATFORM_VIEWS.VIEW_COUNT * ELEMENT_COUNT);

        views.forEach(a -> {
            verify(a).className();
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementByXPathWithNoElement_shouldThrow() {
        // Setup
        List<WebElement> result = mock(ArrayList.class);
        when(DRIVER.findElements(any(By.ByXPath.class))).thenReturn(result);

        ByXPath param = mock(ByXPath.class);
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxElementByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(null);
        subscriber.assertNoValues();
        subscriber.assertNotComplete();
        verify(result, never()).get(anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementByXPathWithParent_shouldSuccess() {
        // Setup
        WebElement element = mock(WebElement.class);
        List<WebElement> result = spy(new ArrayList<WebElement>());
        result.add(element);

        Flowable<List<WebElement>> parent = Flowable.just(result);

        ByXPath param = ByXPath.builder()
            .withParent(parent)
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxElementByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(result).get(anyInt());
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementByXPathWithNoParent_shouldSucceed() {
        // Setup
        WebElement element = mock(WebElement.class);
        List<WebElement> result = spy(new ArrayList<WebElement>());
        result.add(element);

        doReturn(Flowable.just(result))
            .when(ENGINE).rxElementsByXPath(any(ByXPath.class));

        ByXPath param = ByXPath.builder()
            .withClasses(PLATFORM_VIEWS.allViews())
            .withError("")
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxElementByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(result).get(anyInt());
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
    }
    //endregion

    //region Element With Text
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsWithText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        when(param.text()).thenReturn("");

        // When
        ENGINE.rxElementsWithText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementWithTextWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        when(param.text()).thenReturn("");

        when(DRIVER.findElements(any(By.ByXPath.class)))
            .thenReturn(Collections.emptyList());

        // When
        ENGINE.rxElementWithText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(noElementsWithText(LOCALIZED_TEXT));
        subscriber.assertNotComplete();
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementWithText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        when(param.text()).thenReturn("");

        // When
        ENGINE.rxElementWithText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }
    //endregion

    //region Element Containing Text
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsContainingText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        when(param.text()).thenReturn("");

        // When
        ENGINE.rxElementsContainingText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementContainingTextWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        when(param.text()).thenReturn("");

        when(DRIVER.findElements(any(By.ByXPath.class)))
            .thenReturn(Collections.emptyList());

        // When
        ENGINE.rxElementContainingText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(noElementsContainingText(LOCALIZED_TEXT));
        subscriber.assertNotComplete();
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementContainingText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        when(param.text()).thenReturn("");

        // When
        ENGINE.rxElementContainingText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }
    //endregion

    //region Element With Hint
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsWithHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        when(param.hint()).thenReturn("");

        // When
        ENGINE.rxElementsWithHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementWithHintWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        when(param.hint()).thenReturn("");

        when(DRIVER.findElements(any(By.ByXPath.class)))
            .thenReturn(Collections.emptyList());

        // When
        ENGINE.rxElementWithHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(noElementsWithHint(LOCALIZED_TEXT));
        subscriber.assertNotComplete();
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementWithHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        when(param.hint()).thenReturn("");

        // When
        ENGINE.rxElementWithHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }
    //endregion

    //region Element Containing Hint
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsContainingHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        when(param.hint()).thenReturn("");

        // When
        ENGINE.rxElementsContainingHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementContainingHintWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        when(param.hint()).thenReturn("");

        when(DRIVER.findElements(any(By.ByXPath.class)))
            .thenReturn(Collections.emptyList());

        // When
        ENGINE.rxElementContainingHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(noElementsContainingHint(LOCALIZED_TEXT));
        subscriber.assertNotComplete();
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementContainingHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        when(param.hint()).thenReturn("");

        // When
        ENGINE.rxElementContainingHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }
    //endregion

    static class MockPlatformView extends PlatformView {
        @NotNull private final Random RAND;
        final int VIEW_COUNT;

        {
            RAND = new Random();

            /* The number of View to pass to PlatformView */
            VIEW_COUNT = 1000;
        }

        @NotNull
        @Override
        protected View[] getViews() {
            return Arrays
                .stream(new Object[VIEW_COUNT])
                .map(a -> spy(new MockView(RAND)))
                .toArray(View[]::new);
        }
    }

    static class MockEngine extends PlatformEngine<WebDriver> {
        @NotNull
        @Override
        protected WebDriver driver(@NotNull String serverUrl,
                                   @NotNull DesiredCapabilities capabilities) {
            return mock(WebDriver.class);
        }

        @NotNull
        @Override
        public XPath.Builder newXPathBuilderInstance() {
            PlatformProtocol platform = mock(PlatformProtocol.class);

            when(platform.enabledAttribute())
                .thenReturn(Attribute.withSingleAttribute("enabled"));

            when(platform.hintAttribute())
                .thenReturn(Attribute.withSingleAttribute("hint"));

            when(platform.textAttribute())
                .thenReturn(Attribute.withSingleAttribute("text"));

            return XPath.builder(platform);
        }

        static final class Builder extends PlatformEngine.Builder<MockEngine> {
            Builder() {
                super(new MockEngine(), new MockCap.Builder());
            }
        }
    }

    static class MockCap extends BaseCap {
        static final class Builder extends BaseCap.Builder<MockCap> {
            Builder() {
                super(new MockCap());
            }
        }
    }

    static class MockView implements View {
        @NotNull private final Random RAND;

        MockView(@NotNull Random rand) {
            RAND = rand;
        }

        @NotNull
        @Override
        public String className() {
            return getClass().getSimpleName();
        }

        @Override
        public boolean hasText() {
            return RAND.nextBoolean();
        }

        @Override
        public boolean isEditable() {
            return RAND.nextBoolean();
        }

        @Override
        public boolean isClickable() {
            return RAND.nextBoolean();
        }

        @NotNull
        @Override
        public String toString() {
            String base = "";
            base += ("\nhasText: " + hasText());
            base += ("\nisClickable: " + isClickable());
            base += ("\nisEditable: " + isEditable());
            return base;
        }
    }
}
