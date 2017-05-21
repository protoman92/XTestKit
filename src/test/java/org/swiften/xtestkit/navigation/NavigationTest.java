package org.swiften.xtestkit.navigation;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import static org.mockito.Mockito.*;

import org.swiften.javautilities.collection.CollectionUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.Engine;
import static org.testng.Assert.*;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by haipham on 5/20/17.
 */
public class NavigationTest implements ScreenManagerType {
    @NotNull private final ScreenManagerType MANAGER;
    @NotNull private final List<Node> FORWARD_NODES;
    @NotNull private final List<Node> BACKWARD_NODES;
    @NotNull private final Engine<?> ENGINE;
    private final int TRIES = 100;

    {
        FORWARD_NODES = new LinkedList<>();
        BACKWARD_NODES = new LinkedList<>();
        ENGINE = mock(Engine.class);
        MANAGER = spy(this);
    }

    @NotNull
    @DataProvider
    public Object[][] dataProvider() {
        return IntStream.range(0, TRIES)
            .boxed()
            .map(a -> new Object[0])
            .toArray(Object[][]::new);
    }

    @BeforeMethod
    public void beforeMethod() {
        MANAGER.register(Screen.values());
    }

    @AfterMethod
    public void afterMethod() {
        FORWARD_NODES.clear();
    }

    @NotNull
    @Override
    public Engine<?> engine() {
        return ENGINE;
    }

    @Override
    public void addForwardNodes(@NotNull List<Node> nodes) {
        FORWARD_NODES.addAll(nodes);
    }

    @Override
    public void addBackwardNodes(@NotNull List<Node> nodes) {
        BACKWARD_NODES.addAll(nodes);
    }

    @NotNull
    public List<Node> registeredForwardNodes() {
        return FORWARD_NODES;
    }

    @NotNull
    public List<Node> registeredBackwardNodes() {
        return BACKWARD_NODES;
    }

    @SuppressWarnings("EmptyCatchBlock")
    @Test(dataProvider = "dataProvider")
    public void test_mapNodes_shouldSucceed() {
        // Setup
        List<Screen> screens = Arrays.asList(Screen.values());

        for (int i = 0, length = screens.size(); i < length; i++) {
            Screen current = screens.get(i);

            for (Screen screen : screens) {
                if (!current.equals(screen)) {
                    try {
                        // When
                        List<Node> nodes = multipleShortest(current, screen);
                        Flowable<?> navigator = rx_navigate(true, current, screen);
                        LogUtil.println(nodes);

                        // Then
                        for (int j = 0, size = nodes.size(); j < size; j++) {
                            if (j < size - 1) {
                                Node node1 = nodes.get(j);
                                Node node2 = nodes.get(j + 1);
                                assertEquals(node1.S2, node2.S1);
                            }
                        }
                    } catch (Exception e) {}
                }
            }
        }
    }

    enum Screen implements ScreenType {
        SCREEN_1,
        SCREEN_2,
        SCREEN_3,
        SCREEN_4,
        SCREEN_5,
        SCREEN_6,
        SCREEN_7,
        SCREEN_8,
        SCREEN_9;

        public boolean largerThan(@NotNull Screen screen) {
            List<Screen> screens = Arrays.asList(values());
            return screens.indexOf(this) > screens.indexOf(screen);
        }

        @NotNull
        @Override
        public List<Direction> forwardAccessible(@NotNull Engine<?> engine) {
            final Screen THIS = this;
            List<Screen> screens = Arrays.asList(values());
            final Random RAND = new Random();
            int size = RAND.nextInt(screens.size());

            return CollectionUtil
                .subList(screens, 0, size).stream()
                .filter(a -> a.largerThan(THIS) && RAND.nextBoolean())
                .map(a -> new Direction(a, Flowable::just))
                .collect(Collectors.toList());
        }

        @NotNull
        @Override
        public List<Direction> backwardAccessible(@NotNull Engine<?>  engine) {
            final Screen THIS = this;
            List<Screen> screens = Arrays.asList(values());
            final Random RAND = new Random();
            int size = RAND.nextInt(screens.size());

            return CollectionUtil
                .subList(screens, 0, size).stream()
                .filter(a -> THIS.largerThan(a) && RAND.nextBoolean())
                .map(a -> new Direction(a, Flowable::just))
                .collect(Collectors.toList());
        }
    }
}
