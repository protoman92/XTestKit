package com.swiften.xtestkit.android.mock;

import com.swiften.engine.base.param.StartEnvParam;
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

    {
        ENGINE = spy(AndroidEngine.newBuilder()
            .withDeviceName("Nexus_4_API_23")
            .build());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ENGINE.processRunner());
    }

    @Before
    public void setUp() {
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();

        /* Shorten the delay for testing */
        doReturn(100L).when(ENGINE).emulatorBootRetryDelay();
    }

    @After
    public void tearDown() {
        reset(ENGINE, PROCESS_RUNNER);
    }

    //region Start Emulator.
    @Test
    @SuppressWarnings("unchecked")
    public void mock_startEmulatorWithBootAnimError_shouldThrow() {
        try {
            // Setup
            int retries = 3;
            String startEmulator = ENGINE.startEmulator();
            String bootAnim = ENGINE.bootAnim();

            /* We return a valid output here i.e. when startEmulator is
             * executed, no error will be thrown. If we do not limit the
             * number of retries, the bootanim checking process will loop
             * indefinitely */
            doReturn("Valid output").when(PROCESS_RUNNER).execute(startEmulator);
            doThrow(new IOException()).when(PROCESS_RUNNER).execute(bootAnim);

            StartEnvParam param = StartEnvParam.newBuilder()
                .withRetriesOnError(retries)
                .build();

            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStartEmulator(param).take(3).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(RuntimeException.class);
            subscriber.assertNotComplete();
            verify(PROCESS_RUNNER).rxExecute(anyString());

            try {
                verify(PROCESS_RUNNER, times(retries + 2)).execute(anyString());
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
            StartEnvParam param = StartEnvParam.newBuilder().build();
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStartEmulator(param).subscribe(subscriber);
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
    public void mock_startEmulator_shouldThrow() {
        try {
            // Setup
            String startEmulator = ENGINE.startEmulator();
            String bootAnim = ENGINE.bootAnim();
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(eq(startEmulator));

            /* Emulate successful bootanim output */
            doReturn("stopped").when(PROCESS_RUNNER).execute(eq(bootAnim));
            StartEnvParam param = StartEnvParam.newBuilder().build();
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStartEmulator(param).subscribe(subscriber);
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
}
