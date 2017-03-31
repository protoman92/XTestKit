package com.swiften.xtestkit.engine.ios.mock;

import com.swiften.xtestkit.engine.mobile.ios.IOSEngine;
import com.swiften.xtestkit.util.ProcessRunner;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/31/17.
 */
public class IOSEngineTest {
    @NotNull private final IOSEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;

    {
        ENGINE = spy(IOSEngine.newBuilder()
            .withDeviceUID("CF6E7ACD-F818-4145-A140-75CF1F229A8C")
            .withDeviceName("iPhone 7 Plus")
            .build());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ENGINE.processRunner());
    }

    @Before
    public void before() {
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();

        /* Shorten the delay for testing */
        doReturn(100L).when(ENGINE).simulatorBootRetryDelay();
    }

    @After
    public void after() {
        reset(ENGINE, PROCESS_RUNNER);
    }

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
