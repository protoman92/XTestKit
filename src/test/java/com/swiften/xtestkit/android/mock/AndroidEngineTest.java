package com.swiften.xtestkit.android.mock;

import com.swiften.engine.base.param.StartEnvParam;
import com.swiften.engine.base.param.protocol.RetryProtocol;
import com.swiften.engine.mobile.android.AndroidEngine;
import com.swiften.util.Log;
import com.swiften.util.ProcessRunner;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/22/17.
 */
public final class AndroidEngineTest {
    @NotNull private final AndroidEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final StartEnvParam START_PARAM;
    private final int RETRIES_ON_ERROR;

    {
        ENGINE = spy(AndroidEngine.newBuilder()
            .withDeviceName("Nexus_4_API_23")
            .build());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ENGINE.processRunner());

        /* Create a mock here to fake retriesOnError() */
        START_PARAM = mock(StartEnvParam.class);

        RETRIES_ON_ERROR = 3;
    }

    @Before
    public void setUp() {
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();

        /* Shorten the delay for testing */
        doReturn(100L).when(ENGINE).emulatorBootRetryDelay();

        /* We specifically mock StartEnvParams because starting emulator
         * requires rather complicated behaviors */
        when(START_PARAM.retriesOnError()).thenReturn(RETRIES_ON_ERROR);
    }

    @After
    public void tearDown() {
        reset(ENGINE, PROCESS_RUNNER, START_PARAM);
    }

    //region Start Emulator
    @Test
    @SuppressWarnings("unchecked")
    public void mock_startEmulatorWithBootAnimError_shouldThrow() {
        try {
            // Setup
            String startEmulator = ENGINE.startEmulator();
            String bootAnim = ENGINE.bootAnim();

            /* We return a valid output here i.e. when startEmulator is
             * executed, no error will be thrown. If we do not limit the
             * number of retries, the bootanim checking process will loop
             * indefinitely */
            doReturn("Valid output").when(PROCESS_RUNNER).execute(startEmulator);
            doThrow(new IOException()).when(PROCESS_RUNNER).execute(bootAnim);
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStartEmulator(START_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(RuntimeException.class);
            subscriber.assertNotComplete();
            verify(PROCESS_RUNNER).rxExecute(anyString());

            try {
                verify(PROCESS_RUNNER, times(RETRIES_ON_ERROR + 2)).execute(anyString());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_startEmulatorWithError_shouldThrow() {
        try {
            // Setup
            /* We need to make sure that when startEmulator() throws an
             * error, bootAnim loop will be notified. This is because the
             * former is run on a different Thread */
            doThrow(new IOException()).when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStartEmulator(START_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(IOException.class);
            subscriber.assertNotComplete();
            verify(PROCESS_RUNNER).rxExecute(anyString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_startEmulator_shouldSucceed() {
        try {
            // Setup
            String startEmulator = ENGINE.startEmulator();
            String bootAnim = ENGINE.bootAnim();
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(eq(startEmulator));

            /* Emulate successful bootanim output */
            doReturn("stopped").when(PROCESS_RUNNER).execute(eq(bootAnim));
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStartEmulator(START_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(PROCESS_RUNNER).rxExecute(anyString());

            try {
                verify(PROCESS_RUNNER, times(2)).execute(anyString());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Stop Emulator
    @Test
    @SuppressWarnings("unchecked")
    public void mock_stopEmulatorWithError_shouldThrow() {
        try {
            // Setup
            int retries = new RetryProtocol() {}.retriesOnError();
            String command = ENGINE.stopEmulator();
            doThrow(new IOException()).when(PROCESS_RUNNER).execute(eq(command));
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStopEmulator().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoValues();
            subscriber.assertNotComplete();

            /* Since we are using retry(), there will be a total of retry
             * count + 1 (including the original count) */
            verify(PROCESS_RUNNER, times(retries + 1)).execute(eq(command));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_stopEmulator_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStopEmulator().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(PROCESS_RUNNER).execute(anyString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion
}
