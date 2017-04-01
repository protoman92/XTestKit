package com.swiften.xtestkit.engine;

import com.swiften.xtestkit.engine.base.XPath;
import com.swiften.xtestkit.engine.base.param.*;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.base.protocol.ErrorProtocol;
import com.swiften.xtestkit.engine.base.protocol.PlatformProtocol;
import com.swiften.xtestkit.engine.base.protocol.PlatformView;
import com.swiften.xtestkit.engine.base.protocol.View;
import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by haipham on 3/20/17.
 */
public final class PlatformEngineTest implements ErrorProtocol {
    @NotNull private final WebDriver DRIVER;
    @NotNull private final MockEngine ENGINE;
    @NotNull private final PlatformEngine.TextDelegate LOCALIZER;
    @NotNull private final Alert ALERT;
    @NotNull private final WebDriver.Navigation NAVIGATION;
    @NotNull private final WebDriver.TargetLocator TARGET_LOCATOR;
    @NotNull private final MockPlatformView PLATFORM_VIEWS;
    @NotNull private final String LOCALIZED_TEXT;
    private final int ELEMENT_COUNT, TRIES;

    {
        ENGINE = spy(new MockEngine.Builder()
            .withPlatformView(mock(PlatformView.class))
            .build());

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

        /* The number of tries for certain tests */
        TRIES = 10;
    }

    @Before
    public void before() {
        doReturn(DRIVER).when(ENGINE).driver();
        doReturn(PLATFORM_VIEWS).when(ENGINE).platformView();
        doReturn(LOCALIZED_TEXT).when(LOCALIZER).localize(anyString());
        doReturn(Flowable.just(LOCALIZED_TEXT)).when(LOCALIZER).rxLocalize(anyString());
        doReturn(LOCALIZER).when(ENGINE).localizer();
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

    @After
    public void after() {
        reset(DRIVER, ENGINE, NAVIGATION, PLATFORM_VIEWS);
    }

    //region Engine Setup
    @Test
    public void mock_createEngine_shouldHaveCorrectCapabilities() {
        // Setup
        // When
        // Then
        assertTrue(ENGINE.hasAllRequiredInformation());
    }
    //endregion

    //region Start Driver
    @Test
    @SuppressWarnings("unchecked")
    public void mock_startDriverWithWrongConfigs_shouldThrow() {
        // Setup
        doReturn(false).when(ENGINE).hasAllRequiredInformation();
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxStartDriver().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(INSUFFICIENT_SETTINGS);
        subscriber.assertNotComplete();
        verify(ENGINE, never()).createDriverInstance();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_unableToStartDriver_shouldThrow() {
        // Setup
        doThrow(new RuntimeException(DRIVER_UNAVAILABLE))
            .when(ENGINE)
            .createDriverInstance();

        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxStartDriver().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(DRIVER_UNAVAILABLE);
        subscriber.assertNotComplete();
        verify(ENGINE).createDriverInstance();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_startDriver_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxStartDriver().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));

        verify(ENGINE).createDriverInstance();
    }
    //endregion

