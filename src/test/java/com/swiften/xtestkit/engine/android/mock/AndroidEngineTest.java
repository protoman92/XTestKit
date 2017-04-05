package com.swiften.xtestkit.engine.android.mock;

import com.swiften.xtestkit.engine.base.param.NavigateBack;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.android.AndroidEngine;
import com.swiften.xtestkit.engine.mobile.android.protocol.AndroidErrorProtocol;
import com.swiften.xtestkit.util.ProcessRunner;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.apache.bcel.generic.RET;
import org.apache.regexp.RE;
import org.jetbrains.annotations.NotNull;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/22/17.
 */
public final class AndroidEngineTest implements AndroidErrorProtocol {
    @NotNull private final AndroidEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final RetryProtocol RETRY;
    private final int RETRIES_ON_ERROR;

    {
        ENGINE = spy(AndroidEngine.newBuilder()
            .withDeviceName("Nexus_4_API_23")
            .build());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ENGINE.processRunner());

        /* Create a mock here to fake retries() */
        RETRY = mock(RetryProtocol.class);

        RETRIES_ON_ERROR = 3;
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();

        /* Shorten the delay for testing */
        doReturn(100L).when(ENGINE).emulatorBootRetryDelay();

        /* We specifically mock this because starting emulator requires rather
         * complicated behaviors */
        when(RETRY.minRetries()).thenReturn(RETRIES_ON_ERROR);
        when(RETRY.maxRetries()).thenReturn(RETRIES_ON_ERROR);
    }

    @AfterMethod
    public void afterMethod() {
        reset(ENGINE, PROCESS_RUNNER, RETRY);
    }

    //region Start Emulator
    @Test
    @SuppressWarnings("unchecked")
    public void mock_startEmulatorWithBootAnimError_shouldThrow() {
        try {
            // Setup
            String startEmulator = ENGINE.cmStartEmulator();
            String bootAnim = ENGINE.cmBootAnim();

            /* We return a valid output here i.e. when startEmulator is
             * executed, no error will be thrown. If we do not limit the
             * number of retries, the bootanim checking process will loop
             * indefinitely */
            doReturn("Valid output").when(PROCESS_RUNNER).execute(startEmulator);
            doThrow(new IOException()).when(PROCESS_RUNNER).execute(bootAnim);
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStartEmulator(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(RuntimeException.class);
            subscriber.assertNotComplete();
            verify(ENGINE, never()).rxDisableEmulatorAnimations();
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
            ENGINE.rxStartEmulator(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(IOException.class);
            subscriber.assertNotComplete();
            verify(ENGINE, never()).rxDisableEmulatorAnimations();
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
            String startEmulator = ENGINE.cmStartEmulator();
            String bootAnim = ENGINE.cmBootAnim();
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(eq(startEmulator));

            /* Emulate successful bootanim output */
            doReturn("stopped").when(PROCESS_RUNNER).execute(eq(bootAnim));
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxStartEmulator(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(PROCESS_RUNNER).rxExecute(anyString());

            try {
                verify(PROCESS_RUNNER, atLeastOnce()).execute(anyString());
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
            int retries = new RetryProtocol() {}.minRetries();
            String command = ENGINE.cmStopEmulator();
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

    //region Toggle Connection
    @Test
    @SuppressWarnings("unchecked")
    public void mock_toggleConnectionWithNonEmptyOutput_shouldThrow() {
        try {
            // Setup
            /* We expect no output when enabling/disabling connection */
            doReturn("Invalid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxEnableInternetConnection()
                .flatMap(a -> ENGINE.rxDisableInternetConnection())
                .subscribe(subscriber);

            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertErrorMessage(NO_OUTPUT_EXPECTED);
            subscriber.assertNotComplete();
            verify(PROCESS_RUNNER).execute(anyString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_toggleConnection_shouldSucceed() {
        try {
            // Setup
            /* We expect no output when enabling/disabling connection */
            doReturn("").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxEnableInternetConnection()
                .flatMap(a -> ENGINE.rxDisableInternetConnection())
                .subscribe(subscriber);

            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(PROCESS_RUNNER, times(2)).execute(anyString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Check Keyboard State
    @Test
    @SuppressWarnings("unchecked")
    public void mock_checkKeyboardWithNoOutput_shouldEmitFalse() {
        try {
            // Setup
            String command = ENGINE.cmCheckKeyboardOpen();
            doReturn("").when(PROCESS_RUNNER).execute(eq(command));
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxCheckKeyboardOpen().subscribe(subscriber);

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertFalse(TestUtil.getFirstNextEvent(subscriber));
            verify(PROCESS_RUNNER).execute(eq(command));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_checkKeyboardOpen_shouldSucceed() {
        try {
            // Setup
            String command = ENGINE.cmCheckKeyboardOpen();

            /* The output below represents a typical valid response */
            String valid =
                "mHasSurface=true " +
                "mShownFrame=[0.0,48.0][768.0,1280.0] " +
                "isReadyForDisplay()=true";

            doReturn(valid).when(PROCESS_RUNNER).execute(eq(command));
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxCheckKeyboardOpen().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertTrue(TestUtil.getFirstNextEvent(subscriber));
            verify(PROCESS_RUNNER).execute(eq(command));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_dismissHiddenKeyboard_shouldDoNothing() {
        // Setup
        doReturn(Flowable.just(false)).when(ENGINE).rxCheckKeyboardOpen();
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxDismissKeyboard().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).rxCheckKeyboardOpen();
        verify(ENGINE, never()).rxNavigateBack(any(NavigateBack.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_dismissHidden_shouldSucceed() {
        // Setup
        doReturn(Flowable.just(true)).when(ENGINE).rxCheckKeyboardOpen();

        doReturn(Flowable.just(true))
            .when(ENGINE).rxNavigateBack(any(NavigateBack.class));

        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxDismissKeyboard().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).rxCheckKeyboardOpen();
        verify(ENGINE).rxNavigateBack(any(NavigateBack.class));
    }
    //endregion

    //region Disable Emulator Animations
    @Test
    @SuppressWarnings("unchecked")
    public void mock_disableEmulatorAnimationsWithMismatchedValue_shouldThrow() {
        try {
            // Setup
            Arrays.stream(ENGINE.disableAnimationCommands())
                .forEach(a -> {
                    try {
                        doReturn("Valid Output").when(PROCESS_RUNNER).execute(eq(a));
                    } catch (Exception e) {
                        fail(e.getMessage());
                    }
                });

            String randomCommand = TestUtil.randomElement(ENGINE.getAnimationValuesCommands());

            /* The correct output should be 0. We return 1 here to simulate
             * an error */
            doReturn("1").when(PROCESS_RUNNER).execute(eq(randomCommand));
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxDisableEmulatorAnimations().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(Exception.class);
            subscriber.assertNoValues();
            subscriber.assertNotComplete();
            verify(PROCESS_RUNNER, times(2)).execute(anyString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_disableEmulatorAnimationsWithError_shouldThrow() {
        try {
            // Setup
            /* Return all valid outputs for all commands */
            Arrays.stream(ENGINE.disableAnimationCommands())
                .forEach(a -> {
                    try {
                        doThrow(new IOException()).when(PROCESS_RUNNER).execute(eq(a));
                    } catch (Exception e) {
                        fail(e.getMessage());
                    }
                });

            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxDisableEmulatorAnimations().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(IOException.class);
            subscriber.assertNotComplete();
            verify(PROCESS_RUNNER).execute(anyString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_disableEmulatorAnimations_shouldSucceed() {
        try {
            int totalCommands = ENGINE.disableAnimationCommands().length;

            // Setup
            Arrays.stream(ENGINE.disableAnimationCommands())
                .forEach(a -> {
                    try {
                        doReturn("").when(PROCESS_RUNNER).execute(eq(a));
                    } catch (Exception e) {
                        fail(e.getMessage());
                    }
                });

            Arrays.stream(ENGINE.getAnimationValuesCommands())
                .forEach(a -> {
                    try {
                        doReturn("0").when(PROCESS_RUNNER).execute(eq(a));
                    } catch (Exception e) {
                        fail(e.getMessage());
                    }
                });

            TestSubscriber subscriber = TestSubscriber.create();

            // When
            ENGINE.rxDisableEmulatorAnimations().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(PROCESS_RUNNER, times(totalCommands * 2)).execute(anyString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion
}
