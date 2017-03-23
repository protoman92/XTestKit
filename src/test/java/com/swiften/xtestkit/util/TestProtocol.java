package com.swiften.xtestkit.util;

import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by haipham on 3/23/17.
 */
public interface TestProtocol {
    @NotNull
    @SuppressWarnings("unchecked")
    default <T> List<T> getNextEvents(@NotNull List<Object> events) {
        return (List)events.get(0);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    default <T> T getFirstNextEvent(@NotNull List<Object> events) {
        return (T)getNextEvents(events).get(0);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    default <T> T getFirstNextEvent(@NotNull TestSubscriber subscriber) {
        return (T)this.<T>getFirstNextEvent(subscriber.getEvents());
    }
}
