package org.swiften.xtestkit.engine.android;

import org.swiften.xtestkit.engine.base.RetriableType;
import org.swiften.xtestkit.engine.mobile.android.ADBHandler;
import org.swiften.xtestkit.engine.mobile.android.DeviceUIDType;
import org.swiften.xtestkit.engine.mobile.android.param.ClearCacheParam;
import org.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import org.swiften.xtestkit.engine.mobile.android.param.StopEmulatorParam;
import org.swiften.xtestkit.engine.mobile.android.ADBErrorType;
import org.swiften.xtestkit.system.NetworkHandler;
import org.swiften.xtestkit.system.ProcessRunner;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
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
public final class MockADBHandlerTest implements ADBErrorType {
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final StartEmulatorParam SE_PARAM;
    @NotNull private final ClearCacheParam CC_PARAM;
    @NotNull private final DeviceUIDType DUID_PARAM;
    @NotNull private final RetriableType RETRY;
    @NotNull private final String APP_PACKAGE;
    @NotNull private final String DEVICE_NAME;
    @NotNull private final String DEVICE_UID;
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
        RETRY = mock(RetriableType.class);
        CC_PARAM = mock(ClearCacheParam.class);
        DUID_PARAM = mock(DeviceUIDType.class);

        /* Return this appPackage when calling CC_PARAM#appPackage */
        APP_PACKAGE = "TestAppPackage";

        /* Return this deviceName when calling SE_PARAM#deviceName() */
        DEVICE_NAME = "Nexus_4_API_23";

        /* Return this deviceUID when calling DUID_PARAM#deviceUID() */
        DEVICE_UID = "emulator-5556";

        /* Return this retry count when calling SE_PARAM#retries and
         * SE_PARAM#maxRetries() */
        RETRIES_ON_ERROR = 3;

        /* There should be 3 commands to disable animations */
        ANIM_DISABLE_CMD_COUNT = 3;
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(PROCESS_RUNNER).when(ADB_HANDLER).processRunner();
        doReturn(PROCESS_RUNNER).when(NETWORK_HANDLER).processRunner();
        doReturn(NETWORK_HANDLER).when(ADB_HANDLER).networkHandler();
        doReturn(true).when(ADB_HANDLER).isAcceptablePort(anyInt());

        /* Shorten the delay for testing */
        doReturn(100L).when(ADB_HANDLER).emulatorBootRetryDelay();

