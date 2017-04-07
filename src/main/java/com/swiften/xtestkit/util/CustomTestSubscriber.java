package com.swiften.xtestkit.util;

import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/8/17.
 */

/**
 * Use this class to implement hooks that intercept emissions.
 * @param <T> Generics parameter.
 */
public class CustomTestSubscriber<T> extends TestSubscriber<T> {
    @NotNull
    public static <T> CustomTestSubscriber<T> create() {
        return new CustomTestSubscriber<>();
    }

    private CustomTestSubscriber() {
        super();
    }

    @Override
    public void onError(@NotNull Throwable t) {
        super.onError(t);
        Log.printf("Error encountered: %s", t.getMessage());
    }
}
