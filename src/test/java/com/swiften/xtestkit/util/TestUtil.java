package com.swiften.xtestkit.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by haipham on 3/24/17.
 */
public class TestUtil {
    @NotNull
    private static Random RAND = new Random();

    /**
     * Produce a random number between two bounds.
     * @param from The inclusive lower bound.
     * @param to The non-inclusive upper bound.
     * @return An {@link Integer} value.
     */
    public static int randomBetween(int from, int to) {
        return RAND.nextInt(to - from) + from;
    }

    /**
     * Produce a random {@link T} from an Array of {@link T}.
     * @param elements The Array of {@link T} from which the element will be
     *                 produced.
     * @param <T> Generics.
     * @return A {@link T} element.
     * @throws RuntimeException If the produced element is null, or the
     * Array is empty.
     */
    @NotNull
    public static <T> T randomElement(@NotNull T[] elements) {
        return randomElement(Arrays.asList(elements));
    }

    /**
     * Produce a random {@link T} from a {@link List} of {@link T}.
     * @param elements The {@link List} of {@link T} from which the element
     *                 will be produce.
     * @param <T> Generics.
     * @return A {@link T} element.
     * @throws RuntimeException If the produced element is null, or the
     * {@link List} is empty.
     */
    @NotNull
    public static <T> T randomElement(@NotNull List<T> elements) {
        if (elements.isEmpty()) {
            throw new RuntimeException("List/Array cannot be empty");
        }

        T element = elements.get(randomBetween(0, elements.size()));

        if (element != null) {
            return element;
        } else {
            throw new RuntimeException("Element cannot be null");
        }
    }
}
