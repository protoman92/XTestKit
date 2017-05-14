package org.swiften.xtestkit.system;

import org.swiften.xtestkit.base.type.RetryType;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.xtestkit.system.type.NetworkHandlerErrorType;
import org.swiften.xtestkit.system.type.PortType;
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
public final class NetworkHandlerTest implements NetworkHandlerErrorType {
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
    public void test_checkPortWithError_shouldRerunUntilCorrect() {
        try {
            // Setup
            int tries = 10;
            doReturn(false).when(HANDLER).isPortAvailable(anyString(), anyInt());
            doReturn(true).when(HANDLER).isPortAvailable(anyString(), eq(tries));
            CheckPortParam param = new CheckPortParam(1);
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            HANDLER.rxCheckUntilPortAvailable(param).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertEquals(RxTestUtil.firstNextEvent(subscriber), Integer.valueOf(tries));
            verify(HANDLER, times(tries)).isPortAvailable(anyString(), anyInt());
            verify(HANDLER, times(tries)).processRunner();
            verify(HANDLER, times(tries)).cmListAllPorts();
            verify(HANDLER, times(tries)).rxCheckPortAvailable(any());
            verify(HANDLER, times(tries)).rxCheckUntilPortAvailable(any());
            verifyNoMoreInteractions(HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_checkPortAvailable_shouldSucceed() {
        try {
            // Setup
            doReturn("").when(PROCESS_RUNNER).execute(anyString());
            CheckPortParam param = new CheckPortParam(0);
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            HANDLER.rxCheckUntilPortAvailable(param).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertEquals(RxTestUtil.firstNextEvent(subscriber), Integer.valueOf(0));
            verify(HANDLER).processRunner();
            verify(HANDLER).cmListAllPorts();
            verify(HANDLER).rxCheckPortAvailable(any());
            verify(HANDLER).rxCheckUntilPortAvailable(any());
            verify(HANDLER).isPortAvailable(anyString(), anyInt());
            verifyNoMoreInteractions(HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static final class CheckPortParam implements
        PortType,
        RetryType
    {
        private final int PORT;

        CheckPortParam(int port) {
            PORT = port;
        }

        //region PortType
        @Override
        public int port() {
            return PORT;
        }
        //endregion
    }
}
