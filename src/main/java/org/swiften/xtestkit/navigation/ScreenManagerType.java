package org.swiften.xtestkit.navigation;

/**
 * Created by haipham on 5/20/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.xtestkit.base.Engine;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This interface provides methods to manager {@link ScreenType} and calculate
 * the short routes between any 2 {@link ScreenType}.
 */
public interface ScreenManagerType extends ScreenManagerErrorType {
    /**
     * Get the associated {@link Engine} instance.
     * @return {@link Engine} instance.
     */
    @NotNull
    Engine<?> engine();

    /**
     * Register a {@link ScreenType} and store its related {@link Node}
     * in an inner cache.
     * @param screen A {@link ScreenType} instance.
     * @see #engine()
     * @see ScreenType#accessibleFromHere(Engine)
     */
    default void register(@NotNull ScreenType screen) {
        Engine<?> engine = engine();

        List<Node> nodes = screen
            .accessibleFromHere(engine)
            .stream()
            .map(a -> new Node(screen, a))
            .collect(Collectors.toList());

        addNodes(nodes);
    }

    /**
     * Register a varargs of {@link ScreenType}.
     * @param screens A varargs of {@link ScreenType}.
     * @see #register(Collection)
     */
    default void register(@NotNull ScreenType...screens) {
        register(Arrays.asList(screens));
    }

    /**
     * Register a {@link Collection} of {@link ScreenType}.
     * @param screens A {@link Collection} of {@link ScreenType}.
     * @see #register(ScreenType)
     */
    default void register(@NotNull Collection<? extends ScreenType> screens) {
        final ScreenManagerType THIS = this;
        screens.forEach(THIS::register);
    }

    /**
     * Add a {@link Node} to an inner cache.
     * @param node A {@link List} of {@link Node}.
     */
    void addNodes(@NotNull List<Node> node);

    /**
     * Check whether a {@link Node} has been added to an inner cache.
     * @param node A {@link Node} instance.
     * @return A {@link Boolean} value.
     * @see #register(ScreenType)
     */
    default boolean hasAddedNode(@NotNull Node node) {
        return registeredNodes().contains(node);
    }

    /**
     * Return the inner {@link Node} cache.
     * @return A {@link List} of {@link Node}.
     */
    @NotNull
    List<Node> registeredNodes();

    /**
     * Get a {@link List} of {@link Node} whose {@link Node#FIRST}
     * equals to a {@link ScreenType} instance.
     * @param SCREEN A {@link ScreenType} instance.
     * @return A {@link List} of {@link Node}.
     * @see #registeredNodes()
     * @see Node#FIRST
     */
    @NotNull
    default List<Node> fromNodes(@NotNull final ScreenType SCREEN) {
        return registeredNodes().stream()
            .filter(a -> a.FIRST.equals(SCREEN))
            .collect(Collectors.toList());
    }

    /**
     * Get a {@link List} of {@link Node} whose {@link Node#SECOND}
     * equals to a {@link ScreenType} instance.
     * @param SCREEN A {@link ScreenType} instance.
     * @return A {@link List} of {@link Node}.
     * @see #registeredNodes()
     * @see Node#SECOND
     */
    default List<Node> toNodes(@NotNull final ScreenType SCREEN) {
        return registeredNodes().stream()
            .filter(a -> a.SECOND.equals(SCREEN))
            .collect(Collectors.toList());
    }

    /**
     * Get the {@link Node} that connects one {@link ScreenType}
     * and another.
     * @param FROM The origin {@link ScreenType} instance.
     * @param TO The destination {@link ScreenType} instance.
     * @return A {@link Optional} instance.
     * @see #registeredNodes()
     * @see Node#connects(ScreenType, ScreenType)
     */
    @NotNull
    default Optional<Node> node(@NotNull final ScreenType FROM,
                                @NotNull final ScreenType TO) {
        return registeredNodes().stream()
            .filter(a -> a.connects(FROM, TO))
            .findFirst();
    }

