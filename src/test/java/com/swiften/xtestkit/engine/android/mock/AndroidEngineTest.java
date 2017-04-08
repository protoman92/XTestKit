package com.swiften.xtestkit.engine.android.mock;

import com.swiften.xtestkit.engine.base.param.NavigateBack;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.android.ADBHandler;
import com.swiften.xtestkit.engine.mobile.android.AndroidEngine;
import com.swiften.xtestkit.system.NetworkHandler;
import com.swiften.xtestkit.system.ProcessRunner;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
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
public final class AndroidEngineTest {
    @NotNull private final AndroidEngine ENGINE;
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final RetryProtocol RETRY;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    private final int RETRIES_ON_ERROR;

    {
        ENGINE = spy(AndroidEngine.builder()
            .withDeviceName("Nexus_4_API_23")
            .build());

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
        doReturn(ADB_HANDLER).when(ENGINE).adbHandler();
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();
        doReturn(NETWORK_HANDLER).when(ENGINE).networkHandler();
        doReturn(ENGINE).when(NETWORK_HANDLER).processRunner();

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
    public void mock_dismissHidden_shouldSucceed() {
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
}
