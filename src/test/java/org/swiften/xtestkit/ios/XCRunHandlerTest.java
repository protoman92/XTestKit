package org.swiften.xtestkit.ios;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.ios.param.StartSimulatorParam;
import org.swiften.xtestkit.system.process.ProcessRunner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Created by haipham on 4/8/17.
 */
@SuppressWarnings("MessageMissingOnTestNGAssertion")
public final class XCRunHandlerTest {
    @NotNull private final XCRunHandler XC_HANDLER;
    @NotNull private final ProcessRunner RUNNER;
    @NotNull private final StartSimulatorParam SS_PARAM;
    @NotNull private final RetryType RETRY;
    @NotNull private final String DEVICE_NAME;
    @NotNull private final String DEVICE_UID;

    {
        XC_HANDLER = spy(new XCRunHandler());

        /* We spy this class to check for method calls */
        RUNNER = spy(new ProcessRunner());

        /* Use this parameter when a RetryType is needed */
        SS_PARAM = mock(StartSimulatorParam.class);
        RETRY = mock(RetryType.class);

        DEVICE_NAME = "iPhone 7 Plus";

        /* Return this when calling SS_PARAM#deviceUID() */
        DEVICE_UID = "CF6E7ACD-F818-4145-A140-75CF1F229A8C";
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(RUNNER).when(XC_HANDLER).processRunner();
        doReturn(DEVICE_UID).when(SS_PARAM).deviceUID();

        /* Shorten the delay for testing */
        doReturn(100L).when(XC_HANDLER).simulatorBootRetryDelay();
        doReturn(3).when(RETRY).retries();
    }

    @AfterMethod
    public void afterMethod() {
        reset(XC_HANDLER, RUNNER, SS_PARAM, RETRY);
    }

    //region Start Simulator
    @Test
    @SuppressWarnings("unchecked")
    public void test_startSimulatorWithError_shouldThrow() {
        try {
            // Setup
            doReturn(RxUtil.error()).when(RUNNER).rxa_execute(contains("CurrentDeviceUDID"));
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            XC_HANDLER.rxa_startSimulator(SS_PARAM).subscribe(subscriber);

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(RuntimeException.class);
            subscriber.assertNotComplete();
            verify(XC_HANDLER, atLeastOnce()).rxa_checkSimulatorBooted(any());
            verify(XC_HANDLER).rxa_startSimulator(any());
            verify(XC_HANDLER, times(2)).processRunner();
            verify(XC_HANDLER).simulatorBootRetryDelay();
            verify(XC_HANDLER).cmXCode();
            verify(XC_HANDLER).cmXCodeSimulator();
            verify(XC_HANDLER).cm_startSimulator(any());
            verify(XC_HANDLER).cmCheckSimulatorBooted(any());
            verify(XC_HANDLER).cmGetEnv(any(), any());
            verify(XC_HANDLER).cmGetHomeEnv(any());
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
            doReturn(Flowable.just("")).when(RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            XC_HANDLER.rxa_startSimulator(SS_PARAM).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertTrue(RxTestUtil.firstNextEvent(subscriber));
            verify(XC_HANDLER).rxa_checkSimulatorBooted(any());
            verify(XC_HANDLER).rxa_startSimulator(any());
            verify(XC_HANDLER, times(2)).processRunner();
            verify(XC_HANDLER).simulatorBootRetryDelay();
            verify(XC_HANDLER).cmXCode();
            verify(XC_HANDLER).cmXCodeSimulator();
            verify(XC_HANDLER).cm_startSimulator(any());
            verify(XC_HANDLER).cmCheckSimulatorBooted(any());
            verify(XC_HANDLER).cmGetEnv(any(), any());
            verify(XC_HANDLER).cmGetHomeEnv(any());
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
            doReturn(RxUtil.error()).when(RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            XC_HANDLER.rxa_stopSimulator(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertTrue(RxTestUtil.firstNextEvent(subscriber));
            verify(XC_HANDLER).rxa_stopSimulator(any());
            verify(XC_HANDLER).processRunner();
            verify(XC_HANDLER).cmStopSimulator();
            verifyNoMoreInteractions(XC_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion
}
