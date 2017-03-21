package com.swiften.xtestkit.engine;

import com.swiften.engine.mobile.android.AndroidEngine;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

/**
 * Created by haipham on 3/22/17.
 */
public final class AndroidEngineTest {
    @NotNull private final AndroidEngine ENGINE;

    {
        ENGINE = AndroidEngine.newBuilder()
            .withDeviceName("Nexus_4_API_23")
            .build();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_startDevice_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        ENGINE.rxStartApp().subscribe(subscriber);

        // Then
    }
}
