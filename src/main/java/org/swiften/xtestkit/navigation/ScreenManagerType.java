package org.swiften.xtestkit.navigation;

/**
 * Created by haipham on 5/20/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.rx.RxUtil;
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
     * Register {@link ScreenType} and store its related {@link Node}
     * in an inner cache.
     * @param screen {@link ScreenType} instance.
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
     * Register {@link Collection} of {@link ScreenType}.
     * @param screens {@link Collection} of {@link ScreenType}.
     * @see #register(ScreenType)
     */
    default void register(@NotNull Collection<? extends ScreenType> screens) {
        final ScreenManagerType THIS = this;
        screens.forEach(THIS::register);
    }

    /**
     * Add {@link Node} to an inner cache.
     * @param nodes {@link List} of {@link Node}.
     */
    void addNodes(@NotNull List<Node> nodes);

    /**
     * Check whether {@link Node} has been added to an inner cache.
     * @param node {@link Node} instance.
     * @return {@link Boolean} value.
     * @see #register(ScreenType)
     */
    default boolean hasAddedNode(@NotNull Node node) {
        return registeredNodes().contains(node);
    }

    /**
     * Return the inner {@link Node} cache.
     * @return {@link List} of {@link Node}.
     */
    @NotNull
    List<Node> registeredNodes();

    /**
     * Get {@link List} of {@link Node} whose {@link Node#FIRST}
     * equals to {@link ScreenType} instance.
     * @param SCREEN {@link ScreenType} instance.
     * @return {@link List} of {@link Node}.
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
     * Get {@link List} of {@link Node} whose {@link Node#SECOND}
     * equals to {@link ScreenType} instance.
     * @param SCREEN {@link ScreenType} instance.
     * @return {@link List} of {@link Node}.
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
     * @return {@link Optional} instance.
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
     * @return {@link List} of {@link Node}.
     * @see #node(ScreenType, ScreenType)
     * @see #fromNodes(ScreenType)
     */
    @NotNull
    default List<Node> nodes(@NotNull ScreenType from,
                             @NotNull ScreenType to) {
        Optional<Node> nodeOps = node(from, to);
        List<Node> navigations = Collections.emptyList();

        if (nodeOps.isPresent()) {
            return Collections.singletonList(nodeOps.get());
        } else if (!from.equals(to)) {
            List<Node> nodes = fromNodes(from);
            int nodeCount = 0;

            for (Node node : nodes) {
                ScreenType screen1 = node.SECOND;
                List<Node> nav1 = new LinkedList<>(nodes(screen1, to));
                nav1.add(0, node);

                /* Including the originating node, if the node list has only
                 * 2 elements, it should be the shortest since we have
                 * already ruled out the 1-element case */
                if (nav1.size() == 2) {
                    navigations = nav1;
                    break;
                } else if (nav1.size() > 1) {
                    /* If nav1 is empty (not including the originating node),
                     * that means we cannot access the second screen from the
                     * first screen */

                    if (nodeCount == 0 || nav1.size() < nodeCount) {
                        nodeCount = nav1.size();
                        navigations = nav1;
                    }
                }
            }
        }

        return navigations;
    }

    /**
     * Get {@link List} of {@link Node} to get from one
     * {@link ScreenType} to another. If {@link ScreenType} is not directly
     * accessible from a previous instance, attempt to calculate the shortest
     * route.
     * @param screens A varargs of {@link ScreenType}.
     * @return {@link List} of {@link Node}.
     * @see #engine()
     * @see #nodes(ScreenType, ScreenType)
     */
    @NotNull
    default List<Node> nodes(@NotNull ScreenType...screens) {
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
     * @return {@link Flowable} instance.
     * @see #nodes(ScreenType...)
     * @see #notReachable(ScreenType...)
     * @see ScreenType.Direction#NAVIGATION
     * @see ScreenType.Navigation#navigator(Object)
     * @see RxUtil#error(String)
     */
    @NotNull
    default Flowable<?> rx_navigate(@NotNull Object initial,
                                    @NotNull ScreenType...screens) {
        final List<Node> NODES = nodes(screens);

        if (NODES.isEmpty()) {
            return RxUtil.error(notReachable(screens));
        } else {
            final int LENGTH = NODES.size();

            class Repeater {
                @NotNull
                private Flowable<?> repeat(@NotNull Object init, final int INDEX) {
                    if (INDEX < LENGTH) {
                        return NODES.get(INDEX)
                            .DIRECTION.NAVIGATION.navigator(init)
                            .flatMap(a -> new Repeater().repeat(a, INDEX + 1));
                    } else {
                        return Flowable.just(init);
                    }
                }
            }

            return new Repeater().repeat(initial, 0);
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
            return String.format("%s to %s", FIRST, SECOND);
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
         * @return {@link Boolean} value.
         * @see #FIRST
         * @see #SECOND
         */
        boolean connects(@NotNull ScreenType from, @NotNull ScreenType to) {
            return FIRST.equals(from) && SECOND.equals(to);
        }
    }
}
