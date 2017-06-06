package org.swiften.xtestkit.android;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.android.adb.ADBErrorType;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.param.ClearCacheParam;
import org.swiften.xtestkit.android.param.StartEmulatorParam;
import org.swiften.xtestkit.android.param.StopEmulatorParam;
import org.swiften.xtestkit.android.type.DeviceUIDType;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.system.network.NetworkHandler;
import org.swiften.xtestkit.system.process.ProcessRunner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by haipham on 4/8/17.
 */
@SuppressWarnings("MessageMissingOnTestNGAssertion")
public final class MockADBHandlerTest implements ADBErrorType {
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final StartEmulatorParam SE_PARAM;
    @NotNull private final ClearCacheParam CC_PARAM;
    @NotNull private final DeviceUIDType DUID_PARAM;
    @NotNull private final RetryType RETRY;
    @NotNull private final String APP_PACKAGE;
    @NotNull private final String DEVICE_NAME;
    @NotNull private final String DEVICE_UID;
    private final int RETRIES_ON_ERROR;
    private final int ANIM_DISABLE_CMD_COUNT;

    {
        ADB_HANDLER = spy(new ADBHandler());

        /* We return this networkHandler when calling
         * ADB_HANDLER.networkHandler() */
        NETWORK_HANDLER = spy(new NetworkHandler());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(new ProcessRunner());

        /* Create a mock here to fake retries() */
        SE_PARAM = mock(StartEmulatorParam.class);
        RETRY = mock(RetryType.class);
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
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_restartAdb().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_launchAdb();
            verify(ADB_HANDLER).rxa_restartAdb();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).networkHandler();
            verify(NETWORK_HANDLER).rxa_killWithName(any());
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
        doReturn(false).when(NETWORK_HANDLER).isPortAvailable(any(), anyInt());
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ADB_HANDLER.rxe_availablePort(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(NO_PORT_AVAILABLE);
        subscriber.assertNotComplete();
        verify(ADB_HANDLER).networkHandler();
        verify(ADB_HANDLER).rxe_availablePort(any());
        verify(ADB_HANDLER).availablePorts();
        verify(NETWORK_HANDLER).rxa_checkUntilPortAvailable(any());
        verify(NETWORK_HANDLER, atLeastOnce()).rxa_checkPortAvailable(any());
        verify(NETWORK_HANDLER, never()).markPortUsed(anyInt());
        verifyNoMoreInteractions(ADB_HANDLER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_findPortWithAllUsed_shouldThrow() {
        // Setup
        doReturn(true).when(NETWORK_HANDLER).checkPortsUsed(any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxe_availablePort(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(NO_PORT_AVAILABLE);
        subscriber.assertNotComplete();
        verify(ADB_HANDLER).rxe_availablePort(any());
        verify(ADB_HANDLER).networkHandler();
        verify(ADB_HANDLER).availablePorts();
        verify(NETWORK_HANDLER).checkPortsUsed(any());
        verifyNoMoreInteractions(ADB_HANDLER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_findPort_shouldSucceed() {
        // Setup
        int correctPort = ADBHandler.MAX_PORT - 1;
        doReturn(false).when(NETWORK_HANDLER).isPortAvailable(any(), anyInt());
        doReturn(true).when(NETWORK_HANDLER).isPortAvailable(any(), eq(correctPort));
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ADB_HANDLER.rxe_availablePort(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(RxTestUtil.<Integer>firstNextEvent(subscriber).intValue(), correctPort);
        verify(ADB_HANDLER).networkHandler();
        verify(ADB_HANDLER).rxe_availablePort(any());
        verify(ADB_HANDLER).availablePorts();
        verify(NETWORK_HANDLER).rxa_checkUntilPortAvailable(any());
        verify(NETWORK_HANDLER, atLeastOnce()).rxa_checkPortAvailable(any());
        verify(NETWORK_HANDLER).markPortUsed(correctPort);
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
        ADB_HANDLER.rxa_startEmulator(SE_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertError(Exception.class);
        subscriber.assertNotComplete();
        verify(ADB_HANDLER).emulatorBootRetryDelay();
        verify(ADB_HANDLER).emulatorBootTimeout();
        verify(ADB_HANDLER).processRunner();
        verify(ADB_HANDLER, atLeastOnce()).cm_AndroidHome();
        verify(ADB_HANDLER).cm_adb();
        verify(ADB_HANDLER).cm_adbShell(any());
        verify(ADB_HANDLER).cm_bootAnim(any());
        verify(ADB_HANDLER).cm_emulator();
        verify(ADB_HANDLER).cm_startEmulator(any());
        verify(ADB_HANDLER).rxa_startEmulator(any());
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
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(contains("-avd"));
            doReturn(RxUtil.error()).when(PROCESS_RUNNER).rxa_execute(contains("bootanim"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_startEmulator(SE_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(RuntimeException.class);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER, atLeastOnce()).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell(any());
            verify(ADB_HANDLER).cm_bootAnim(any());
            verify(ADB_HANDLER).cm_emulator();
            verify(ADB_HANDLER).cm_startEmulator(any());
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).emulatorBootRetryDelay();
            verify(ADB_HANDLER).emulatorBootTimeout();
            verify(ADB_HANDLER).rxa_startEmulator(any());
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
            doReturn(RxUtil.error()).when(PROCESS_RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_startEmulator(SE_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(IOException.class);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER, atLeastOnce()).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell(any());
            verify(ADB_HANDLER).cm_bootAnim(any());
            verify(ADB_HANDLER).cm_emulator();
            verify(ADB_HANDLER).cm_startEmulator(any());
            verify(ADB_HANDLER).isAcceptablePort(anyInt());
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).emulatorBootRetryDelay();
            verify(ADB_HANDLER).emulatorBootTimeout();
            verify(ADB_HANDLER).rxa_startEmulator(any());
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
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(contains("avd"));
            doReturn(Flowable.just("stopped")).when(PROCESS_RUNNER).rxa_execute(contains("bootanim"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_startEmulator(SE_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER, atLeastOnce()).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell(any());
            verify(ADB_HANDLER).cm_bootAnim(any());
            verify(ADB_HANDLER).cm_emulator();
            verify(ADB_HANDLER).cm_startEmulator(any());
            verify(ADB_HANDLER).isAcceptablePort(anyInt());
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).emulatorBootRetryDelay();
            verify(ADB_HANDLER).emulatorBootTimeout();
            verify(ADB_HANDLER).rxa_startEmulator(any());
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
            doReturn(RxUtil.error()).when(PROCESS_RUNNER).rxa_execute(contains("reboot"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_stopAllEmulators(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoValues();
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER, atLeastOnce()).cm_AndroidHome();
            verify(ADB_HANDLER, atLeastOnce()).cm_adb();
            verify(ADB_HANDLER, atLeastOnce()).cm_adbShell();
            verify(ADB_HANDLER, atLeastOnce()).cm_stopAllEmulators();
            verify(ADB_HANDLER).rxa_stopAllEmulators(any());
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
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_stopAllEmulators(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell();
            verify(ADB_HANDLER).cm_stopAllEmulators();
            verify(ADB_HANDLER).rxa_stopAllEmulators(any());
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
            .when(NETWORK_HANDLER).rxa_killWithPort(any(), any());

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxa_stopEmulator(StopEmulatorParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ADB_HANDLER).rxa_stopEmulator(any());
        verify(ADB_HANDLER).networkHandler();
        verify(NETWORK_HANDLER).rxa_killWithPort(any(), any());
        verifyNoMoreInteractions(ADB_HANDLER);
    }
    //endregion

    //region Check App Installation
    @Test
    @SuppressWarnings("unchecked")
    public void test_checkAppNotInstalled_shouldThrow() {
        try {
            // Setup
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(contains("list"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxe_appInstalled(CC_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertErrorMessage(appNotInstalled(APP_PACKAGE));
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).appNotInstalled(any());
            verify(ADB_HANDLER).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell(any());
            verify(ADB_HANDLER).cm_listPackages(any());
            verify(ADB_HANDLER).rxe_appInstalled(any());
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
            doReturn(Flowable.just(APP_PACKAGE)).when(PROCESS_RUNNER).rxa_execute(contains("list"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxe_appInstalled(CC_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).appNotInstalled(any());
            verify(ADB_HANDLER).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell(any());
            verify(ADB_HANDLER).cm_listPackages(any());
            verify(ADB_HANDLER).rxe_appInstalled(any());
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
            doReturn(RxUtil.error()).when(PROCESS_RUNNER).rxa_execute(contains("pm clear"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_clearCache(CC_PARAM).subscribe(subscriber);

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(RuntimeException.class);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell(any());
            verify(ADB_HANDLER).cm_clearCache(any());
            verify(ADB_HANDLER).unableToClearCache(any());
            verify(ADB_HANDLER).rxa_clearCache(any());
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
            doReturn(Flowable.just("Success")).when(PROCESS_RUNNER).rxa_execute(contains("pm clear"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_clearCache(CC_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell(any());
            verify(ADB_HANDLER).cm_clearCache(any());
            verify(ADB_HANDLER).unableToClearCache(any());
            verify(ADB_HANDLER).rxa_clearCache(any());
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
            doReturn(Flowable.just("Invalid Output")).when(PROCESS_RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_enableInternet(DUID_PARAM)
                .flatMap(a -> ADB_HANDLER.rxa_disableInternet(DUID_PARAM))
                .subscribe(subscriber);

            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertErrorMessage(NO_OUTPUT_EXPECTED);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell(any());
            verify(ADB_HANDLER).cm_toggleInternet(any());
            verify(ADB_HANDLER).rxa_toggleInternet(any());
            verify(ADB_HANDLER).rxa_enableInternet(any());
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
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_enableInternet(DUID_PARAM)
                .flatMap(a -> ADB_HANDLER.rxa_disableInternet(DUID_PARAM))
                .subscribe(subscriber);

            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER, times(2)).processRunner();
            verify(ADB_HANDLER, times(2)).cm_AndroidHome();
            verify(ADB_HANDLER, times(2)).cm_adb();
            verify(ADB_HANDLER, times(2)).cm_adbShell(any());
            verify(ADB_HANDLER, times(2)).cm_toggleInternet(any());
            verify(ADB_HANDLER, times(2)).rxa_toggleInternet(any());
            verify(ADB_HANDLER).rxa_enableInternet(any());
            verify(ADB_HANDLER).rxa_disableInternet(any());
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
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(contains("InputMethod"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxe_keyboardOpen(DUID_PARAM).subscribe(subscriber);

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertFalse(RxTestUtil.firstNextEvent(subscriber));
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell(any());
            verify(ADB_HANDLER).cm_checkKeyboardOpen(any());
            verify(ADB_HANDLER).rxe_keyboardOpen(any());
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
            String valid = "mHasSurface=true " +
                "mShownFrame=[0.0,48.0][768.0,1280.0] " +
                "isReadyForDisplay()=true";

            doReturn(Flowable.just(valid)).when(PROCESS_RUNNER).rxa_execute(contains("InputMethod"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxe_keyboardOpen(DUID_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertTrue(RxTestUtil.firstNextEvent(subscriber));
            verify(ADB_HANDLER).processRunner();
            verify(ADB_HANDLER).cm_AndroidHome();
            verify(ADB_HANDLER).cm_adb();
            verify(ADB_HANDLER).cm_adbShell(any());
            verify(ADB_HANDLER).cm_checkKeyboardOpen(any());
            verify(ADB_HANDLER).rxe_keyboardOpen(any());
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
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(contains("put"));
            doReturn(Flowable.just("1")).when(PROCESS_RUNNER).rxa_execute(contains("get"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_disableEmulatorAnimations(DUID_PARAM).subscribe(subscriber);
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
            doReturn(RxUtil.error()).when(PROCESS_RUNNER).rxa_execute(contains("put"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_disableEmulatorAnimations(DUID_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(Exception.class);
            subscriber.assertNotComplete();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cm_AndroidHome();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cm_adb();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cm_adbShell(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).changeSettingsFailed(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cm_putSettings(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).processRunner();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).emulatorSettingTimeout();
            verify(ADB_HANDLER).rxa_disableWindowAnimationScale(any());
            verify(ADB_HANDLER).rxa_disableTransitionAnimationScale(any());
            verify(ADB_HANDLER).rxa_disableAnimatorDurationScale(any());
            verify(ADB_HANDLER).rxa_disableEmulatorAnimations(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).rxa_changeSettings(any());
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
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(contains("put"));
            doReturn(Flowable.just("0")).when(PROCESS_RUNNER).rxa_execute(contains("get"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ADB_HANDLER.rxa_disableEmulatorAnimations(DUID_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT * 2)).cm_AndroidHome();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT * 2)).cm_adb();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT * 2)).cm_adbShell(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).changeSettingsFailed(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cm_putSettings(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).cm_getSettings(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).processRunner();
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).emulatorSettingTimeout();
            verify(ADB_HANDLER).rxa_disableWindowAnimationScale(any());
            verify(ADB_HANDLER).rxa_disableTransitionAnimationScale(any());
            verify(ADB_HANDLER).rxa_disableAnimatorDurationScale(any());
            verify(ADB_HANDLER).rxa_disableEmulatorAnimations(any());
            verify(ADB_HANDLER, times(ANIM_DISABLE_CMD_COUNT)).rxa_changeSettings(any());
            verifyNoMoreInteractions(ADB_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion
}
