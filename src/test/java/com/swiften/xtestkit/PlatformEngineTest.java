package com.swiften.xtestkit;

import com.swiften.engine.base.XPath;
import com.swiften.engine.base.param.*;
import com.swiften.engine.base.protocol.EngineError;
import com.swiften.engine.base.protocol.PlatformProtocol;
import com.swiften.engine.base.protocol.PlatformView;
import com.swiften.engine.base.protocol.View;
import com.swiften.engine.base.PlatformEngine;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by haipham on 3/20/17.
 */
public final class PlatformEngineTest implements EngineError {
    @NotNull private final WebDriver DRIVER;
    @NotNull private final MockEngine ENGINE;
    @NotNull private final WebDriver.Navigation NAVIGATION;
    @NotNull private final MockPlatformView PLATFORM_VIEWS;
    private final int ELEMENT_COUNT, TRIES;

    {
        ENGINE = spy(new MockEngine.Builder()
            .withPlatformView(mock(PlatformView.class))
            .build());

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

        /* The number of elements to return for a DRIVER.findElement request */
        ELEMENT_COUNT = 2;

        /* The number of tries for certain tests */
        TRIES = 10;
    }

    @Before
    public void setUp() {
        doReturn(DRIVER).when(ENGINE).driver();
        doReturn(PLATFORM_VIEWS).when(ENGINE).platformView();
        when(DRIVER.navigate()).thenReturn(NAVIGATION);

        when(DRIVER.findElements(any(By.class))).thenReturn(
            Arrays
                .stream(new Object[ELEMENT_COUNT])
                .map(a -> mock(WebElement.class))
                .collect(Collectors.toList())
        );
    }

    @After
    public void tearDown() {
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

        /* Since we are using Completable, we expect that there would not
         * be any onNext event */
        subscriber.assertNoValues();

        verify(ENGINE).createDriverInstance();
    }
    //endregion

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

        /* Since we are using Completable, we expect that there would not
         * be any onNext event */
        subscriber.assertNoValues();
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
    public void mock_navigateBack_shouldSuccess() {
        // Setup
        /* Stub out backNavigationDelay() to avoid long wait */
        long delay = 100;
        doReturn(delay).when(ENGINE).backNavigationDelay();

        TestSubscriber subscriber = TestSubscriber.create();

        Flowable cases = Flowable.range(1, TRIES)
            .flatMap(a -> Flowable.just(a)
                .map(b -> NavigateBack.newBuilder().withTimes(b).build())
                .flatMap(ENGINE::rxNavigateBack)

                /* Check that back navigation has been invoked a times */
                .doOnNext(b -> {
                    verify(NAVIGATION, times(a)).back();
                })
                .doOnComplete(() -> {
                    /* If we reset the driver here, it will no longer return
                     * NAVIGATION when navigate() is called */
                    reset(NAVIGATION);
                }));

        // When
        cases.subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();

        /* No values due to Completable */
        subscriber.assertNoValues();
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
            .withXPath("")
            .build();

        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxElementsByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        List events = (List)((List)subscriber.getEvents().get(0)).get(0);
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(events.size(), 0);

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
            .withXPath("")
            .build();

        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxElementsByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        List events = (List)((List)subscriber.getEvents().get(0)).get(0);
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(events.size(), PLATFORM_VIEWS.VIEW_COUNT * ELEMENT_COUNT);

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
        Object object = ((List)subscriber.getEvents().get(0)).get(0);
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(result).get(anyInt());
        assertTrue(object instanceof WebElement);
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
        Object object = ((List)subscriber.getEvents().get(0)).get(0);
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(result).get(anyInt());
        assertTrue(object instanceof WebElement);
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
        subscriber.assertErrorMessage(noElementsWithText(""));
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
        Object object = ((List)subscriber.getEvents().get(0)).get(0);
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(object instanceof WebElement);
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
        subscriber.assertErrorMessage(noElementsContainingText(""));
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
        Object object = ((List)subscriber.getEvents().get(0)).get(0);
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(object instanceof WebElement);
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
        subscriber.assertErrorMessage(noElementsWithHint(""));
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
        Object object = ((List)subscriber.getEvents().get(0)).get(0);
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(object instanceof WebElement);
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
        subscriber.assertErrorMessage(noElementsContainingHint(""));
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
        Object object = ((List)subscriber.getEvents().get(0)).get(0);
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(object instanceof WebElement);
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
        public XPath.Builder newXPathBuilderInstance() {
            PlatformProtocol platform = mock(PlatformProtocol.class);
            when(platform.enabledAttribute()).thenReturn("enabled");
            when(platform.hintAttribute()).thenReturn("hint");
            when(platform.textAttribute()).thenReturn("text");
            return XPath.newBuilder(platform);
        }

        @NotNull
        @Override
        public Flowable<Boolean> rxStartTestEnvironment(@NotNull StartEnvParam param) {
            return Flowable.empty();
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
