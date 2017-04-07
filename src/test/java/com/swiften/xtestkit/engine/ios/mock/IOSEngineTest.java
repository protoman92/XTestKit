package com.swiften.xtestkit.engine.ios.mock;

import com.swiften.xtestkit.engine.base.protocol.ErrorProtocol;
import com.swiften.xtestkit.engine.mobile.ios.IOSEngine;
import com.swiften.xtestkit.engine.mobile.ios.protocol.IOSErrorProtocol;
import com.swiften.xtestkit.system.ProcessRunner;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/31/17.
 */
public class IOSEngineTest implements ErrorProtocol, IOSErrorProtocol {
    @NotNull private final IOSEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;

    {
        ENGINE = spy(IOSEngine.builder()
            .withDeviceUID("CF6E7ACD-F818-4145-A140-75CF1F229A8C")
            .withDeviceName("iPhone 7 Plus")
            .build());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ENGINE.processRunner());
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();

        /* Shorten the delay for testing */
        doReturn(100L).when(ENGINE).simulatorBootRetryDelay();
    }

    @AfterMethod
    public void afterMethod() {
        reset(ENGINE, PROCESS_RUNNER);
    }

    //region Capabilities Setup
    @Test
    @SuppressWarnings("unchecked")
    public void mock_addInvalidFileName_shouldThrow() {
        // Setup
        doReturn(true).when(ENGINE).hasAllRequiredInformation();
        doReturn(false).when(ENGINE).hasCorrectFileExtension();
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxStartDriver().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(INVALID_APP_EXTENSION);
        subscriber.assertNotComplete();
        verify(ENGINE).hasCorrectFileExtension();
        verify(ENGINE).rxHasCorrectFileExtension();
        verify(ENGINE).rxHasAllRequiredInformation();
        verify(ENGINE).rxStartDriver();

        try {
            ENGINE.driver();
        } catch (Exception e) {
            assertEquals(e.getMessage(), DRIVER_UNAVAILABLE);
        }
    }
    //endregion

    //region Start Simulator
    @Test
    @SuppressWarnings("unchecked")
    public void mock_startSimulatorWithError_shouldThrow() {
        try {
            // Setup
            String start = ENGINE.cmStartSimulator();
            TestSubscriber subscriber = TestSubscriber.create();
            doThrow(new RuntimeException()).when(PROCESS_RUNNER).execute(eq(start));

            // When
            ENGINE.rxStartSimulator().subscribe(subscriber);

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(RuntimeException.class);
            subscriber.assertNotComplete();
            verify(ENGINE, atLeastOnce()).rxCheckSimulatorBooted();
            verify(PROCESS_RUNNER, atLeastOnce()).execute(anyString());
            verify(PROCESS_RUNNER, atLeastOnce()).rxExecute(anyString());
            verifyNoMoreInteractions(PROCESS_RUNNER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_startSimulator_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStartSimulator().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertTrue(TestUtil.getFirstNextEvent(subscriber));
            verify(ENGINE).rxCheckSimulatorBooted();
            verify(PROCESS_RUNNER, times(2)).execute(anyString());
            verify(PROCESS_RUNNER).rxExecute(anyString());
            verifyNoMoreInteractions(PROCESS_RUNNER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Stop Simulator
    @Test
    @SuppressWarnings("unchecked")
    public void mock_stopSimulatorWithError_shouldSucceed() {
        try {
            // Setup
            doThrow(new RuntimeException()).when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStopSimulator().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertTrue(TestUtil.getFirstNextEvent(subscriber));
            verify(ENGINE).rxStopSimulator();
            verify(PROCESS_RUNNER).execute(anyString());
            verify(PROCESS_RUNNER).rxExecute(anyString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion
}
