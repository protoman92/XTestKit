package com.swiften.xtestkit.engine.android.mock;

import com.swiften.xtestkit.engine.base.param.BeforeClassParam;
import com.swiften.xtestkit.engine.base.param.NavigateBack;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.android.ADBHandler;
import com.swiften.xtestkit.engine.mobile.android.AndroidEngine;
import com.swiften.xtestkit.engine.mobile.android.AndroidInstance;
import com.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import com.swiften.xtestkit.system.NetworkHandler;
import com.swiften.xtestkit.system.ProcessRunner;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import com.swiften.xtestkit.util.Log;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.apache.commons.validator.Arg;
import org.jetbrains.annotations.NotNull;
import static org.testng.Assert.*;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;

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
    private final int RETRIES_ON_ERROR;

    {
        DEVICE_NAME = "Nexus_4_API_23";

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
        doReturn(ENGINE).when(NETWORK_HANDLER).processRunner();
        doReturn(DEVICE_NAME).when(ANDROID_INSTANCE).deviceName();

        /* Shorten the delay for testing */
        doReturn(100L).when(ADB_HANDLER).emulatorBootRetryDelay();

        /* We specifically mock this because starting emulator requires rather
         * complicated behaviors */
        when(RETRY.minRetries()).thenReturn(RETRIES_ON_ERROR);
        when(RETRY.maxRetries()).thenReturn(RETRIES_ON_ERROR);
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
        doReturn(Flowable.just(correctPort)).when(ADB_HANDLER).rxFindAvailablePort();
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxStartEmulator(any());
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxDisableEmulatorAnimations();
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
        verify(ENGINE).rxBeforeClass(any());
        verify(ADB_HANDLER).rxFindAvailablePort();
        verify(ADB_HANDLER).rxStartEmulator(SE_CAPTOR.capture());
        verify(ADB_HANDLER).rxDisableEmulatorAnimations();
        verify(ANDROID_INSTANCE).setPort(anyInt());
        verifyNoMoreInteractions(ENGINE);
        assertEquals(ANDROID_INSTANCE.port(), SE_CAPTOR.getValue().port());
    }
    //endregion

    //region Dismiss Keyboard
    @Test
    @SuppressWarnings("unchecked")
    public void mock_dismissHiddenKeyboard_shouldDoNothing() {
        // Setup
        doReturn(Flowable.just(false)).when(ADB_HANDLER).rxCheckKeyboardOpen();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxDismissKeyboard().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ADB_HANDLER).rxCheckKeyboardOpen();
        verify(ENGINE).rxDismissKeyboard();
        verify(ENGINE).adbHandler();
        verify(ENGINE, never()).rxNavigateBack(any(NavigateBack.class));
        verifyNoMoreInteractions(ENGINE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_dismissKeyboard_shouldSucceed() {
        // Setup
        doReturn(Flowable.just(true))
            .when(ADB_HANDLER).rxCheckKeyboardOpen();

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
        verify(ADB_HANDLER).rxCheckKeyboardOpen();
        verify(ENGINE).adbHandler();
        verify(ENGINE).rxNavigateBack(any(NavigateBack.class));
        verify(ENGINE).rxNavigateBack();
        verify(ENGINE).rxDismissKeyboard();
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion
}
