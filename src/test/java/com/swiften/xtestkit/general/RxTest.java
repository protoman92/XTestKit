package com.swiften.xtestkit.general;

import com.swiften.xtestkit.util.BooleanUtil;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import com.swiften.xtestkit.util.LogUtil;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.testng.annotations.Test;

/**
 * Created by haipham on 3/25/17.
 */
public class RxTest {
    @Test
    @SuppressWarnings("unchecked")
    public void mock_concat() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable
            .concat(
                Flowable.just(true),
                Flowable.just(true),
                Flowable.just(true)
            )
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .doOnNext(LogUtil::println)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(subscriber.getEvents());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_toList() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable
            .concat(
                Flowable.just(true),
                Flowable.just(true),
                Flowable.just(true)
            )
            .doOnNext(LogUtil::println)
            .toList()
            .toFlowable()
            .doOnNext(LogUtil::println)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(subscriber.getEvents());
    }
}
