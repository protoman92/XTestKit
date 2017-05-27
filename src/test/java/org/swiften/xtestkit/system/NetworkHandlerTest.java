package org.swiften.xtestkit.system;

import io.reactivex.Flowable;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.type.RetryType;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.xtestkit.system.network.NetworkHandler;
import org.swiften.xtestkit.system.network.type.MaxPortType;
import org.swiften.xtestkit.system.network.type.NetworkHandlerErrorType;
import org.swiften.xtestkit.system.network.type.PortStepType;
import org.swiften.xtestkit.system.network.type.PortType;
import org.swiften.xtestkit.system.process.ProcessRunner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Created by haipham on 4/7/17.
 */
public final class NetworkHandlerTest implements NetworkHandlerErrorType {
    @NotNull private final NetworkHandler HANDLER;
    @NotNull private final ProcessRunner PROCESS_RUNNER;

    {
        HANDLER = spy(new NetworkHandler());

        /* Return this processRunner when we call HANDLER.processRunner() */
        PROCESS_RUNNER = spy(new ProcessRunner());
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(PROCESS_RUNNER).when(HANDLER).processRunner();
    }

    @AfterMethod
    public void afterMethod() {
        reset(PROCESS_RUNNER, HANDLER);
        HANDLER.clearUsedPorts();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_checkPortWithError_shouldRerunUntilCorrect() {
        try {
            // Setup
            int tries = 10;
            doReturn(false).when(HANDLER).isPortAvailable(anyString(), anyInt());
            doReturn(true).when(HANDLER).isPortAvailable(anyString(), eq(tries));
            CheckPort param = new CheckPort(1);
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
            verify(HANDLER).rxCheckUntilPortAvailable(any());
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
            CheckPort param = new CheckPort(0);
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
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_checkPortUntilAvailable_shouldSucceed() {
        // Setup
        int minPort = 4723, tries = 10;
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(minPort, tries)
            .doOnNext(LogUtil::println)
            .map(CheckPort::new)
            .concatMap(HANDLER::rxCheckUntilPortAvailable)
            .doOnNext(a -> LogUtil.printfThread("Port %d", a))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        List<Integer> usedPorts = new LinkedList<>(HANDLER.usedPorts());
        LogUtil.println(usedPorts);
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(usedPorts.size(), tries);
    }

    private static final class CheckPort implements PortType, MaxPortType, PortStepType, RetryType {
        private final int PORT;

        CheckPort(int port) {
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
