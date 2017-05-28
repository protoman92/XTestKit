package org.swiften.xtestkit.kit;

import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.android.AndroidEngine;
import org.swiften.xtestkit.ios.IOSEngine;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.system.network.NetworkHandler;
import org.swiften.xtestkit.system.process.ProcessRunner;
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
@SuppressWarnings("MessageMissingOnTestNGAssertion")
public final class MockTestKitTest {
    @NotNull private final TestKit TEST_KIT;
    @NotNull private final Engine ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;

    {
        /* Redirect all calls to Engine here and check that method
         * call counts are correct */
        ENGINE = mock(Engine.class);

        /* Return this when we call ENGINE.processRunner() */
        PROCESS_RUNNER = spy(new ProcessRunner());

        /* Return this when we call ENGINE.networkHandler() */
        NETWORK_HANDLER = spy(new NetworkHandler());

        /* Redirect all calls to TestKit here and check that method call
         * counts are correct */
        TEST_KIT = spy(TestKit.builder().build());
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(Flowable.just(true)).when(ENGINE).rxa_beforeClass(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxa_afterClass(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxa_beforeMethod(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxa_afterMethod(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxa_onFreshStart();
        doReturn(Flowable.just(true)).when(ENGINE).rxa_onAllTestsFinished();
        doReturn(Flowable.just(true)).when(ENGINE).rxa_onBatchStarted(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxa_onBatchFinished(any());
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
        Engine engine1 = mock(AndroidEngine.class);
        Engine engine2 = mock(AndroidEngine.class);
        Engine engine3 = mock(IOSEngine.class);
        Engine engine4 = mock(IOSEngine.class);

        doReturn(Arrays.asList(
            engine1, engine2, engine3, engine4
        )).when(TEST_KIT).engines();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        TEST_KIT.rxe_distinctEngines().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(RxTestUtil.nextEvents(subscriber).size(), 2);
        verify(TEST_KIT).rxe_distinctEngines();
        verify(TEST_KIT).engines();
        verifyNoMoreInteractions(TEST_KIT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_beforeAllTests_shouldSucceed() {
        try {
            // Setup
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            TEST_KIT.rxa_onFreshStart().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxa_onFreshStart();
            verify(TEST_KIT).rxa_onFreshStart();
            verify(TEST_KIT).rxa_killAllAppiumInstances();
            verify(TEST_KIT).cmKillAllAppiumInstances();
            verify(TEST_KIT).networkHandler();
            verify(TEST_KIT).engines();
            verify(TEST_KIT).rxe_distinctEngines();
            verify(NETWORK_HANDLER).rxa_killAll(any());
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
            doReturn(RxUtil.error()).when(PROCESS_RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            TEST_KIT.rxa_onFreshStart().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxa_onFreshStart();
            verify(TEST_KIT).rxa_onFreshStart();
            verify(TEST_KIT).rxa_killAllAppiumInstances();
            verify(TEST_KIT).cmKillAllAppiumInstances();
            verify(TEST_KIT).networkHandler();
            verify(TEST_KIT).engines();
            verify(TEST_KIT).rxe_distinctEngines();
            verify(NETWORK_HANDLER).rxa_killAll(any());
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
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            TEST_KIT.rxa_onAllTestsFinished().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxa_onAllTestsFinished();
            verify(TEST_KIT).rxa_onAllTestsFinished();
            verify(TEST_KIT).networkHandler();
            verify(TEST_KIT).engines();
            verify(TEST_KIT).cmKillAllAppiumInstances();
            verify(TEST_KIT).rxa_killAllAppiumInstances();
            verify(TEST_KIT).rxe_distinctEngines();
            verify(NETWORK_HANDLER).rxa_killAll(any());
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
            doReturn(RxUtil.error()).when(PROCESS_RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            TEST_KIT.rxa_onAllTestsFinished().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxa_onAllTestsFinished();
            verify(TEST_KIT).rxa_onAllTestsFinished();
            verify(TEST_KIT).networkHandler();
            verify(TEST_KIT).engines();
            verify(TEST_KIT).cmKillAllAppiumInstances();
            verify(TEST_KIT).rxe_distinctEngines();
            verify(TEST_KIT).rxa_killAllAppiumInstances();
            verify(NETWORK_HANDLER).rxa_killAll(any());
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
            TEST_KIT.rxa_onBatchStarted(new int[1]).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxa_onBatchStarted(any());
            verify(TEST_KIT).rxe_enginesFromIndexes(any());
            verify(TEST_KIT).rxa_onBatchStarted(any());
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
            TEST_KIT.rxa_onBatchFinished(new int[1]).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).rxa_onBatchFinished(any());
            verify(TEST_KIT).rxe_enginesFromIndexes(any());
            verify(TEST_KIT).rxa_onBatchFinished(any());
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
        TEST_KIT.rxa_beforeClass(BeforeClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxa_beforeClass(any());
        verify(ENGINE).rxa_beforeClass(any());
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
        TEST_KIT.rxa_beforeMethod(BeforeParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxa_beforeMethod(any());
        verify(ENGINE).rxa_beforeMethod(any());
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
        TEST_KIT.rxa_afterClass(AfterClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxa_afterClass(any());
        verify(ENGINE).rxa_afterClass(any());
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
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
        verify(TEST_KIT).engine(anyInt());
        verify(TEST_KIT).rxAfterMethod(any());
        verify(ENGINE).rxa_afterMethod(any());
        verifyNoMoreInteractions(TEST_KIT);
    }
    //endregion
}
