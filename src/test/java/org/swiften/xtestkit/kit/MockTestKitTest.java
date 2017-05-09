package org.swiften.xtestkit.kit;

import org.swiften.xtestkit.base.BaseEngine;
import org.swiften.xtestkit.mobile.android.AndroidEngine;
import org.swiften.xtestkit.mobile.ios.IOSEngine;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.system.NetworkHandler;
import org.swiften.xtestkit.system.ProcessRunner;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.testng.Assert.*;

import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by haipham on 3/24/17.
 */
public final class MockTestKitTest {
    @NotNull private final TestKit TEST_KIT;
    @NotNull private final BaseEngine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;

    {
        /* Redirect all calls to BaseEngine here and check that method
         * call counts are correct */
        ENGINE = mock(BaseEngine.class);

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
        doReturn(PROCESS_RUNNER).when(NETWORK_HANDLER).processRunner();
    }

    @AfterMethod
    public void afterMethod() {
        reset(ENGINE, TEST_KIT, NETWORK_HANDLER, PROCESS_RUNNER);
    }

    //region TestListenerType
    @Test
    @SuppressWarnings("unchecked")
    public void test_distinctEngines_shouldReturnCorrectResults() {
        // Setup
        BaseEngine engine1 = mock(AndroidEngine.class);
        BaseEngine engine2 = mock(AndroidEngine.class);
        BaseEngine engine3 = mock(IOSEngine.class);
        BaseEngine engine4 = mock(IOSEngine.class);

        doReturn(Arrays.asList(
            engine1, engine2, engine3, engine4
        )).when(TEST_KIT).engines();

        TestSubscriber subscriber = CustomTestSubscriber.create();

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
        assertEquals(RxTestUtil.getNextEvents(subscriber).size(), 2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_beforeAllTests_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            TEST_KIT.rxOnFreshStart().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxOnFreshStart();
            verify(TEST_KIT).rxOnFreshStart();
            verify(TEST_KIT).rxKillAllAppiumInstances();
            verify(TEST_KIT).cmKillAllAppiumInstances();
            verify(TEST_KIT).networkHandler();
            verify(TEST_KIT).engines();
            verify(TEST_KIT).rxDistinctEngines();
            verify(NETWORK_HANDLER).rxKillAll(anyString());
            verifyNoMoreInteractions(TEST_KIT);;
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_beforeAllTestWithKillAllError_shouldSucceed() {
        try {
            // Setup
            doThrow(new RuntimeException()).when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            TEST_KIT.rxOnFreshStart().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxOnFreshStart();
            verify(TEST_KIT).rxOnFreshStart();
            verify(TEST_KIT).rxKillAllAppiumInstances();
            verify(TEST_KIT).cmKillAllAppiumInstances();
            verify(TEST_KIT).networkHandler();
            verify(TEST_KIT).engines();
            verify(TEST_KIT).rxDistinctEngines();
            verify(NETWORK_HANDLER).rxKillAll(anyString());
            verifyNoMoreInteractions(TEST_KIT);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_afterAllTests_shouldSucceed() {
        try {
            // Setup
            doReturn("Valid Output").when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

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
            verify(TEST_KIT).engines();
            verify(TEST_KIT).cmKillAllAppiumInstances();
            verify(TEST_KIT).rxKillAllAppiumInstances();
            verify(TEST_KIT).rxDistinctEngines();
            verify(NETWORK_HANDLER).rxKillAll(anyString());
            verifyNoMoreInteractions(TEST_KIT);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_afterAllTestWithKillAllError_shouldSucceed() {
        try {
            // Setup
            doThrow(new RuntimeException()).when(PROCESS_RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

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
            verify(TEST_KIT).engines();
            verify(TEST_KIT).cmKillAllAppiumInstances();
            verify(TEST_KIT).rxDistinctEngines();
            verify(TEST_KIT).rxKillAllAppiumInstances();
            verify(NETWORK_HANDLER).rxKillAll(anyString());
            verifyNoMoreInteractions(TEST_KIT);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_beforeBatchTests_shouldSucceed() {
        try {
            // Setup
            TestSubscriber subscriber = CustomTestSubscriber.create();

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
            verifyNoMoreInteractions(TEST_KIT);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_afterBatchTests_shouldSucceed() {
        try {
            // Setup
            TestSubscriber subscriber = CustomTestSubscriber.create();

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
            verifyNoMoreInteractions(TEST_KIT);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    //endregion

    //region BeforeClass
    @Test
    @SuppressWarnings("unchecked")
    public void test_beforeClass_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        TEST_KIT.rxBeforeClass(BeforeClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxBeforeClass(any());
        verify(ENGINE).rxBeforeClass(any());
        verifyNoMoreInteractions(TEST_KIT);
    }
    //endregion

    //region BeforeMethod
    @Test
    @SuppressWarnings("unchecked")
    public void test_beforeMethod_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        TEST_KIT.rxBeforeMethod(BeforeParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxBeforeMethod(any());
        verify(ENGINE).rxBeforeMethod(any());
        verifyNoMoreInteractions(TEST_KIT);
    }
    //endregion

    //region AfterClass
    @Test
    @SuppressWarnings("unchecked")
    public void test_afterClass_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        TEST_KIT.rxAfterClass(AfterClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxAfterClass(any());
        verify(ENGINE).rxAfterClass(any());
        verifyNoMoreInteractions(TEST_KIT);
    }
    //endregion

    //region After
    @Test
    @SuppressWarnings("unchecked")
    public void test_afterMethod_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        TEST_KIT.rxAfterMethod(AfterParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxAfterMethod(any());
        verify(ENGINE).rxAfterMethod(any());
        verifyNoMoreInteractions(TEST_KIT);
    }
    //endregion
}
