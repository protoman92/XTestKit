package org.swiften.xtestkit.navigation;

/**
 * Created by haipham on 5/20/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.collection.HPIterables;
import org.swiften.javautilities.util.HPLog;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.util.EngineProviderType;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This interface provides methods to manager {@link ScreenType} and calculate
 * the short routes between any 2 {@link ScreenType}.
 */
public interface ScreenManagerType extends EngineProviderType, ScreenManagerErrorType {
    /**
     * Register {@link ScreenType} and store its related {@link Node} in an
     * inner cache.
     * @param SCREEN {@link ScreenType} instance.
     * @see #engine()
     * @see ScreenType#forwardAccessible(Engine)
     * @see ScreenType#backwardAccessible(Engine)
     * @see #addForwardNodes(List)
     * @see #addBackwardNodes(List)
     */
    default void register(@NotNull final ScreenType SCREEN) {
        Engine<?> engine = engine();

        addForwardNodes(SCREEN.forwardAccessible(engine)
            .stream().map(a -> new Node(SCREEN, a))
            .collect(Collectors.toCollection(LinkedList::new)));

        addBackwardNodes(SCREEN.backwardAccessible(engine)
            .stream().map(a -> new Node(SCREEN, a))
            .collect(Collectors.toCollection(LinkedList::new)));
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
     * Clear all {@link Node} from the inner caches.
     */
    void clearAllNodes();

    /**
     * Add forward {@link Node} to an inner cache.
     * @param nodes {@link List} of {@link Node}.
     */
    void addForwardNodes(@NotNull List<Node> nodes);

    /**
     * Same as above, but uses a varargs of {@link Node}.
     * @param nodes Varargs {@link Node}.
     * @see #addForwardNodes(List)
     */
    default void addForwardNodes(@NotNull Node...nodes) {
        addForwardNodes(HPIterables.asList(nodes));
    }

    /**
     * Add backward {@link Node} to an inner cache.
     * @param nodes {@link List} of {@link Node}.
     */
    void addBackwardNodes(@NotNull List<Node> nodes);

    /**
     * Same as above, but uses a varargs of {@link Node}.
     * @param nodes Varargs {@link Node}.
     * @see #addBackwardNodes(List)
     */
    default void addBackwardNodes(@NotNull Node...nodes) {
        addBackwardNodes(HPIterables.asList(nodes));
    }

    /**
     * Return the inner forward {@link Node} cache.
     * @return {@link List} of {@link Node}.
     */
    @NotNull
    List<Node> registeredForwardNodes();

    /**
     * Return the inner backward {@link Node} cache.
     * @return {@link List} of {@link Node}.
     */
    @NotNull
    List<Node> registeredBackwardNodes();

    /**
     * Get {@link List} of forward {@link Node} whose {@link Node#S1}
     * equals to {@link ScreenType} instance.
     * @param SCREEN {@link ScreenType} instance.
     * @return {@link List} of {@link Node}.
     * @see #registeredForwardNodes()
     * @see Node#S1
     */
    @NotNull
    default List<Node> forwardNodes(@NotNull final ScreenType SCREEN) {
        return registeredForwardNodes().stream()
            .filter(a -> a.S1.equals(SCREEN))
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get {@link List} of backward {@link Node} whose {@link Node#S1}
     * equals to {@link ScreenType} instance.
     * @param SCREEN {@link ScreenType} instance.
     * @return {@link List} of {@link Node}.
     * @see #registeredBackwardNodes()
     * @see Node#S1
     */
    default List<Node> backwardNodes(@NotNull final ScreenType SCREEN) {
        return registeredBackwardNodes().stream()
            .filter(a -> a.S1.equals(SCREEN))
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get the {@link Node} that connects one {@link ScreenType}
     * and another.
     * @param FROM The origin {@link ScreenType} instance.
     * @param TO The destination {@link ScreenType} instance.
     * @return {@link Optional} instance.
     * @see #registeredForwardNodes()
     * @see Node#connects(ScreenType, ScreenType)
     */
    @NotNull
    default Optional<Node> node(@NotNull final ScreenType FROM,
                                @NotNull final ScreenType TO) {
        return registeredForwardNodes().stream()
            .filter(Node::isNotDummy)
            .filter(a -> a.connects(FROM, TO))
            .findFirst();
    }

    /**
     * Get the shortest route to a destination {@link ScreenType}, out of
     * a {@link List} of {@link Node}.
     * @param origin The {@link ScreenType} marked as the point of origin.
     *               Usually this will be the same throughout all recursive
     *               calls, unless it is reset by a backtrack.
     * @param dest Destination {@link ScreenType} instance.
     * @param originNodes {@link List} of {@link Node}.
     * @return {@link List} of {@link Node}.
     * @see #node(ScreenType, ScreenType)
     */
    @NotNull
    default List<Node> shortest(@NotNull ScreenType origin,
                                @NotNull ScreenType dest,
                                @NotNull List<Node> originNodes) {
        List<Node> navigations = Collections.emptyList();

        int minimum = 0;

        for (Node node : originNodes) {
            List<Node> nav1 = new LinkedList<>(shortest(origin, dest, node.S2));
            int size = nav1.size();

            if ((minimum == 0 || size < minimum) && size > 0) {
                /* If nav1 is empty (not including the originating node),
                 * that means we cannot access the second screen from the
                 * first screen */
                minimum = nav1.size();
                navigations = nav1;
            }

            /* We need to add the originating node to complete the chain */
            nav1.add(0, node);
        }

        return navigations;
    }

    /**
     * Get the shortest route from one {@link ScreenType} to another.
     * @param ORIGIN The {@link ScreenType} marked as the point of origin.
     *               Usually this will be the same throughout all recursive
     *               calls, unless it is reset by a backtrack.
     * @param dest The final destination {@link ScreenType} instance.
     * @param current The current {@link ScreenType} instance, an intermediary
     *                point between origin and destination.
     * @return {@link List} of {@link Node}.
     * @see #node(ScreenType, ScreenType)
     * @see #forwardNodes(ScreenType)
     */
    @NotNull
    default List<Node> shortest(@NotNull final ScreenType ORIGIN,
                                @NotNull ScreenType dest,
                                @NotNull ScreenType current) {
        Optional<Node> nodeOps = node(current, dest);

        if (nodeOps.isPresent()) {
            return Collections.singletonList(nodeOps.get());
        } else if (!current.equals(dest)) {
            /* We need to filter out nodes that lead to origin, or else a
             * StackOverflow may happen if we backtrack even once */
            List<Node> forwardNodes = forwardNodes(current)
                .stream().filter(a -> !a.S2.equals(ORIGIN))
                .collect(Collectors.toCollection(LinkedList::new));

            if (forwardNodes.isEmpty()) {
                List<Node> backwardNodes = backwardNodes(current);

                /* When we backtrack even once, set the origin node to current
                 * in order to avoid StackOverflow */
                return shortest(current, dest, backwardNodes);
            } else {
                return shortest(ORIGIN, dest, forwardNodes);
            }
        } else {
            /* If the current screen is the same as the destination screen,
             * return a dummy node that does no navigation (but is still
             * valid) */
            return Collections.singletonList(Node.dummy(current));
        }
    }

    /**
     * Get {@link List} of {@link Node} to get from one
     * {@link ScreenType} to another. If {@link ScreenType} is not directly
     * accessible from a previous instance, attempt to calculate the shortest
     * route.
     * @param screens A varargs of {@link ScreenType}.
     * @return {@link List} of {@link Node}.
     * @see #engine()
     * @see #shortest(ScreenType, ScreenType, List)
     */
    @NotNull
    default List<Node> multiNodes(@NotNull ScreenType...screens) {
        List<Node> nav = new LinkedList<>();

        for (int i = 0, length = screens.length; i < length; i++) {
            if (i < length - 1) {
                ScreenType from = screens[i];
                ScreenType to = screens[i + 1];
                List<Node> nodes = shortest(from, to, from);

                /* If nodes is empty, that means the chain is cut off. We
                 * cannot navigate from start to finish */
                if (nodes.isEmpty()) break; else nav.addAll(nodes);
            }
        }

        return nav;
    }

    /**
     * Navigate sequentially from the first {@link Node} to the last.
     * @param initial The initial argument to pass to
     *                {@link NavigationSupplier#navigation(Object)}
     * @param screens A varargs of {@link ScreenType}.
     * @return {@link Flowable} instance.
     * @see NavigationSupplier#navigation(Object)
     * @see ScreenType.Direction#NAVIGATION
     * @see ScreenType#rxa_onInitialized()
     * @see #multiNodes(ScreenType...)
     */
    @NotNull
    default Flowable<?> rxa_navigate(@NotNull Object initial,
                                     @NotNull ScreenType...screens) {
        HPLog.printft("Navigating %s", Arrays.toString(screens));
        final List<Node> NODES = multiNodes(screens);
        final int LENGTH = NODES.size();

        class Repeater {
            @NotNull
            private Flowable<?> repeat(@NotNull Object init, final int INDEX) {
                if (INDEX < LENGTH) {
                    final Node NODE = NODES.get(INDEX);

                    return NODE.NAV_SUPPLIER.navigation(init)
                        .delay(NODE.DELAY, NODE.TIME_UNIT)
                        .flatMap(NODE.S2.rxa_onInitialized()::navigation)
                        .flatMap(a -> new Repeater().repeat(a, INDEX + 1));
                } else {
                    return Flowable.just(init);
                }
            }
        }

        return new Repeater().repeat(initial, 0);
    }

    /**
     * Each {@link Node} represents a navigation from one {@link ScreenType}
     * to another.
     */
    @SuppressWarnings("WeakerAccess")
    final class Node {
        /**
         * Get a {@link Node} that does no navigation, i.e. {@link #S1} and
         * {@link #S2} are the same {@link ScreenType}.
         * @param screen {@link ScreenType} instance.
         * @return {@link Node} instance.
         */
        @NotNull
        static Node dummy(@NotNull ScreenType screen) {
            NavigationSupplier navigation = a -> Flowable.just(true);
            TimeUnit unit = TimeUnit.MILLISECONDS;
            return new Node(screen, screen, navigation, unit, 0);
        }

        @NotNull public final ScreenType S1;
        @NotNull public final ScreenType S2;
        @NotNull public final NavigationSupplier NAV_SUPPLIER;
        @NotNull public final TimeUnit TIME_UNIT;
        final long DELAY;

        Node(@NotNull ScreenType firstScreen,
             @NotNull ScreenType secondScreen,
             @NotNull NavigationSupplier navigation,
             @NotNull TimeUnit timeUnit,
             long delay) {
            S1 = firstScreen;
            S2 = secondScreen;
            NAV_SUPPLIER = navigation;
            DELAY = delay;
            TIME_UNIT = timeUnit;
        }

        Node(@NotNull ScreenType firstScreen,
             @NotNull ScreenType.Direction direction) {
            this(
                firstScreen,
                direction.TARGET,
                direction.NAVIGATION,
                direction.TIME_UNIT,
                direction.DELAY
            );
        }

        @NotNull
        @Override
        public String toString() {
            return String.format("%s to %s", S1, S2);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (o instanceof Node) {
                Node node = (Node)o;
                return node.S1.equals(S1) && node.S2.equals(S2);
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
         * @see #S1
         * @see #S2
         */
        boolean connects(@NotNull ScreenType from, @NotNull ScreenType to) {
            return S1.equals(from) && S2.equals(to);
        }

        /**
         * Check if the current {@link Node} is a dummy.
         * @return {@link Boolean} value.
         * @see #S1
         * @see #S2
         */
        boolean isDummy() {
            return S1.equals(S2);
        }

        /**
         * Check if the current {@link Node} is not a dummy.
         * @return {@link Boolean} value.
         * @see #isDummy()
         */
        boolean isNotDummy() {
            return !isDummy();
        }
    }
}
