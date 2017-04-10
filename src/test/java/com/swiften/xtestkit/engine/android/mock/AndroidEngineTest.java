package com.swiften.xtestkit.engine.android.mock;

import com.swiften.xtestkit.kit.param.AfterClassParam;
import com.swiften.xtestkit.kit.param.AfterParam;
import com.swiften.xtestkit.kit.param.BeforeClassParam;
import com.swiften.xtestkit.engine.base.param.NavigateBack;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.android.ADBHandler;
import com.swiften.xtestkit.engine.mobile.android.AndroidEngine;
import com.swiften.xtestkit.engine.mobile.android.AndroidInstance;
import com.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import com.swiften.xtestkit.system.NetworkHandler;
import com.swiften.xtestkit.system.ProcessRunner;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.testng.Assert.*;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/22/17.
 */
public final class AndroidEngineTest {
    @NotNull private final AndroidEngine ENGINE;
    @NotNull private final AndroidInstance ANDROID_INSTANCE;
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final RetryProtocol RETRY;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final String DEVICE_NAME;
    @NotNull private final String DEVICE_UID;
    private final int RETRIES_ON_ERROR;

    {
        DEVICE_NAME = "Nexus_4_API_23";
        DEVICE_UID = "emulator-5556";

        ENGINE = spy(AndroidEngine.builder()
            .withDeviceName(DEVICE_NAME)
            .build());

        /* We return this androidInstance when calling
         * ENGINE.androidInstance() */
        ANDROID_INSTANCE = mock(AndroidInstance.class);

        /* We return this adbHandler when calling ENGINE.adbHandler() */
        ADB_HANDLER = spy(ADBHandler.builder().build());

        /* We return this networkHandler when calling ENGINE.networkHandler() */
        NETWORK_HANDLER = spy(NetworkHandler.builder().build());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ProcessRunner.builder().build());

        /* Create a mock here to fake retries() */
        RETRY = mock(RetryProtocol.class);

        RETRIES_ON_ERROR = 3;
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(ANDROID_INSTANCE).when(ENGINE).androidInstance();
        doReturn(ADB_HANDLER).when(ENGINE).adbHandler();
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();
        doReturn(NETWORK_HANDLER).when(ENGINE).networkHandler();
        doReturn(DEVICE_NAME).when(ANDROID_INSTANCE).deviceName();
        doReturn(DEVICE_UID).when(ANDROID_INSTANCE).deviceUID();

        /* Shorten the delay for testing */
        doReturn(100L).when(ADB_HANDLER).emulatorBootRetryDelay();

        /* We specifically mock this because starting emulator requires rather
         * complicated behaviors */
        when(RETRY.retries()).thenReturn(RETRIES_ON_ERROR);
    }

    @AfterMethod
    public void afterMethod() {
        reset(ENGINE, ADB_HANDLER, PROCESS_RUNNER, NETWORK_HANDLER, RETRY);
    }

    //region BeforeClass
    @Test
    @SuppressWarnings("unchecked")
    public void mock_beforeClass_shouldSucceed() {
        // Setup
        int correctPort = 10;
        doReturn(correctPort).when(ANDROID_INSTANCE).port();
        doReturn(Flowable.just(correctPort)).when(ADB_HANDLER).rxFindAvailablePort(any());
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxStartEmulator(any());
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxDisableEmulatorAnimations(any());
        ArgumentCaptor<StartEmulatorParam> SE_CAPTOR = ArgumentCaptor.forClass(StartEmulatorParam.class);
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxBeforeClass(BeforeClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).adbHandler();
        verify(ENGINE).androidInstance();
        verify(ENGINE).testMode();
        verify(ENGINE).deviceName();
        verify(ENGINE, atLeastOnce()).serverAddress();
        verify(ENGINE, atLeastOnce()).processRunner();
        verify(ENGINE).appiumStartDelay();
        verify(ENGINE).networkHandler();
        verify(ENGINE).serverQueue();
        verify(ENGINE).cmWhichAppium();
        verify(ENGINE).cmFallBackAppium();
        verify(ENGINE).cmStartLocalAppiumInstance(anyString(), anyInt());
        verify(ENGINE).rxStartLocalAppiumInstance(any());
        verify(ENGINE).startAppiumOnNewThread(anyString());
        verify(ENGINE).rxBeforeClass(any());
        verify(ADB_HANDLER).rxFindAvailablePort(any());
        verify(ADB_HANDLER).rxStartEmulator(SE_CAPTOR.capture());
        verify(ADB_HANDLER).rxDisableEmulatorAnimations(any());
        verify(ANDROID_INSTANCE).setPort(anyInt());
        verifyNoMoreInteractions(ENGINE);
        assertEquals(ANDROID_INSTANCE.port(), SE_CAPTOR.getValue().port());
    }
    //endregion

    //region AfterClass
    @Test
    @SuppressWarnings("unchecked")
    public void mock_afterClass_shouldSucceed() {
        // Setup
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxStopEmulator(any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxAfterClass(AfterClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE, times(3)).networkHandler();
        verify(ENGINE).androidInstance();
        verify(ENGINE).adbHandler();
        verify(ENGINE, atLeastOnce()).serverAddress();
        verify(ENGINE).testMode();
        verify(ENGINE).rxAfterClass(any());
        verify(ENGINE).rxStopLocalAppiumInstance();
        verify(ADB_HANDLER).rxStopEmulator(any());
        verify(NETWORK_HANDLER, times(2)).markPortAsAvailable(anyInt());
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion

    //region AfterMethod
    @Test
    @SuppressWarnings("unchecked")
    public void mock_afterMethod_shouldSucceed() {
        // Setup
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxClearCachedData(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxStopDriver();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxAfterMethod(AfterParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).androidInstance();
        verify(ENGINE).adbHandler();
        verify(ENGINE).appPackage();
        verify(ENGINE).rxStopDriver();
        verify(ENGINE).rxAfterMethod(any());
        verify(ADB_HANDLER).rxClearCachedData(any());
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion

    //region Dismiss Keyboard
    @Test
    @SuppressWarnings("unchecked")
    public void mock_dismissHiddenKeyboard_shouldDoNothing() {
        // Setup
        doReturn(Flowable.just(false)).when(ADB_HANDLER).rxCheckKeyboardOpen(any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxDismissKeyboard().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ADB_HANDLER).rxCheckKeyboardOpen(any());
        verify(ENGINE).adbHandler();
        verify(ENGINE).androidInstance();
        verify(ENGINE).rxDismissKeyboard();
        verify(ENGINE, never()).rxNavigateBack(any(NavigateBack.class));
        verifyNoMoreInteractions(ENGINE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_dismissKeyboard_shouldSucceed() {
        // Setup
        doReturn(Flowable.just(true))
            .when(ADB_HANDLER).rxCheckKeyboardOpen(any());

        doReturn(Flowable.just(true))
            .when(ENGINE).rxNavigateBack(any(NavigateBack.class));

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxDismissKeyboard().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ADB_HANDLER).rxCheckKeyboardOpen(any());
        verify(ENGINE).adbHandler();
        verify(ENGINE).androidInstance();
        verify(ENGINE).rxNavigateBack(any(NavigateBack.class));
        verify(ENGINE).rxNavigateBackOnce();
        verify(ENGINE).rxDismissKeyboard();
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion
}
