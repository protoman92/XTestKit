package com.swiften.xtestkit.engine.android.actual;

import com.swiften.engine.mobile.android.AndroidEngine;
import com.swiften.engine.mobile.android.protocol.AndroidDelay;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;

/**
 * Created by haipham on 3/23/17.
 */
public class AndroidEngineTest implements AndroidDelay {
    @NotNull private final AndroidEngine ENGINE;

    {
        ENGINE = spy(AndroidEngine.newBuilder()
            .withDeviceName("Nexus_4_API_23")
            .build());
    }

    @Before
    @SuppressWarnings("unchecked")
    public void before() {
        TestSubscriber subscriber = TestSubscriber.create();

        ENGINE.rxStartEmulator()
            .delay(emulatorBootFinishDelay(), TimeUnit.MILLISECONDS)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();
    }

    @After
    @SuppressWarnings("unchecked")
    public void after() {
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
        Assert.assertTrue(TestUtil.getFirstNextEvent(subscriber));
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
        Assert.assertTrue(TestUtil.getFirstNextEvent(subscriber));
    }
}
