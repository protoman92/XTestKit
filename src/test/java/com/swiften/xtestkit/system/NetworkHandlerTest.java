package com.swiften.xtestkit.system;

import com.swiften.xtestkit.system.protocol.NetworkHandlerError;
import com.swiften.xtestkit.util.Log;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Created by haipham on 4/7/17.
 */
public class NetworkHandlerTest implements NetworkHandlerError {
    @NotNull private final NetworkHandler HANDLER;
    @NotNull private final ProcessRunner PROCESS_RUNNER;

    {
        HANDLER = spy(NetworkHandler.builder().build());

        /* Return this processRunner when we call HANDLER.processRunner() */
        PROCESS_RUNNER = spy(ProcessRunner.builder().build());
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(PROCESS_RUNNER).when(HANDLER).processRunner();
    }

    @AfterMethod
    public void afterMethod() {
        reset(PROCESS_RUNNER, HANDLER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_checkPortWithError_shouldRerunUntilCorrect() {
        try {
            // Setup
            int tries = 10;
            doReturn(false).when(HANDLER).isPortAvailable(anyString(), anyInt());
            doReturn(true).when(HANDLER).isPortAvailable(anyString(), eq(tries));
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            HANDLER.rxCheckUntilPortAvailable(1).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertEquals(TestUtil.getFirstNextEvent(subscriber), Integer.valueOf(tries));
            verify(PROCESS_RUNNER, times(tries)).execute(anyString());
            verify(PROCESS_RUNNER, times(tries)).rxExecute(anyString());
            verify(HANDLER, times(tries)).isPortAvailable(anyString(), anyInt());
            verify(HANDLER, times(tries)).processRunner();
            verify(HANDLER, times(tries)).cmListAllPorts();
            verify(HANDLER, times(tries)).rxCheckPortAvailable(anyInt());
            verify(HANDLER, times(tries)).rxCheckUntilPortAvailable(anyInt());
            verifyNoMoreInteractions(PROCESS_RUNNER);
            verifyNoMoreInteractions(HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_checkPortAvailable_shouldSucceed() {
        try {
            // Setup
            doReturn("").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            HANDLER.rxCheckUntilPortAvailable(0).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertEquals(TestUtil.getFirstNextEvent(subscriber), Integer.valueOf(0));
            verify(PROCESS_RUNNER).execute(anyString());
            verify(PROCESS_RUNNER).rxExecute(anyString());
            verify(HANDLER).processRunner();
            verify(HANDLER).cmListAllPorts();
            verify(HANDLER).rxCheckPortAvailable(anyInt());
            verify(HANDLER).rxCheckUntilPortAvailable(anyInt());
            verify(HANDLER).isPortAvailable(anyString(), anyInt());
            verifyNoMoreInteractions(PROCESS_RUNNER);
            verifyNoMoreInteractions(HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_checkPortWithNoAvailable_shouldThrow() {
        try {
            // Setup
            int tries = 10;
            doReturn(false).when(HANDLER).isPortAvailable(anyString(), anyInt());
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            HANDLER.rxCheckUntilPortAvailable(1, tries).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertErrorMessage(NO_PORT_AVAILABLE);
            subscriber.assertNotComplete();
            verify(PROCESS_RUNNER, times(tries)).execute(anyString());
            verify(PROCESS_RUNNER, times(tries)).rxExecute(anyString());
            verify(HANDLER, times(tries)).isPortAvailable(anyString(), anyInt());
            verify(HANDLER, times(tries)).processRunner();
            verify(HANDLER, times(tries)).cmListAllPorts();
            verify(HANDLER, times(tries)).rxCheckPortAvailable(anyInt());

            /* tries + 1 because the last iteration returns an error Flowable,
             * so the other methods are only run 1 * tries times */
            verify(HANDLER, times(tries + 1)).rxCheckUntilPortAvailable(anyInt(), anyInt());

            verifyNoMoreInteractions(PROCESS_RUNNER);
            verifyNoMoreInteractions(HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_checkPortAvailable_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        HANDLER.rxCheckUntilPortAvailable(4723, 4725).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }
}
