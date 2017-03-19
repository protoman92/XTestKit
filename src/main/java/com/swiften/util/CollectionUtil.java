package com.swiften.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by haipham on 3/20/17.
 */
public class CollectionUtil {
    /**
     * Concatenate all {@link T} with generics {@link E} into one {@link T}.
     * The first {@link T} in the varargs will be appended to and changed.
     * @param cls A varargs of {@link T}.
     * @param <E> {@link T}'s generics.
     * @param <T> Generics that extends {@link Collection<E>}
     * @return A unified {@link T} instance.
     * @throws Exception Throws an {@link Exception} if the varargs is
     * empty. We can simply throw a {@link RuntimeException} here.
     */
    @SafeVarargs
    public static <E,T extends Collection<E>> T unify(@NotNull T...cls)
        throws Exception
    {
        int length = cls.length;

        if (length > 0 && Objects.nonNull(cls[0])) {
            T first = cls[0];

            for (int i = 1; i < length; i++) {
                if (i < length) {
                    if (Objects.nonNull(cls[i])) {
                        first.addAll(cls[i]);
                    }
                } else {
                    break;
                }
            }

            return first;
        }

        throw new RuntimeException("Varargs cannot be empty");
    }
}
