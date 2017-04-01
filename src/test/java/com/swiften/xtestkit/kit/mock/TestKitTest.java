package com.swiften.xtestkit.kit.mock;

import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.AfterClassParam;
import com.swiften.xtestkit.engine.base.param.AfterParam;
import com.swiften.xtestkit.engine.base.param.BeforeClassParam;
import com.swiften.xtestkit.engine.base.param.BeforeParam;
import com.swiften.xtestkit.kit.TestKit;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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
    public void before() {
        doReturn(Flowable.just(true)).when(ENGINE).rxBeforeClass(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxAfterClass(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxBefore(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxAfter(any());
        doReturn(ENGINE).when(TEST_KIT).currentEngine();
    }

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
        verify(TEST_KIT).currentEngine();
        verify(ENGINE).rxBeforeClass(any());
    }
    //endregion

    //region Before
    @Test
    @SuppressWarnings("unchecked")
    public void mock_before_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxBefore(BeforeParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).currentEngine();
        verify(ENGINE).rxBefore(any());
    }
    //endregion

    //region AfterClass
    @Test
    @SuppressWarnings("unchecked")
    public void mock_afterClass_shouldSucceed() {
        // Setup
        doReturn(0).when(TEST_KIT).currentIndex();
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxAfterClass(AfterClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).currentEngine();
        verify(ENGINE).rxAfterClass(any());
    }
    //endregion

    //region After
    @Test
    @SuppressWarnings("unchecked")
    public void mock_after_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        TEST_KIT.rxAfter(AfterParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
        verify(TEST_KIT).currentEngine();
        verify(ENGINE).rxAfter(any());
    }
    //endregion
}