    @Test
    @SuppressWarnings("unchecked")
    public void mock_acceptAlert_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxAcceptAlert().subscribe(subscriber);

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(ENGINE).rxDismissAlert(any(AlertParam.class));
    }

    //region Stop Driver
    @Test
    @SuppressWarnings("unchecked")
    public void mock_stopUnavailableDriver_shouldThrow() {
        // Setup
        doThrow(new RuntimeException(DRIVER_UNAVAILABLE)).when(ENGINE).driver();
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxStopDriver().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(DRIVER_UNAVAILABLE);
        subscriber.assertNotComplete();
        verify(ENGINE).driver();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_stopAvailableDriver_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxStopDriver().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
    }
    //endregion

    //region Navigate Back
    @Test
    @SuppressWarnings("unchecked")
    public void mock_navigateBackWithNoDriver_shouldThrow() {
        // Setup
        doThrow(new RuntimeException(DRIVER_UNAVAILABLE))
            .when(ENGINE)
            .driver();

        TestSubscriber subscriber = TestSubscriber.create();

        NavigateBack param = NavigateBack.newBuilder()
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
    public void mock_navigateBack_shouldSucceed() {
        // Setup
        /* Stub out backNavigationDelay() to avoid long wait */
        long delay = 100;
        doReturn(delay).when(ENGINE).backNavigationDelay();

        int times = TestUtil.randomBetween(1, 100);
        NavigateBack param = NavigateBack.newBuilder().withTimes(times).build();
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxNavigateBack(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(NAVIGATION, times(times)).back();
    }
    //endregion

    //region Element By XPATH
    @Test
    @SuppressWarnings("unchecked")
    public void mock_elementsByXPathWithNoDriver_shouldThrow() {
        // Setup
        doThrow(new RuntimeException(DRIVER_UNAVAILABLE)).when(ENGINE).driver();
        ByXPath param = ByXPath.newBuilder().build();
        TestSubscriber subscriber = TestSubscriber.create();

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
    public void mock_failToFindElements_shouldReturnEmptyList() {
        // Setup
        when(DRIVER.findElements(any(By.ByXPath.class)))
            .thenThrow(new RuntimeException());

        List<View> views = PLATFORM_VIEWS.allViews();

        ByXPath param = ByXPath.newBuilder()
            .withClasses(views)
            .withXPath(XPath.EMPTY)
            .build();

        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxElementsByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(TestUtil.<List>getFirstNextEvent(subscriber).size(), 0);

        views.forEach(a -> {
            verify(a).className();
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_elementsByXPath_shouldSucceed() {
        // Setup
        List<View> views = PLATFORM_VIEWS.allViews();

        ByXPath param = ByXPath.newBuilder()
            .withClasses(views)
            .withXPath(XPath.EMPTY)
            .build();

        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxElementsByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();

        assertEquals(
            TestUtil.<List>getFirstNextEvent(subscriber).size(),
            PLATFORM_VIEWS.VIEW_COUNT * ELEMENT_COUNT);

        views.forEach(a -> {
            verify(a).className();
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_elementByXPathWithNoElement_shouldThrow() {
        // Setup
        List<WebElement> result = mock(ArrayList.class);
        when(DRIVER.findElements(any(By.ByXPath.class))).thenReturn(result);

        ByXPath param = mock(ByXPath.class);
        TestSubscriber subscriber = TestSubscriber.create();

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
    public void mock_elementByXPathWithParent_shouldSuccess() {
        // Setup
        WebElement element = mock(WebElement.class);
        List<WebElement> result = spy(new ArrayList<WebElement>());
        result.add(element);

        Flowable<List<WebElement>> parent = Flowable.just(result);

        ByXPath param = ByXPath.newBuilder()
            .withParent(parent)
            .build();

        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxElementByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(result).get(anyInt());
        assertTrue(TestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_elementByXPathWithNoParent_shouldSucceed() {
        // Setup
        WebElement element = mock(WebElement.class);
        List<WebElement> result = spy(new ArrayList<WebElement>());
        result.add(element);

        doReturn(Flowable.just(result))
            .when(ENGINE).rxElementsByXPath(any(ByXPath.class));

        ByXPath param = ByXPath.newBuilder()
            .withClasses(PLATFORM_VIEWS.allViews())
            .withError("")
            .build();

        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxElementByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(result).get(anyInt());
        assertTrue(TestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
    }
    //endregion

    //region Element With Text
    @Test
    @SuppressWarnings("unchecked")
    public void mock_elementsWithText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
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
    public void mock_elementWithTextWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
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
    public void mock_elementWithText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
        TextParam param = mock(TextParam.class);
        when(param.text()).thenReturn("");

        // When
        ENGINE.rxElementWithText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }
    //endregion

    //region Element Containing Text
    @Test
    @SuppressWarnings("unchecked")
    public void mock_elementsContainingText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
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
    public void mock_elementContainingTextWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
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
    public void mock_elementContainingText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
        TextParam param = mock(TextParam.class);
        when(param.text()).thenReturn("");

        // When
        ENGINE.rxElementContainingText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }
    //endregion

    //region Element With Hint
    @Test
    @SuppressWarnings("unchecked")
    public void mock_elementsWithHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
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
    public void mock_elementWithHintWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
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
    public void mock_elementWithHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
        HintParam param = mock(HintParam.class);
        when(param.hint()).thenReturn("");

        // When
        ENGINE.rxElementWithHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(ENGINE).rxElementsByXPath(any(ByXPath.class));
        verify(ENGINE).rxElementByXPath(any(ByXPath.class));
    }
    //endregion

    //region Element Containing Hint
    @Test
    @SuppressWarnings("unchecked")
    public void mock_elementsContainingHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
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
    public void mock_elementContainingHintWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
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
    public void mock_elementContainingHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();
        HintParam param = mock(HintParam.class);
        when(param.hint()).thenReturn("");

        // When
        ENGINE.rxElementContainingHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
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
        protected WebDriver createDriverInstance() {
            return mock(WebDriver.class);
        }

        @NotNull
        @Override
        public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
            return Flowable.empty();
        }

        @NotNull
        @Override
        public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
            return Flowable.empty();
        }

        @NotNull
        @Override
        public Flowable<Boolean> rxBefore(@NotNull BeforeParam param) {
            return Flowable.empty();
        }

        @NotNull
        @Override
        public Flowable<Boolean> rxAfter(@NotNull AfterParam param) {
            return Flowable.empty();
        }

        @NotNull
        @Override
        public XPath.Builder newXPathBuilderInstance() {
            PlatformProtocol platform = mock(PlatformProtocol.class);
            when(platform.enabledAttribute()).thenReturn("enabled");
            when(platform.hintAttribute()).thenReturn("hint");
            when(platform.textAttribute()).thenReturn("text");
            return XPath.newBuilder(platform);
        }

        static final class Builder extends PlatformEngine.Builder<MockEngine> {
            @NotNull
            @Override
            protected MockEngine createEngineInstance() {
                return new MockEngine();
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
