package com.swiften.xtestkit.engine.android.mock;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.android.ADBHandler;
import com.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import com.swiften.xtestkit.engine.mobile.android.protocol.ADBErrorProtocol;
import com.swiften.xtestkit.system.NetworkHandler;
import com.swiften.xtestkit.system.ProcessRunner;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by haipham on 4/8/17.
 */
public class ADBHandlerTest implements ADBErrorProtocol {
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final StartEmulatorParam SE_PARAM;
    @NotNull private final RetryProtocol RETRY;
    @NotNull private final String DEVICE_NAME;
    private final int RETRIES_ON_ERROR;
    private final int ANIM_DISABLE_CMD_COUNT;

    {
        ADB_HANDLER = spy(ADBHandler.builder().build());

        /* We return this networkHandler when calling
         * ADB_HANDLER.networkHandler() */
        NETWORK_HANDLER = spy(NetworkHandler.builder().build());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ProcessRunner.builder().build());

        /* Create a mock here to fake retries() */
        SE_PARAM = mock(StartEmulatorParam.class);
        RETRY = mock(RetryProtocol.class);

        /* Return this deviceName when calling SE_PARAM#deviceName() */
        DEVICE_NAME = "Nexus_4_API_23";

        /* Return this retry count when calling SE_PARAM#minRetries and
         * SE_PARAM#maxRetries() */
        RETRIES_ON_ERROR = 3;

        /* There should be 3 commands to disable animations */
        ANIM_DISABLE_CMD_COUNT = 3;
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(PROCESS_RUNNER).when(ADB_HANDLER).processRunner();
        doReturn(NETWORK_HANDLER).when(ADB_HANDLER).networkHandler();
        doReturn(true).when(ADB_HANDLER).isAcceptablePort(anyInt());

        /* Shorten the delay for testing */
        doReturn(100L).when(ADB_HANDLER).emulatorBootRetryDelay();

