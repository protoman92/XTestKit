package com.swiften.xtestkit.rx;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/26/17.
 */

/**
 * Convenience class to use with
 * {@link io.reactivex.Flowable#zip(Iterable, Function)} and
 * {@link io.reactivex.Flowable#zipWith(Iterable, BiFunction)}.
 * @param <T> Non-bound generics.
 */
public final class Index<T> {
    @NotNull public final T OBJECT;
    public final int INDEX;

    public Index(@NotNull T object, int index) {
        OBJECT = object;
        INDEX = index;
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("%s, index: %d", OBJECT, INDEX);
    }
}
