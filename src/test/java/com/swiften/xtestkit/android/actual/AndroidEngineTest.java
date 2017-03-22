package com.swiften.xtestkit.android.actual;

import com.swiften.engine.mobile.android.AndroidEngine;
import com.swiften.util.Log;
import com.swiften.util.ProcessRunner;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.spy;

/**
 * Created by haipham on 3/23/17.
 */
public class AndroidEngineTest {
    @NotNull private final AndroidEngine ENGINE;

    {
        ENGINE = spy(AndroidEngine.newBuilder()
            .withDeviceName("Nexus_4_API_23")
            .build());
    }

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    @SuppressWarnings("unchecked")
    public void actual_startAndStopEmulator_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxStartEmulator()
            .delay(2000, TimeUnit.MILLISECONDS)
            .flatMap(a -> ENGINE.rxStopEmulator())

            /* By the time rxCheckEmulatorOpen is called, it would emit false,
             * so we need to check the test subscriber for a false value */
            .flatMap(a -> ENGINE.rxCheckEmulatorOpen())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        Assert.assertFalse((boolean)((List)subscriber.getEvents().get(0)).get(0));
    }
}
