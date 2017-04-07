package com.swiften.xtestkit.general;

import com.swiften.xtestkit.util.CustomTestSubscriber;
import com.swiften.xtestkit.util.Log;
import io.reactivex.Completable;
import io.reactivex.subscribers.TestSubscriber;
import org.testng.annotations.Test;

/**
 * Created by haipham on 3/25/17.
 */
public class RxTest {
    @Test
    @SuppressWarnings("unchecked")
    public void mock_completable() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Completable
            .fromAction(() -> Log.println("First Step"))
            .toFlowable()
            .defaultIfEmpty(true)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        Log.println(subscriber.getEvents());
    }
}
