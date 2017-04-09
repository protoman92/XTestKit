package com.swiften.xtestkit.engine.ios.mock;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.base.protocol.ErrorProtocol;
import com.swiften.xtestkit.engine.mobile.ios.IOSEngine;
import com.swiften.xtestkit.engine.mobile.ios.protocol.IOSErrorProtocol;
import com.swiften.xtestkit.system.ProcessRunner;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/31/17.
 */
public class IOSEngineTest implements ErrorProtocol, IOSErrorProtocol {
    @NotNull private final IOSEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final RetryProtocol RETRY;

    {
        ENGINE = spy(IOSEngine.builder()
            .withDeviceUID("CF6E7ACD-F818-4145-A140-75CF1F229A8C")
            .withDeviceName("iPhone 7 Plus")
            .build());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ProcessRunner.builder().build());

        /* Use this parameter when a RetryProtocol is needed */
        RETRY = mock(RetryProtocol.class);
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();
        doReturn(3).when(RETRY).retries();
    }

    @AfterMethod
    public void afterMethod() {
        reset(ENGINE, PROCESS_RUNNER);
    }

    //region Capabilities Setup
    @Test
    @SuppressWarnings("unchecked")
    public void mock_addInvalidFileName_shouldThrow() {
        // Setup
        doReturn(true).when(ENGINE).hasAllRequiredInformation();
        doReturn(false).when(ENGINE).hasCorrectFileExtension();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxStartDriver(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(INVALID_APP_EXTENSION);
        subscriber.assertNotComplete();
        verify(ENGINE).hasAllRequiredInformation();
        verify(ENGINE).hasCorrectFileExtension();
        verify(ENGINE).startDriverDelay();
        verify(ENGINE).rxHasCorrectFileExtension();
        verify(ENGINE).rxHasAllRequiredInformation();
        verify(ENGINE).rxStartDriver(any());
        verifyNoMoreInteractions(ENGINE);

        try {
            ENGINE.driver();
        } catch (Exception e) {
            assertEquals(e.getMessage(), DRIVER_UNAVAILABLE);
        }
    }
    //endregion
}
