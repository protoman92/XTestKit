package org.swiften.xtestkit.base;

import org.swiften.xtestkit.base.capability.BaseCap;
import org.swiften.xtestkit.base.capability.type.CapType;
import org.swiften.xtestkit.base.element.action.tap.type.TapType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;
import org.swiften.xtestkit.base.type.BaseEngineErrorType;
import org.swiften.xtestkit.base.type.PlatformType;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.system.ProcessRunner;
import org.swiften.xtestkit.base.element.locator.general.xpath.Attribute;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.system.NetworkHandler;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import org.openqa.selenium.WebDriver;

import static org.testng.Assert.*;

import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by haipham on 3/20/17.
 */
public final class BaseEngineTest implements BaseEngineErrorType {
    @NotNull private final WebDriver DRIVER;
    @NotNull private final CapType CAPABILITY;
    @NotNull private final MockEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final RetryType RETRY;
    private final int TRIES;

    {
        ENGINE = spy(new MockEngine.Builder()
            .withPlatformView(mock(PlatformView.class))
            .build());

        /* Return this capability when we cann ENGINE.capabilityType() */
        CAPABILITY = mock(CapType.class);

        /* Return this processRunner when we call ENGINE.processRunner() */
        PROCESS_RUNNER = spy(ProcessRunner.builder().build());

        /* Return this networkHandler when we call ENGINE.networkHandler().
         * On the other hand, return ENGINE when we call
         * NETWORK_HANDLER.processRunner() */
        NETWORK_HANDLER = spy(NetworkHandler.builder().build());

        /* We initialize a driver here in order to access a common mock that
         * stores call counts on its methods. Tests that require the driver()
         * method to throw an Exception should stub the method themselves */
        DRIVER = mock(WebDriver.class);

        /* Use this parameter when a RetryType is needed */
        RETRY = mock(RetryType.class);

        /* The number of tries for certain test */
        TRIES = 10;
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(CAPABILITY).when(ENGINE).capabilityType();
        doReturn(DRIVER).when(ENGINE).driver();
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();
        doReturn(NETWORK_HANDLER).when(ENGINE).networkHandler();
        doReturn(TRIES).when(RETRY).retries();
    }

    @AfterMethod
    public void afterMethod() {
        reset(
            CAPABILITY,
            DRIVER,
            ENGINE,
            PROCESS_RUNNER,
            NETWORK_HANDLER
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
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
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
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
    }
    //endregion

    static class MockEngine extends BaseEngine<WebDriver> {
        @Override
        public <P extends TapType & RetryType> void tap(@NotNull P param) {}

        @NotNull
        @Override
        public XPath.Builder newXPathBuilder() {
            PlatformType platform = mock(PlatformType.class);
            when(platform.enabledAttribute()).thenReturn(Attribute.single("enabled"));
            when(platform.hintAttribute()).thenReturn(Attribute.single("hint"));
            when(platform.textAttribute()).thenReturn(Attribute.single("text"));
            return XPath.builder(platform);
        }

        @NotNull
        @Override
        public Flowable<Boolean> rxSwipeOnce(@NotNull SwipeType param) {
            return Flowable.empty();
        }

        static final class Builder extends BaseEngine.Builder<MockEngine> {
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
}
