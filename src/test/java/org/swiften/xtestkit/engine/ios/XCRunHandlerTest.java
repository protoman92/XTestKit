package org.swiften.xtestkit.engine.ios;

import org.swiften.xtestkit.engine.base.type.RetryType;
import org.swiften.xtestkit.engine.mobile.ios.XCRunHandler;
import org.swiften.xtestkit.engine.mobile.ios.param.StartSimulatorParam;
import org.swiften.xtestkit.system.ProcessRunner;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Created by haipham on 4/8/17.
 */
public final class XCRunHandlerTest {
    @NotNull private final XCRunHandler XC_HANDLER;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final StartSimulatorParam SS_PARAM;
    @NotNull private final RetryType RETRY;
    @NotNull private final String DEVICE_NAME;
    @NotNull private final String DEVICE_UID;

    {
        XC_HANDLER = spy(XCRunHandler.builder().build());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ProcessRunner.builder().build());

        /* Use this parameter when a RetryType is needed */
        SS_PARAM = mock(StartSimulatorParam.class);
        RETRY = mock(RetryType.class);

        DEVICE_NAME = "iPhone 7 Plus";

        /* Return this when calling SS_PARAM#deviceUID() */
        DEVICE_UID = "CF6E7ACD-F818-4145-A140-75CF1F229A8C";
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(PROCESS_RUNNER).when(XC_HANDLER).processRunner();
        doReturn(DEVICE_UID).when(SS_PARAM).deviceUID();

        /* Shorten the delay for testing */
        doReturn(100L).when(XC_HANDLER).simulatorBootRetryDelay();
        doReturn(3).when(RETRY).retries();
    }

    @AfterMethod
    public void afterMethod() {
        reset(XC_HANDLER, PROCESS_RUNNER, SS_PARAM, RETRY);
    }

    //region Start Simulator
    @Test
    @SuppressWarnings("unchecked")
    public void test_startSimulatorWithError_shouldThrow() {
        try {
            // Setup
            doThrow(new RuntimeException())
                .when(PROCESS_RUNNER).execute(contains("CurrentDeviceUDID"));

            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            XC_HANDLER.rxStartSimulator(SS_PARAM).subscribe(subscriber);

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(RuntimeException.class);
            subscriber.assertNotComplete();
            verify(XC_HANDLER, atLeastOnce()).rxCheckSimulatorBooted(anyString());
            verify(XC_HANDLER).rxStartSimulator(any());
            verify(XC_HANDLER, times(2)).processRunner();
            verify(XC_HANDLER).simulatorBootRetryDelay();
            verify(XC_HANDLER).cmXCode();
            verify(XC_HANDLER).cmXCodeSimulator();
            verify(XC_HANDLER).cmStartSimulator(anyString());
            verify(XC_HANDLER).cmCheckSimulatorBooted(anyString());
            verify(XC_HANDLER).cmGetEnv(anyString(), anyString());
            verify(XC_HANDLER).cmGetHomeEnv(anyString());
            verify(XC_HANDLER).cmSimctl();
            verify(XC_HANDLER).cmXCRun();
            verifyNoMoreInteractions(XC_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_startSimulator_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            XC_HANDLER.rxStartSimulator(SS_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
            verify(XC_HANDLER).rxCheckSimulatorBooted(anyString());
            verify(XC_HANDLER).rxStartSimulator(any());
            verify(XC_HANDLER, times(2)).processRunner();
            verify(XC_HANDLER).simulatorBootRetryDelay();
            verify(XC_HANDLER).cmXCode();
            verify(XC_HANDLER).cmXCodeSimulator();
            verify(XC_HANDLER).cmStartSimulator(anyString());
            verify(XC_HANDLER).cmCheckSimulatorBooted(anyString());
            verify(XC_HANDLER).cmGetEnv(anyString(), anyString());
            verify(XC_HANDLER).cmGetHomeEnv(anyString());
            verify(XC_HANDLER).cmSimctl();
            verify(XC_HANDLER).cmXCRun();
            verifyNoMoreInteractions(XC_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region Stop Simulator
    @Test
    @SuppressWarnings("unchecked")
    public void test_stopSimulatorWithError_shouldSucceed() {
        try {
            // Setup
            doThrow(new RuntimeException()).when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            XC_HANDLER.rxStopSimulator(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
            verify(XC_HANDLER).rxStopSimulator(any());
            verify(XC_HANDLER).processRunner();
            verify(XC_HANDLER).cmStopSimulator();
            verifyNoMoreInteractions(XC_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion
}