        /* We specifically mock this because starting emulator requires rather
         * complicated behaviors */
        doReturn(RETRIES_ON_ERROR).when(SE_PARAM).retries();
        doReturn(DEVICE_NAME).when(SE_PARAM).deviceName();
        doReturn(RETRIES_ON_ERROR).when(RETRY).retries();
        doReturn(DEVICE_UID).when(DUID_PARAM).deviceUID();
        doReturn(APP_PACKAGE).when(CC_PARAM).appPackage();
        doReturn(DEVICE_UID).when(CC_PARAM).deviceUID();
        doReturn(RETRIES_ON_ERROR).when(CC_PARAM).retries();
    }

    @AfterMethod
    public void afterMethod() {
        reset(ADB_HANDLER, PROCESS_RUNNER, NETWORK_HANDLER, RETRY, SE_PARAM);
    }

    //region Adb Setup
    @Test
    @SuppressWarnings("unchecked")
    public void test_restartAdb_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(any());
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
    public void test_findPortWithNoneAvailable_shouldThrow() {
        // Setup
        doReturn(false).when(ADB_HANDLER).isAcceptablePort(anyInt());
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ADB_HANDLER.rxFindAvailablePort(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(NO_PORT_AVAILABLE);
        subscriber.assertNotComplete();
        verify(ADB_HANDLER).networkHandler();
        verify(ADB_HANDLER).rxFindAvailablePort(any());
        verify(ADB_HANDLER).availablePorts();
        verify(ADB_HANDLER, atLeastOnce()).isAcceptablePort(anyInt());
        verify(ADB_HANDLER, atLeastOnce()).rxIsAcceptablePort(anyInt());
        verify(NETWORK_HANDLER, atLeastOnce()).rxCheckPortAvailable(any());
        verify(NETWORK_HANDLER, never()).markPortAsUsed(anyInt());
        verifyNoMoreInteractions(ADB_HANDLER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_findPortWithAllUsed_shouldThrow() {
        // Setup
        doReturn(true).when(NETWORK_HANDLER).checkPortsMarkedAsUsed(any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxFindAvailablePort(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(NO_PORT_AVAILABLE);
        subscriber.assertNotComplete();
        verify(ADB_HANDLER).rxFindAvailablePort(any());
        verify(ADB_HANDLER).networkHandler();
        verify(ADB_HANDLER).availablePorts();
        verify(NETWORK_HANDLER).checkPortsMarkedAsUsed(any());
        verifyNoMoreInteractions(ADB_HANDLER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_findPort_shouldSucceed() {
        // Setup
        int correctPort = ADBHandler.MAX_PORT - 1;
        doReturn(false).when(ADB_HANDLER).isAcceptablePort(anyInt());
        doReturn(true).when(ADB_HANDLER).isAcceptablePort(correctPort);
        doReturn(true).when(NETWORK_HANDLER).isPortAvailable(anyString(), eq(correctPort));
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ADB_HANDLER.rxFindAvailablePort(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(RxTestUtil.<Integer>getFirstNextEvent(subscriber).intValue(), correctPort);
        verify(ADB_HANDLER).networkHandler();
        verify(ADB_HANDLER).rxFindAvailablePort(any());
        verify(ADB_HANDLER).availablePorts();
        verify(ADB_HANDLER, atLeastOnce()).isAcceptablePort(anyInt());
        verify(ADB_HANDLER, atLeastOnce()).rxIsAcceptablePort(anyInt());
        verify(NETWORK_HANDLER, atLeastOnce()).rxCheckPortAvailable(any());
        verify(NETWORK_HANDLER).markPortAsUsed(correctPort);
        verifyNoMoreInteractions(ADB_HANDLER);
        assertFalse(NETWORK_HANDLER.isPortAvailable(correctPort));
    }
    //endregion

    //region Start Emulator
    @Test
    @SuppressWarnings("unchecked")
    public void test_startEmulatorWithInvalidPort_shouldThrow() {
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
    public void test_startEmulatorWithBootAnimError_shouldThrow() {
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
            verify(ADB_HANDLER).cmAdbShell(any());
            verify(ADB_HANDLER).cmBootAnim(any());
            verify(ADB_HANDLER).cmEmulator();
            verify(ADB_HANDLER).cmStartEmulator(any());
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
    public void test_startEmulatorWithError_shouldThrow() {
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
            verify(ADB_HANDLER).cmAdbShell(any());
            verify(ADB_HANDLER).cmBootAnim(any());
            verify(ADB_HANDLER).cmEmulator();
            verify(ADB_HANDLER).cmStartEmulator(any());
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
    public void test_startEmulator_shouldSucceed() {
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
            verify(ADB_HANDLER).cmAdbShell(any());
            verify(ADB_HANDLER).cmBootAnim(any());
            verify(ADB_HANDLER).cmEmulator();
            verify(ADB_HANDLER).cmStartEmulator(any());
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
    public void test_stopEmulatorsWithError_shouldThrow() {
        try {
            // Setup
            doThrow(new IOException()).when(PROCESS_RUNNER).execute(contains("reboot"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxStopAllEmulators(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoValues();
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER, atLeastOnce()).cmAndroidHome();
            verify(ADB_HANDLER, atLeastOnce()).cmAdb();
            verify(ADB_HANDLER, atLeastOnce()).cmAdbShell();
            verify(ADB_HANDLER, atLeastOnce()).cmStopAllEmulators();
            verify(ADB_HANDLER).rxStopAllEmulators(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_stopEmulators_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxStopAllEmulators(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell();
            verify(ADB_HANDLER).cmStopAllEmulators();
            verify(ADB_HANDLER).rxStopAllEmulators(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_stopEmulator_shouldSucceed() {
        // Setup
        doReturn(Flowable.just(true))
            .when(NETWORK_HANDLER).rxKillProcessWithPort(any(), any());

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxStopEmulator(StopEmulatorParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ADB_HANDLER).rxStopEmulator(any());
        verify(ADB_HANDLER).networkHandler();
        verify(NETWORK_HANDLER).rxKillProcessWithPort(any(), any());
        verifyNoMoreInteractions(ADB_HANDLER);
    }
    //endregion

    //region Check App Installation
    @Test
    @SuppressWarnings("unchecked")
    public void test_checkAppNotInstalled_shouldThrow() {
        try {
            // Setup
            doReturn("").when(PROCESS_RUNNER).execute(contains("list"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxCheckAppInstalled(CC_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertErrorMessage(appNotInstalled(APP_PACKAGE));
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).appNotInstalled(anyString());
            verify(ADB_HANDLER).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell(any());
            verify(ADB_HANDLER).cmListPackages(any());
            verify(ADB_HANDLER).rxCheckAppInstalled(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_checkAppInstalled_shouldSucceed() {
        try {
            // Setup
            doReturn(APP_PACKAGE).when(PROCESS_RUNNER).execute(contains("list"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxCheckAppInstalled(CC_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).appNotInstalled(anyString());
            verify(ADB_HANDLER).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell(any());
            verify(ADB_HANDLER).cmListPackages(any());
            verify(ADB_HANDLER).rxCheckAppInstalled(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Clear Cache
    @Test
    @SuppressWarnings("unchecked")
    public void test_clearCacheWithError_shouldThrow() {
        try {
            // Setup
            doThrow(new RuntimeException())
                .when(PROCESS_RUNNER).execute(contains("pm clear"));

            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxClearCachedData(CC_PARAM).subscribe(subscriber);

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(RuntimeException.class);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell(any());
            verify(ADB_HANDLER).cmClearCachedData(any());
            verify(ADB_HANDLER).unableToClearCache(anyString());
            verify(ADB_HANDLER).rxClearCachedData(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_clearCache_shouldSucceed() {
        try {
            // Setup
            doReturn("Success").when(PROCESS_RUNNER).execute(contains("pm clear"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxClearCachedData(CC_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell(any());
            verify(ADB_HANDLER).cmClearCachedData(any());
            verify(ADB_HANDLER).unableToClearCache(anyString());
            verify(ADB_HANDLER).rxClearCachedData(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Toggle Connection
    @Test
    @SuppressWarnings("unchecked")
    public void test_toggleConnectionWithNonEmptyOutput_shouldThrow() {
        try {
            // Setup
            /* We expect no output when enabling/disabling connection */
            doReturn("Invalid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxEnableInternetConnection(DUID_PARAM)
                .flatMap(a -> ADB_HANDLER.rxDisableInternetConnection(DUID_PARAM))
                .subscribe(subscriber);

            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertErrorMessage(NO_OUTPUT_EXPECTED);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell(any());
            verify(ADB_HANDLER).cmToggleConnection(any());
            verify(ADB_HANDLER).rxToggleInternetConnection(any());
            verify(ADB_HANDLER).rxEnableInternetConnection(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_toggleConnection_shouldSucceed() {
        try {
            // Setup
            /* We expect no output when enabling/disabling connection */
            doReturn("").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxEnableInternetConnection(DUID_PARAM)
                .flatMap(a -> ADB_HANDLER.rxDisableInternetConnection(DUID_PARAM))
                .subscribe(subscriber);

            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER, times(2)).processRunner();
            verify(ADB_HANDLER, times(2)).cmAndroidHome();
            verify(ADB_HANDLER, times(2)).cmAdb();
            verify(ADB_HANDLER, times(2)).cmAdbShell(any());
            verify(ADB_HANDLER, times(2)).cmToggleConnection(any());
            verify(ADB_HANDLER, times(2)).rxToggleInternetConnection(any());
            verify(ADB_HANDLER).rxEnableInternetConnection(any());
            verify(ADB_HANDLER).rxDisableInternetConnection(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Check Keyboard State
    @Test
    @SuppressWarnings("unchecked")
    public void test_checkKeyboardWithNoOutput_shouldEmitFalse() {
        try {
            // Setup
            doReturn("").when(PROCESS_RUNNER).execute(contains("InputMethod"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxCheckKeyboardOpen(DUID_PARAM).subscribe(subscriber);

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertFalse(RxTestUtil.getFirstNextEvent(subscriber));
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell(any());
            verify(ADB_HANDLER).cmCheckKeyboardOpen(any());
            verify(ADB_HANDLER).rxCheckKeyboardOpen(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_checkKeyboardOpen_shouldSucceed() {
        try {
            // Setup
            /* The output below represents a typical valid response */
            String valid =
                "mHasSurface=true " +
                    "mShownFrame=[0.0,48.0][768.0,1280.0] " +
                    "isReadyForDisplay()=true";

            doReturn(valid).when(PROCESS_RUNNER).execute(contains("InputMethod"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxCheckKeyboardOpen(DUID_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cmAndroidHome();
            verify(ADB_HANDLER).cmAdb();
            verify(ADB_HANDLER).cmAdbShell(any());
            verify(ADB_HANDLER).cmCheckKeyboardOpen(any());
            verify(ADB_HANDLER).rxCheckKeyboardOpen(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Disable Emulator Animations
    @Test
    @SuppressWarnings("unchecked")
    public void test_disableEmulatorAnimationsWithMismatchedValue_shouldThrow() {
        try {
            // Setup
            doReturn("").when(PROCESS_RUNNER).execute(contains("put"));
            doReturn("1").when(PROCESS_RUNNER).execute(contains("get"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxDisableEmulatorAnimations(DUID_PARAM).subscribe(subscriber);
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
    public void test_disableEmulatorAnimationsWithError_shouldSucceed() {
        try {
            // Setup
            doThrow(new RuntimeException()).when(PROCESS_RUNNER).execute(contains("put"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxDisableEmulatorAnimations(DUID_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(Exception.class);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmAndroidHome();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmAdb();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmAdbShell(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).changeSettingsFailed(anyString());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmPutSettings(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).processRunner();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).emulatorSettingTimeout();
            verify(ADB_HANDLER).rxDisableWindowAnimationScale(any());
            verify(ADB_HANDLER).rxDisableTransitionAnimationScale(any());
            verify(ADB_HANDLER).rxDisableAnimatorDurationScale(any());
            verify(ADB_HANDLER).rxDisableEmulatorAnimations(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).rxChangeSettings(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_disableEmulatorAnimations_shouldSucceed() {
        try {
            // Setup
            doReturn("").when(PROCESS_RUNNER).execute(contains("put"));
            doReturn("0").when(PROCESS_RUNNER).execute(contains("get"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxDisableEmulatorAnimations(DUID_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT * 2)).cmAndroidHome();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT * 2)).cmAdb();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT * 2)).cmAdbShell(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).changeSettingsFailed(anyString());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmPutSettings(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cmGetSettings(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).processRunner();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).emulatorSettingTimeout();
            verify(ADB_HANDLER).rxDisableWindowAnimationScale(any());
            verify(ADB_HANDLER).rxDisableTransitionAnimationScale(any());
            verify(ADB_HANDLER).rxDisableAnimatorDurationScale(any());
            verify(ADB_HANDLER).rxDisableEmulatorAnimations(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).rxChangeSettings(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion
}
