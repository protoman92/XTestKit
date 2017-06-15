package org.swiften.xtestkit.base;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.xtestkit.base.capability.BaseEngineCapability;
import org.swiften.xtestkit.base.capability.EngineCapabilityType;
import org.swiften.xtestkit.base.element.choice.ChoiceSelectorType;
import org.swiften.xtestkit.base.element.choice.ChoiceType;
import org.swiften.xtestkit.base.element.date.DateActionType;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DateType;
import org.swiften.xtestkit.base.element.input.KeyboardActionType;
import org.swiften.xtestkit.base.element.search.SearchActionType;
import org.swiften.xtestkit.base.element.swipe.SwipeParamType;
import org.swiften.xtestkit.base.element.switcher.SwitcherActionType;
import org.swiften.xtestkit.base.element.tap.TapParamType;
import org.swiften.xtestkit.base.type.*;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.swiften.xtestkitcomponents.platform.PlatformProviderType;
import org.swiften.xtestkitcomponents.system.network.NetworkHandler;
import org.swiften.xtestkitcomponents.system.process.ProcessRunner;
import org.swiften.xtestkit.util.TestMessageType;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by haipham on 3/20/17.
 */
@SuppressWarnings("MessageMissingOnTestNGAssertion")
public final class EngineTest implements BaseErrorType, TestMessageType {
    @NotNull private final WebDriver DRIVER;
    @NotNull private final EngineCapabilityType CAPABILITY;
    @NotNull private final MockEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final RetryType RETRY;
    private final int TRIES;

    {
        ENGINE = spy(new MockEngine.Builder().build());

        /* Return this capability when we cann ENGINE.capabilityType() */
        CAPABILITY = mock(EngineCapabilityType.class);

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
        subscriber.assertErrorMessage(NOT_AVAILABLE);
        subscriber.assertNotComplete();
        verify(ENGINE, never()).driver(any(), any());
        verify(ENGINE).rxa_startDriver(any());
        verify(ENGINE).capabilityType();
        verify(ENGINE).capabilities();
        verifyNoMoreInteractions(ENGINE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_unableToStartDriver_shouldThrow() {
        // Setup
        doReturn(true).when(CAPABILITY).isComplete(any());
        doThrow(new RuntimeException(NOT_AVAILABLE)).when(ENGINE).driver(any(), any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxa_startDriver(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(NOT_AVAILABLE);
        subscriber.assertNotComplete();
        verify(ENGINE, times(TRIES + 1)).driver(any(), any());
        verify(ENGINE).rxa_startDriver(any());
        verify(ENGINE).capabilityType();
        verify(ENGINE).capabilities();
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

    interface TestDateActionType extends DateActionType<WebDriver> {
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

    interface TestPlatformProviderType extends PlatformProviderType {
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

    interface TestChoiceSelectorType extends ChoiceSelectorType<WebDriver> {
        @NotNull
        @Override
        default Flowable<Boolean> rxa_selectGeneralChoice(@NotNull ChoiceType param) {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    interface TestKeyboardActionType extends KeyboardActionType<WebDriver> {
        @Override
        default void hideKeyboard() {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    interface TestSearchActionType extends SearchActionType {
        @NotNull
        @Override
        default Flowable<WebElement> rxe_textClear() {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    interface TestSwitcherActionType extends SwitcherActionType {
        @NotNull
        @Override
        default String switcherOnValue() {
            return "1";
        }

        @NotNull
        @Override
        default String switcherOffValue() {
            return "0";
        }

        @NotNull
        @Override
        default String switcherValue(@NotNull WebElement element) {
            return element.getAttribute("value");
        }
    }

    static class MockEngine extends Engine<WebDriver> implements
        TestChoiceSelectorType,
        TestDateActionType,
        TestLocatorType,
        TestKeyboardActionType,
        TestPlatformProviderType,
        TestSearchActionType,
        TestSwitcherActionType
    {
        @NotNull
        @Override
        public PlatformView platformView() {
            return mock(PlatformView.class);
        }

        @Override
        public <P extends TapParamType & RetryType> void tap(@NotNull P param) {}

        @NotNull
        @Override
        public Flowable<Boolean> rxa_swipeOnce(@NotNull SwipeParamType param) {
            return Flowable.empty();
        }

        static final class Builder extends Engine.Builder<MockEngine> {
            Builder() {
                super(new MockEngine(), new MockEngineCapability.Builder());
            }
        }
    }

    static class MockEngineCapability extends BaseEngineCapability {
        @NotNull
        @Override
        public PlatformType platform() {
            return mock(PlatformType.class);
        }

        static final class Builder extends BaseEngineCapability.Builder<MockEngineCapability> {
            Builder() {
                super(new MockEngineCapability());
            }
        }
    }
}