        /* We specifically mock this because starting emulator requires rather
         * complicated behaviors */
        when(SE_PARAM.minRetries()).thenReturn(RETRIES_ON_ERROR);
        when(SE_PARAM.maxRetries()).thenReturn(RETRIES_ON_ERROR);
        when(SE_PARAM.deviceName()).thenReturn(DEVICE_NAME);
        when(RETRY.minRetries()).thenReturn(RETRIES_ON_ERROR);
        when(RETRY.maxRetries()).thenReturn(RETRIES_ON_ERROR);
    }

    @AfterMethod
    public void afterMethod() {
        reset(ADB_HANDLER, PROCESS_RUNNER, NETWORK_HANDLER, RETRY, SE_PARAM);
    }

    //region Adb Setup
    @Test
    @SuppressWarnings("unchecked")
    public void mock_restartAdb_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxRestartAdb().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmLaunchAdb();
            verify(ADB_HANDLER).rxRestartAdb();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).networkHandler();
            verify(NETWORK_HANDLER).rxKillProcessWithName(anyString());
            verify(NETWORK_HANDLER).rxKillProcessWithPid(anyString());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Check port
    @Test
    @SuppressWarnings("unchecked")
    public void mock_findPortWithNoneAvailable_shouldThrow() {
        // Setup
        doReturn(false).when(ADB_HANDLER).isAcceptablePort(anyInt());
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ADB_HANDLER.rxFindAvailablePort().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(NO_PORT_AVAILABLE);
        subscriber.assertNotComplete();
        verify(ADB_HANDLER).networkHandler();
        verify(ADB_HANDLER).rxFindAvailablePort();
        verify(ADB_HANDLER, atLeastOnce()).isAcceptablePort(anyInt());
        verify(ADB_HANDLER, atLeastOnce()).rxIsAcceptablePort(anyInt());
        verify(NETWORK_HANDLER, atLeastOnce()).rxCheckPortAvailable(anyInt());
        verify(NETWORK_HANDLER, never()).markPortAsUsed(anyInt());
        verifyNoMoreInteractions(ADB_HANDLER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_findPort_shouldSucceed() {
        // Setup
        int correctPort = ADBHandler.MAX_PORT - 1;
        doReturn(false).when(ADB_HANDLER).isAcceptablePort(anyInt());
        doReturn(true).when(ADB_HANDLER).isAcceptablePort(correctPort);
        doReturn(Flowable.just(true)).when(NETWORK_HANDLER).rxCheckPortAvailable(correctPort);
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ADB_HANDLER.rxFindAvailablePort().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(TestUtil.<Integer>getFirstNextEvent(subscriber).intValue(), correctPort);
        verify(ADB_HANDLER).networkHandler();
        verify(ADB_HANDLER).rxFindAvailablePort();
        verify(ADB_HANDLER, atLeastOnce()).isAcceptablePort(anyInt());
        verify(ADB_HANDLER, atLeastOnce()).rxIsAcceptablePort(anyInt());
        verify(NETWORK_HANDLER, atLeastOnce()).rxCheckPortAvailable(anyInt());
        verify(NETWORK_HANDLER).markPortAsUsed(correctPort);
        verifyNoMoreInteractions(ADB_HANDLER);
        assertFalse(NETWORK_HANDLER.isPortAvailable(correctPort));
    }
    //endregion

    //region Start Emulator
    @Test
    @SuppressWarnings("unchecked")
    public void mock_startEmulatorWithInvalidPort_shouldThrow() {
        // Setup
        doReturn(false).when(ADB_HANDLER).isAcceptablePort(anyInt());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxStartEmulator(SE_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertError(Exception.class);
        subscriber.assertNotComplete();
        verify(ADB_HANDLER).rxStartEmulator(any());
        verify(ADB_HANDLER).unacceptablePort(anyInt());
        verify(ADB_HANDLER).isAcceptablePort(anyInt());
        verifyNoMoreInteractions(ADB_HANDLER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_startEmulatorWithBootAnimError_shouldThrow() {
        try {
            // Setup
            /* We return a valid output here i.e. when startEmulator is
             * executed, no error will be thrown. If we do not limit the
             * number of retries, the bootanim checking process will loop
             * indefinitely */
            doReturn("Valid output").when(PROCESS_RUNNER).execute(contains("-avd"));
            doThrow(new IOException()).when(PROCESS_RUNNER).execute(contains("bootanim"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxStartEmulator(SE_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(RuntimeException.class);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER, atLeastOnce()).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell();
            verify(ADB_HANDLER).cmBootAnim();
            verify(ADB_HANDLER).cmEmulator();
            verify(ADB_HANDLER).cmStartEmulator(anyString());
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).isAcceptablePort(anyInt());
            verify(ADB_HANDLER).emulatorBootRetryDelay();
            verify(ADB_HANDLER).emulatorBootTimeout();
            verify(ADB_HANDLER).rxStartEmulator(any());
            verifyNoMoreInteractions(ADB_HANDLER);
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
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxStartEmulator(SE_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(IOException.class);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER, atLeastOnce()).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell();
            verify(ADB_HANDLER).cmBootAnim();
            verify(ADB_HANDLER).cmEmulator();
            verify(ADB_HANDLER).cmStartEmulator(anyString());
            verify(ADB_HANDLER).isAcceptablePort(anyInt());
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).emulatorBootRetryDelay();
            verify(ADB_HANDLER).emulatorBootTimeout();
            verify(ADB_HANDLER).rxStartEmulator(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_startEmulator_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(contains("avd"));
            doReturn("stopped").when(PROCESS_RUNNER).execute(contains("bootanim"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxStartEmulator(SE_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER, atLeastOnce()).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell();
            verify(ADB_HANDLER).cmBootAnim();
            verify(ADB_HANDLER).cmEmulator();
            verify(ADB_HANDLER).cmStartEmulator(anyString());
            verify(ADB_HANDLER).isAcceptablePort(anyInt());
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).emulatorBootRetryDelay();
            verify(ADB_HANDLER).emulatorBootTimeout();
            verify(ADB_HANDLER).rxStartEmulator(any());
            verifyNoMoreInteractions(ADB_HANDLER);
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
            String command = ADB_HANDLER.cmStopEmulator();
            doThrow(new IOException()).when(PROCESS_RUNNER).execute(eq(command));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxStopEmulator(SE_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoValues();
            subscriber.assertNotComplete();
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
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxStopEmulator(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
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
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxEnableInternetConnection()
                .flatMap(a -> ADB_HANDLER.rxDisableInternetConnection())
                .subscribe(subscriber);

            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertErrorMessage(NO_OUTPUT_EXPECTED);
            subscriber.assertNotComplete();
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
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxEnableInternetConnection()
                .flatMap(a -> ADB_HANDLER.rxDisableInternetConnection())
                .subscribe(subscriber);

            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
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
            String command = ADB_HANDLER.cmCheckKeyboardOpen();
            doReturn("").when(PROCESS_RUNNER).execute(eq(command));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxCheckKeyboardOpen().subscribe(subscriber);

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertFalse(TestUtil.getFirstNextEvent(subscriber));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_checkKeyboardOpen_shouldSucceed() {
        try {
            // Setup
            String command = ADB_HANDLER.cmCheckKeyboardOpen();

            /* The output below represents a typical valid response */
            String valid =
                "mHasSurface=true " +
                    "mShownFrame=[0.0,48.0][768.0,1280.0] " +
                    "isReadyForDisplay()=true";

            doReturn(valid).when(PROCESS_RUNNER).execute(eq(command));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxCheckKeyboardOpen().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertTrue(TestUtil.getFirstNextEvent(subscriber));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Disable Emulator Animations
    @Test
    @SuppressWarnings("unchecked")
    public void mock_disableEmulatorAnimationsWithMismatchedValue_shouldThrow() {
        try {
            // Setup
            doReturn("").when(PROCESS_RUNNER).execute(contains("put"));
            doReturn("1").when(PROCESS_RUNNER).execute(contains("get"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxDisableEmulatorAnimations().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(Exception.class);
            subscriber.assertNoValues();
            subscriber.assertNotComplete();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_disableEmulatorAnimationsWithError_shouldThrow() {
        try {
            // Setup
            doThrow(new RuntimeException()).when(PROCESS_RUNNER).execute(contains("put"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxDisableEmulatorAnimations().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(Exception.class);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).disableWindowAnimationScaleParam();
            verify(ADB_HANDLER).disableTransitionAnimationScaleParam();
            verify(ADB_HANDLER).disableAnimatorDurationScaleParam();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmAndroidHome();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmAdb();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmAdbShell();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).changeSettingsFailed(anyString());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmPutSettings(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).processRunner();
            verify(ADB_HANDLER).rxDisableWindowAnimationScale();
            verify(ADB_HANDLER).rxDisableTransitionAnimationScale();
            verify(ADB_HANDLER).rxDisableAnimatorDurationScale();
            verify(ADB_HANDLER).rxDisableEmulatorAnimations();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).rxChangeSettings(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_disableEmulatorAnimations_shouldSucceed() {
        try {
            // Setup
            doReturn("").when(PROCESS_RUNNER).execute(contains("put"));
            doReturn("0").when(PROCESS_RUNNER).execute(contains("get"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxDisableEmulatorAnimations().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).disableWindowAnimationScaleParam();
            verify(ADB_HANDLER).disableTransitionAnimationScaleParam();
            verify(ADB_HANDLER).disableAnimatorDurationScaleParam();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT * 2)).cmAndroidHome();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT * 2)).cmAdb();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT * 2)).cmAdbShell();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).changeSettingsFailed(anyString());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmPutSettings(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmGetSettings(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).processRunner();
            verify(ADB_HANDLER).rxDisableWindowAnimationScale();
            verify(ADB_HANDLER).rxDisableTransitionAnimationScale();
            verify(ADB_HANDLER).rxDisableAnimatorDurationScale();
            verify(ADB_HANDLER).rxDisableEmulatorAnimations();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).rxChangeSettings(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion
}
