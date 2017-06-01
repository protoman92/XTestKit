package org.swiften.xtestkit.base;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.xtestkit.base.capability.BaseCapability;
import org.swiften.xtestkit.base.capability.type.CapType;
import org.swiften.xtestkit.base.element.choice.BaseChoiceSelectorType;
import org.swiften.xtestkit.base.element.choice.ChoiceType;
import org.swiften.xtestkit.base.element.date.BaseDateActionType;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DateType;
import org.swiften.xtestkit.base.element.input.BaseKeyboardActionType;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.swiften.xtestkit.base.element.tap.TapType;
import org.swiften.xtestkit.base.type.EngineErrorType;
import org.swiften.xtestkit.base.type.PlatformContainerType;
import org.swiften.xtestkit.base.type.PlatformType;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.system.network.NetworkHandler;
import org.swiften.xtestkit.system.process.ProcessRunner;
import org.swiften.xtestkit.util.TestMessageType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by haipham on 3/20/17.
 */
@SuppressWarnings("MessageMissingOnTestNGAssertion")
public final class EngineTest implements EngineErrorType, TestMessageType {
    @NotNull private final WebDriver DRIVER;
    @NotNull private final CapType CAPABILITY;
    @NotNull private final MockEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final RetryType RETRY;
    private final int TRIES;

    {
        ENGINE = spy(new MockEngine.Builder().build());

        /* Return this capability when we cann ENGINE.capabilityType() */
        CAPABILITY = mock(CapType.class);

        /* Return this processRunner when we call ENGINE.processRunner() */
        PROCESS_RUNNER = spy(new ProcessRunner());

        /* Return this networkHandler when we call ENGINE.networkHandler().
         * On the other hand, return ENGINE when we call
         * NETWORK_HANDLER.processRunner() */
        NETWORK_HANDLER = spy(new NetworkHandler());

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

    //region Start Driver
    @Test
    @SuppressWarnings("unchecked")
    public void test_startDriverWithWrongConfigs_shouldThrow() {
        // Setup
        doReturn(false).when(CAPABILITY).isComplete(any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxa_startDriver(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(INSUFFICIENT_SETTINGS);
        subscriber.assertNotComplete();
        verify(ENGINE, never()).driver(any(), any());
        verify(ENGINE).rxa_startDriver(any());
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
        ENGINE.rxa_startDriver(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(DRIVER_UNAVAILABLE);
        subscriber.assertNotComplete();
        verify(ENGINE, times(TRIES + 1)).driver(any(), any());
        verify(ENGINE).rxa_startDriver(any());
        verify(ENGINE).capabilityType();
        verify(ENGINE).capabilities();
        verify(ENGINE).browserName();
        verify(ENGINE).address();
        verify(ENGINE).serverUri();
        verifyNoMoreInteractions(ENGINE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_startDriver_shouldSucceed() {
        // Setup
        doReturn(true).when(CAPABILITY).isComplete(any());
        doReturn(mock(WebDriver.class)).when(ENGINE).driver(any(), any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxa_startDriver(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
        verify(ENGINE).rxa_startDriver(any());
        verify(ENGINE).address();
        verify(ENGINE).serverUri();
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
    public void test_stopAvailableDriver_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxa_stopDriver().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
    }
    //endregion

    interface TestDateActionType extends BaseDateActionType<WebDriver> {
        @NotNull
        @Override
        default Flowable<Boolean> rxa_openPicker(@NotNull DateType param,
                                                 @NotNull CalendarUnit unit) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default Flowable<Boolean> rxa_select(@NotNull DateType param,
                                             @NotNull CalendarUnit unit) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default Flowable<Integer> rxe_displayedUnit(@NotNull DateType param,
                                                    @NotNull CalendarUnit unit) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default Flowable<WebElement> rxe_elementLabel(@NotNull DateType param,
                                                      @NotNull CalendarUnit unit) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default String valueString(@NotNull DateType param, @NotNull CalendarUnit unit) {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    interface TestPlatformContainerType extends PlatformContainerType {
        @NotNull
        @Override
        default PlatformType platform() {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default String platformName() {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    interface TestChoiceSelectorType extends BaseChoiceSelectorType<WebDriver> {
        @NotNull
        @Override
        default Flowable<Boolean> rxa_selectGeneralChoice(@NotNull ChoiceType param) {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    interface TestKeyboardActionType extends BaseKeyboardActionType<WebDriver> {
        @Override
        default void hideKeyboard() {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    static class MockEngine extends Engine<WebDriver> implements
        TestChoiceSelectorType,
        TestDateActionType,
        TestKeyboardActionType,
        TestPlatformContainerType
    {
        @NotNull
        @Override
        public PlatformView platformView() {
            return mock(PlatformView.class);
        }

        @Override
        public <P extends TapType & RetryType> void tap(@NotNull P param) {}

        @NotNull
        @Override
        public Flowable<Boolean> rxa_swipeOnce(@NotNull SwipeType param) {
            return Flowable.empty();
        }

        static final class Builder extends Engine.Builder<MockEngine> {
            Builder() {
                super(new MockEngine(), new MockCapability.Builder());
            }
        }
    }

    static class MockCapability extends BaseCapability {
        @NotNull
        @Override
        public PlatformType platform() {
            return mock(PlatformType.class);
        }

        static final class Builder extends BaseCapability.Builder<MockCapability> {
            Builder() {
                super(new MockCapability());
            }
        }
    }
}
