package org.swiften.xtestkit.ios;

import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.base.type.EngineErrorType;
import org.swiften.xtestkit.ios.type.IOSErrorType;
import org.swiften.xtestkit.ios.capability.IOSCap;
import org.swiften.xtestkit.system.process.ProcessRunner;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.testng.Assert.*;

import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/31/17.
 */
public final class MockIOSEngineTest implements EngineErrorType, IOSErrorType {
    @NotNull private final IOSEngine ENGINE;
    @NotNull private final IOSCap CAPABILITY;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final RetryType RETRY;

    {
        ENGINE = spy(IOSEngine.builder()
            .withDeviceUID("CF6E7ACD-F818-4145-A140-75CF1F229A8C")
            .withDeviceName("iPhone 7 Plus")
            .build());

        /* Return this capability when we call ENGINE.capabilityType() */
        CAPABILITY = mock(IOSCap.class);

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ProcessRunner.builder().build());

        /* Use this parameter when a RetryType is needed */
        RETRY = mock(RetryType.class);
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(CAPABILITY).when(ENGINE).capabilityType();
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();
        doReturn(3).when(RETRY).retries();
    }

    @AfterMethod
    public void afterMethod() {
        reset(CAPABILITY, ENGINE, PROCESS_RUNNER);
    }

    //region Capabilities Setup
    @Test
    @SuppressWarnings("unchecked")
    public void test_addInvalidFileName_shouldThrow() {
        // Setup
        doReturn("").when(CAPABILITY).appPath(any());
        doReturn(false).when(CAPABILITY).hasValidAppName(any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rx_startDriver(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(INSUFFICIENT_SETTINGS);
        subscriber.assertNotComplete();
        verify(ENGINE).browserName();
        verify(ENGINE).app();
        verify(ENGINE).appiumVersion();
        verify(ENGINE).automationName();
        verify(ENGINE).deviceName();
        verify(ENGINE).platformName();
        verify(ENGINE).platformVersion();
        verify(ENGINE).startDriverOnlyOnce();
        verify(ENGINE).appPackage();
        verify(ENGINE).launchTimeout();
        verify(ENGINE).deviceUID();
        verify(ENGINE).capabilities();
        verify(ENGINE).capabilityType();
        verify(ENGINE).rx_startDriver(any());
        verifyNoMoreInteractions(ENGINE);

        try {
            ENGINE.driver();
        } catch (Exception e) {
            assertEquals(e.getMessage(), DRIVER_UNAVAILABLE);
        }
    }
    //endregion
}
