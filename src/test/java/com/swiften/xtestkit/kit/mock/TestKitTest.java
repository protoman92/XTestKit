package com.swiften.xtestkit.kit.mock;

import com.swiften.xtestkit.engine.base.Platform;
import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.AfterClassParam;
import com.swiften.xtestkit.engine.base.param.AfterParam;
import com.swiften.xtestkit.engine.base.param.BeforeClassParam;
import com.swiften.xtestkit.engine.base.param.BeforeParam;
import com.swiften.xtestkit.engine.mobile.android.AndroidEngine;
import com.swiften.xtestkit.engine.mobile.ios.IOSEngine;
import com.swiften.xtestkit.kit.TestKit;
import com.swiften.xtestkit.system.NetworkHandler;
import com.swiften.xtestkit.system.ProcessRunner;
import com.swiften.xtestkit.util.Log;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.testng.Assert.*;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by haipham on 3/24/17.
 */
public class TestKitTest {
    @NotNull private final TestKit TEST_KIT;
    @NotNull private final PlatformEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;

    {
        /* Redirect all calls to PlatformEngine here and check that method
         * call counts are correct */
        ENGINE = mock(PlatformEngine.class);

        /* Return this when we call ENGINE.processRunner() */
        PROCESS_RUNNER = spy(ProcessRunner.builder().build());

        /* Return this when we call ENGINE.networkHandler() */
        NETWORK_HANDLER = spy(NetworkHandler.builder().build());

        /* Redirect all calls to TestKit here and check that method call
         * counts are correct */
        TEST_KIT = spy(TestKit.builder().build());
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(Flowable.just(true)).when(ENGINE).rxBeforeClass(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxAfterClass(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxBeforeMethod(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxAfterMethod(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxOnFreshStart();
        doReturn(Flowable.just(true)).when(ENGINE).rxOnAllTestsFinished();
        doReturn(Flowable.just(true)).when(ENGINE).rxOnBatchStarted(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxOnBatchFinished(any());
        doReturn(Collections.singletonList(ENGINE)).when(TEST_KIT).engines();
        doReturn(ENGINE).when(TEST_KIT).engine(anyInt());
        doReturn(PROCESS_RUNNER).when(TEST_KIT).processRunner();
        doReturn(NETWORK_HANDLER).when(TEST_KIT).networkHandler();
        doReturn(TEST_KIT).when(NETWORK_HANDLER).processRunner();
    }

    @AfterMethod
    public void afterMethod() {
        reset(ENGINE, TEST_KIT, NETWORK_HANDLER, PROCESS_RUNNER);
    }

    //region TestListener
    @Test
    @SuppressWarnings("unchecked")
    public void mock_distinctEngines_shouldReturnCorrectResults() {
        // Setup
        PlatformEngine engine1 = mock(AndroidEngine.class);
        PlatformEngine engine2 = mock(AndroidEngine.class);
        PlatformEngine engine3 = mock(IOSEngine.class);
        PlatformEngine engine4 = mock(IOSEngine.class);

        doReturn(Arrays.asList(
            engine1, engine2, engine3, engine4
        )).when(TEST_KIT).engines();

        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxDistinctEngines().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(TEST_KIT).rxDistinctEngines();
        verify(TEST_KIT).engines();
        verifyNoMoreInteractions(TEST_KIT);
        assertEquals(TestUtil.getNextEvents(subscriber).size(), 2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_beforeAllTests_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            TEST_KIT.rxOnFreshStart().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxOnFreshStart();
            verify(TEST_KIT).rxOnFreshStart();
            verify(TEST_KIT).networkHandler();
            verify(TEST_KIT).processRunner();
            verify(TEST_KIT).engines();
            verify(TEST_KIT).rxExecute(anyString());
            verify(TEST_KIT).rxDistinctEngines();
            verify(PROCESS_RUNNER).execute(anyString());
            verify(PROCESS_RUNNER).rxExecute(anyString());
            verify(NETWORK_HANDLER).processRunner();
            verify(NETWORK_HANDLER).cmKillAll(anyString());
            verify(NETWORK_HANDLER).rxKillAll(anyString());
            verifyNoMoreInteractions(ENGINE);
            verifyNoMoreInteractions(TEST_KIT);
            verifyNoMoreInteractions(PROCESS_RUNNER);
            verifyNoMoreInteractions(NETWORK_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_afterAllTests_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            TEST_KIT.rxOnAllTestsFinished().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxOnAllTestsFinished();
            verify(TEST_KIT).rxOnAllTestsFinished();
            verify(TEST_KIT).networkHandler();
            verify(TEST_KIT).processRunner();
            verify(TEST_KIT).engines();
            verify(TEST_KIT).rxExecute(anyString());
            verify(TEST_KIT).rxDistinctEngines();
            verify(PROCESS_RUNNER).execute(anyString());
            verify(PROCESS_RUNNER).rxExecute(anyString());
            verify(NETWORK_HANDLER).processRunner();
            verify(NETWORK_HANDLER).cmKillAll(anyString());
            verify(NETWORK_HANDLER).rxKillAll(anyString());
            verifyNoMoreInteractions(ENGINE);
            verifyNoMoreInteractions(TEST_KIT);
            verifyNoMoreInteractions(PROCESS_RUNNER);
            verifyNoMoreInteractions(NETWORK_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_beforeBatchTests_shouldSucceed() {
        try {
            // Setup
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            TEST_KIT.rxOnBatchStarted(new int[1]).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxOnBatchStarted(any());
            verify(TEST_KIT).rxEnginesFromIndexes(any());
            verify(TEST_KIT).rxOnBatchStarted(any());
            verify(TEST_KIT).engines();
            verifyNoMoreInteractions(ENGINE);
            verifyNoMoreInteractions(TEST_KIT);
            verifyNoMoreInteractions(PROCESS_RUNNER);
            verifyNoMoreInteractions(NETWORK_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_afterBatchTests_shouldSucceed() {
        try {
            // Setup
            TestSubscriber subscriber = TestSubscriber.create();

            // When
            TEST_KIT.rxOnBatchFinished(new int[1]).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxOnBatchFinished(any());
            verify(TEST_KIT).rxEnginesFromIndexes(any());
            verify(TEST_KIT).rxOnBatchFinished(any());
            verify(TEST_KIT).engines();
            verifyNoMoreInteractions(ENGINE);
            verifyNoMoreInteractions(TEST_KIT);
            verifyNoMoreInteractions(PROCESS_RUNNER);
            verifyNoMoreInteractions(NETWORK_HANDLER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region BeforeClass
    @Test
    @SuppressWarnings("unchecked")
    public void mock_beforeClass_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxBeforeClass(BeforeClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxBeforeClass(any());
        verify(ENGINE).rxBeforeClass(any());
        verifyNoMoreInteractions(TEST_KIT);
        verifyNoMoreInteractions(ENGINE);
        verifyNoMoreInteractions(NETWORK_HANDLER);
        verifyNoMoreInteractions(PROCESS_RUNNER);
    }
    //endregion

    //region BeforeMethod
    @Test
    @SuppressWarnings("unchecked")
    public void mock_beforeMethod_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxBeforeMethod(BeforeParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxBeforeMethod(any());
        verify(ENGINE).rxBeforeMethod(any());
        verifyNoMoreInteractions(TEST_KIT);
        verifyNoMoreInteractions(ENGINE);
        verifyNoMoreInteractions(NETWORK_HANDLER);
        verifyNoMoreInteractions(PROCESS_RUNNER);
    }
    //endregion

    //region AfterClass
    @Test
    @SuppressWarnings("unchecked")
    public void mock_afterClass_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxAfterClass(AfterClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxAfterClass(any());
        verify(ENGINE).rxAfterClass(any());
        verifyNoMoreInteractions(TEST_KIT);
        verifyNoMoreInteractions(ENGINE);
        verifyNoMoreInteractions(NETWORK_HANDLER);
        verifyNoMoreInteractions(PROCESS_RUNNER);
    }
    //endregion

    //region After
    @Test
    @SuppressWarnings("unchecked")
    public void mock_afterMethod_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxAfterMethod(AfterParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxAfterMethod(any());
        verify(ENGINE).rxAfterMethod(any());
        verifyNoMoreInteractions(TEST_KIT);
        verifyNoMoreInteractions(ENGINE);
        verifyNoMoreInteractions(NETWORK_HANDLER);
        verifyNoMoreInteractions(PROCESS_RUNNER);
    }
    //endregion
}
