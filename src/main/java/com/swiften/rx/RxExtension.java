package com.swiften.rx;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by haipham on 3/26/17.
 */
public class RxExtension {
    /**
     * Create a {@link Flowable} from a {@link Collection} of {@link T}. This
     * {@link Flowable} emits {@link Index}, allowing us to access to original
     * {@link T} object, as well as its index in the {@link Collection}.
     * @param collection The {@link Collection} from which {@link Flowable}
     *                   will be constructed.
     * @param <T> Non-bound generics.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public static <T> Flowable<Index<T>> fromCollection(@NotNull Collection<T> collection) {
        return Flowable.zip(
            Flowable.fromIterable(collection),
            Flowable.range(0, collection.size()),
            Index::new
        );
    }
}
