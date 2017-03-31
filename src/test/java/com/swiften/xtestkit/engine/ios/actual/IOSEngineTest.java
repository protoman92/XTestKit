package com.swiften.xtestkit.engine.ios.actual;

import com.swiften.xtestkit.engine.mobile.ios.IOSEngine;
import com.swiften.xtestkit.engine.mobile.ios.protocol.IOSDelayProtocol;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;

/**
 * Created by haipham on 3/31/17.
 */
public class IOSEngineTest implements IOSDelayProtocol {
    @NotNull private final IOSEngine ENGINE;

    {
        ENGINE = IOSEngine.newBuilder()
            .withDeviceUID("CF6E7ACD-F818-4145-A140-75CF1F229A8C")
            .withDeviceName("iPhone 7 Plus")
            .build();
    }

    @Before
    @SuppressWarnings("unchecked")
    public void before() {
        TestSubscriber subscriber = TestSubscriber.create();
        ENGINE.rxStartSimulator().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
    }

    @After
    @SuppressWarnings("unchecked")
    public void after() {
        TestSubscriber subscriber = TestSubscriber.create();
        ENGINE.rxStopSimulator().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
    }
}
