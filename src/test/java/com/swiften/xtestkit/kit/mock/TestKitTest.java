package com.swiften.xtestkit.kit.mock;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.engine.base.param.StartEnvParam;
import com.swiften.engine.base.param.StopEnvParam;
import com.swiften.kit.TestKit;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.springframework.util.Assert.*;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by haipham on 3/24/17.
 */
public class TestKitTest {
    @NotNull private final TestKit TEST_KIT;
    @NotNull private final PlatformEngine ENGINE;

    {
        /* Redirect all calls to PlatformEngine here and check that method
         * call counts are correct */
        ENGINE = mock(PlatformEngine.class);

        /* Redirect all calls to TestKit here and check that method call
         * counts are correct */
        TEST_KIT = spy(TestKit.newBuilder().build());
    }

    @Before
    public void setUp() {
        doReturn(Flowable.just(true)).when(ENGINE).rxStartTestEnvironment();
        doReturn(Flowable.just(true)).when(ENGINE).rxStopTestEnvironment();
        doReturn(Flowable.just(true)).when(ENGINE).rxStartDriver();
        doReturn(Flowable.just(true)).when(ENGINE).rxStopDriver();
        doReturn(ENGINE).when(TEST_KIT).currentEngine();
    }

    //region BeforeClass
    @Test
    @SuppressWarnings("unchecked")
    public void mock_beforeClass_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxBeforeClass().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        isTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).currentEngine();
        verify(ENGINE).rxStartTestEnvironment();
    }
    //endregion

    //region Before
    @Test
    @SuppressWarnings("unchecked")
    public void mock_before_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxBefore().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        isTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).currentEngine();
        verify(ENGINE).rxStartDriver();
    }
    //endregion

    //region AfterClass
    @Test
    @SuppressWarnings("unchecked")
    public void mock_afterClass_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxAfterClass().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        isTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).currentEngine();
        verify(ENGINE).rxStopTestEnvironment();
    }
    //endregion

    //region After
    @Test
    @SuppressWarnings("unchecked")
    public void mock_after_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxAfter().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        isTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).currentEngine();
        verify(ENGINE).rxStopDriver();
    }
    //endregion
}
