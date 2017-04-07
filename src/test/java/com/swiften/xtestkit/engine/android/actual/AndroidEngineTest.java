package com.swiften.xtestkit.engine.android.actual;

import com.swiften.xtestkit.engine.mobile.android.AndroidEngine;
import com.swiften.xtestkit.engine.mobile.android.protocol.AndroidDelayProtocol;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.testng.Assert.*;
import org.testng.annotations.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;

/**
 * Created by haipham on 3/23/17.
 */
public class AndroidEngineTest implements AndroidDelayProtocol {
    @NotNull private static final AndroidEngine ENGINE;

    static {
        ENGINE = spy(AndroidEngine.builder()
            .withDeviceName("Nexus_4_API_23")
            .build());
    }

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void beforeClass() {
        TestSubscriber subscriber = TestSubscriber.create();
        ENGINE.rxStartEmulator().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
    }

    @AfterClass
    @SuppressWarnings("unchecked")
    public void afterClass() {
        TestSubscriber subscriber = TestSubscriber.create();
        ENGINE.rxStopEmulator().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        reset(ENGINE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_enableDisableConnection_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxDisableInternetConnection()
            .filter(success -> success)
            .flatMap(a -> ENGINE.rxEnableInternetConnection())
            .filter(success -> success)
            .switchIfEmpty(Flowable.error(new Exception()))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_checkKeyboardOpen_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxCheckKeyboardOpen().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_disableEmulatorAnimations_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxDisableEmulatorAnimations()
            .filter(success -> success)
            .switchIfEmpty(Flowable.error(new Exception()))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
    }
}
