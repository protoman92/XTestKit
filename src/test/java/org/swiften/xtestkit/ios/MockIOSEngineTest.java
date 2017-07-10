package org.swiften.xtestkit.ios;

import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.xtestkit.ios.capability.IOSEngineCapability;
import org.swiften.xtestkit.ios.type.IOSErrorType;
import org.swiften.xtestkitcomponents.system.process.ProcessRunner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by haipham on 3/31/17.
 */
public final class MockIOSEngineTest implements ErrorProviderType, IOSErrorType {
    @NotNull private final IOSEngine ENGINE;
    @NotNull private final IOSEngineCapability CAPABILITY;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final RetryProviderType RETRY;

    {
        ENGINE = spy(IOSEngine.builder()
            .withDeviceUID("CF6E7ACD-F818-4145-A140-75CF1F229A8C")
            .withDeviceName("iPhone 7 Plus")
            .build());

        /* Return this capability when we call ENGINE.capabilityType() */
        CAPABILITY = mock(IOSEngineCapability.class);

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(new ProcessRunner());

        /* Use this parameter when a RetryProviderType is needed */
        RETRY = mock(RetryProviderType.class);
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
        ENGINE.rxa_startDriver(RETRY).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(NOT_AVAILABLE);
        subscriber.assertNotComplete();
        verify(ENGINE).app();
        verify(ENGINE).appiumVersion();
        verify(ENGINE).automation();
        verify(ENGINE).deviceName();
        verify(ENGINE).platform();
        verify(ENGINE).platformName();
        verify(ENGINE).platformVersion();
        verify(ENGINE).appPackage();
        verify(ENGINE).launchTimeout();
        verify(ENGINE).deviceUID();
        verify(ENGINE).capabilities();
        verify(ENGINE).capabilityType();
        verify(ENGINE).rxa_startDriver(any());
        verifyNoMoreInteractions(ENGINE);

        try {
            ENGINE.driver();
        } catch (Exception e) {
            assertEquals(e.getMessage(), NOT_AVAILABLE);
        }
    }
    //endregion
}