    /**
     * Get the shortest route from one {@link ScreenType} to another.
     * @param from The origin {@link ScreenType} instance.
     * @param to The destination {@link ScreenType} instance.
     * @return A {@link List} of {@link Node}.
     * @throws Exception If the two {@link ScreenType} cannot be connected.
     * @see #node(ScreenType, ScreenType)
     * @see #fromNodes(ScreenType)
     * @see #NOT_REACHABLE
     */
    @NotNull
    default List<Node> nodes(@NotNull ScreenType from,
                             @NotNull ScreenType to) throws Exception {
        Optional<Node> nodeOps = node(from, to);

        if (nodeOps.isPresent()) {
            return Collections.singletonList(nodeOps.get());
        } else if (!from.equals(to)) {
            List<Node> navigations = Collections.emptyList();
            List<Node> nodes = fromNodes(from);
            int nodeCount = 0;

            for (Node node : nodes) {
                ScreenType screen1 = node.SECOND;
                List<Node> nav1 = new LinkedList<>(nodes(screen1, to));

                /* If nav1 is empty, that means we cannot access the second
                 * screen from the first screen */
                if (!nav1.isEmpty()) {
                    /* Add the current node to complete the chain */
                    nav1.add(0, node);

                    if (nodeCount == 0 || nav1.size() < nodeCount) {
                        nodeCount = nav1.size();
                        navigations = nav1;
                    }
                }
            }

            if (!navigations.isEmpty()) {
                return navigations;
            }
        }

        throw new RuntimeException(NOT_REACHABLE);
    }

    /**
     * Get a {@link List} of {@link Node} to get from one
     * {@link ScreenType} to another. If a {@link ScreenType} is not directly
     * accessible from a previous instance, attempt to calculate the shortest
     * route.
     * @param screens A varargs of {@link ScreenType}.
     * @return A {@link List} of {@link Node}.
     * @throws Exception If the two {@link ScreenType} cannot be connected.
     * @see #engine()
     * @see #nodes(ScreenType, ScreenType)
     */
    @NotNull
    default List<Node> nodes(@NotNull ScreenType...screens) throws Exception {
        List<Node> navigations = new LinkedList<>();

        for (int i = 0, length = screens.length; i < length; i++) {
            if (i < length - 1) {
                ScreenType from = screens[i];
                ScreenType to = screens[i + 1];
                navigations.addAll(nodes(from, to));
            }
        }

        return navigations;
    }

    /**
     * Navigate sequentially from the first {@link Node} to the last.
     * @param initial The initial argument to pass to
     *                {@link ScreenType.Navigation#navigator(Object)}
     * @param screens A varargs of {@link ScreenType}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<?> rx_navigate(@NotNull Object initial,
                                    @NotNull ScreenType...screens) {
        try {
            final List<Node> NODES = nodes(screens);
            final int LENGTH = NODES.size();

            class Repeater {
                @NotNull
                private Flowable<?> repeat(@NotNull Object previous,
                                           final int INDEX) {
                    if (INDEX < LENGTH) {
                        return NODES.get(INDEX)
                            .DIRECTION.NAVIGATION.navigator(previous)
                            .flatMap(a -> new Repeater().repeat(a, INDEX + 1));
                    } else {
                        return Flowable.just(previous);
                    }
                }
            }

            return new Repeater().repeat(initial, 0);
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    /**
     * Each {@link Node} represents a navigation from one {@link ScreenType}
     * to another.
     */
    final class Node {
        @NotNull final ScreenType FIRST;
        @NotNull final ScreenType SECOND;
        @NotNull final ScreenType.Navigation NAVIGATION;
        @NotNull final ScreenType.Direction DIRECTION;

        Node(@NotNull ScreenType firstScreen,
             @NotNull ScreenType.Direction direction) {
            FIRST = firstScreen;
            SECOND = direction.TARGET;
            NAVIGATION = direction.NAVIGATION;
            DIRECTION = direction;
        }

        @NotNull
        @Override
        public String toString() {
            return String.format("From %s, to %s", FIRST, SECOND);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (o instanceof Node) {
                Node node = (Node)o;
                return node.FIRST.equals(FIRST) && node.SECOND.equals(SECOND);
            } else {
                return false;
            }
        }

        /**
         * Check if the current {@link Node} connects two
         * {@link ScreenType}.
         * @param from The origin {@link ScreenType} instance.
         * @param to The destination {@link ScreenType} instance.
         * @return A {@link Boolean} value.
         * @see #FIRST
         * @see #SECOND
         */
        boolean connects(@NotNull ScreenType from, @NotNull ScreenType to) {
            return FIRST.equals(from) && SECOND.equals(to);
        }
    }
}
