package com.swiften.xtestkit.engine.ios.actual;

import com.swiften.xtestkit.engine.mobile.ios.IOSEngine;
import com.swiften.xtestkit.engine.mobile.ios.protocol.IOSDelayProtocol;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Created by haipham on 3/31/17.
 */
public class IOSEngineTest implements IOSDelayProtocol {
    @NotNull private final IOSEngine ENGINE;

    {
        ENGINE = IOSEngine.builder()
            .withDeviceUID("CF6E7ACD-F818-4145-A140-75CF1F229A8C")
            .withDeviceName("iPhone 7 Plus")
            .build();
    }

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void beforeMethod() {
        TestSubscriber subscriber = CustomTestSubscriber.create();
        ENGINE.rxStartSimulator().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
    }

    @AfterMethod
    @SuppressWarnings("unchecked")
    public void afterMethod() {
        TestSubscriber subscriber = CustomTestSubscriber.create();
        ENGINE.rxStopSimulator().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
    }
}
