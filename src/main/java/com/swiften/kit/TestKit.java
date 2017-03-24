package com.swiften.kit;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.engine.mobile.Platform;
import com.swiften.kit.protocol.TestKitError;
import com.swiften.util.Log;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.*;

/**
 * Created by haipham on 3/24/17.
 */
public class TestKit implements TestKitError {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull private final List<PlatformEngine> ENGINES;

    private int current;

    TestKit() {
        ENGINES = new LinkedList<>();
        current = -1;
    }

    @NotNull
    public List<PlatformEngine> engines() {
        return Collections.unmodifiableList(ENGINES);
    }

    /**
     * Increment {@link #current} to access a different {@link PlatformEngine}
     * from {@link #ENGINES}.
     */
    public void incrementCurrent() {
        current += 1;
    }

    /**
     * Get {@link #current}.
     * @return An {@link Integer} value.
     */
    public int currentIndex() {
        return current;
    }

    //region PlatformEngine
    /**
     * Get the current active {@link PlatformEngine}.
     * @return A {@link PlatformEngine} instance.
     * @throws RuntimeException If no non-null {@link PlatformEngine} found.
     */
    @NotNull
    public PlatformEngine<?> currentEngine() {
        int current = currentIndex();
        List<PlatformEngine> engines = engines();

        if (current > -1 && current < engines.size()) {
            PlatformEngine engine = engines.get(current);

            if (Objects.nonNull(engine)) {
                return engine;
            }
        }

        throw new RuntimeException(NO_TEST_ENGINE_FOUND);
    }

    /**
     * Get the current active {@link T}.
     * @param cls A {@link T} instance.
     * @param <T> Generics that extends {@link PlatformEngine}.
     * @return A {@link T} instance.
     * @throws RuntimeException If no non-null {@link T} found.
     */
    @NotNull
    public <T extends PlatformEngine> T currentEngine(@NotNull Class<T> cls) {
        PlatformEngine engine = currentEngine();

        if (cls.isInstance(engine)) {
            return cls.cast(engine);
        }

        throw new RuntimeException(NO_TEST_ENGINE_FOUND);
    }
    //endregion

    /**
     * Convenience method for {@link org.junit.BeforeClass}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxBeforeClass() {
        return currentEngine().rxStartTestEnvironment();
    }

    /**
     * @see #rxBeforeClass()
     */
    public void beforeClass() {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxBeforeClass().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * Convenience method for {@link org.junit.Before}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxBefore() {
        return currentEngine().rxStartDriver();
    }

    /**
     * @see #rxBefore()
     */
    public void before() {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxBefore().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * Convenience method for {@link org.junit.AfterClass}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxAfterClass() {
        if (currentIndex() > -1) {
            return currentEngine().rxStopTestEnvironment();
        } else {
            return Flowable.just(false);
        }
    }

    /**
     * @see #rxAfterClass()
     */
    public void afterClass() {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxAfterClass().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * Convenience method for {@link org.junit.After}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxAfter() {
        return currentEngine().rxStopDriver();
    }

    /**
     * @see #rxAfter()
     */
    public void after() {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxAfter().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    public static final class Builder {
        @NotNull private TestKit TEST_KIT;

        Builder() {
            TEST_KIT = new TestKit();
        }

        /**
         * Set the {@link #TEST_KIT#ENGINES} {@link PlatformEngine}.
         * @param engines A {@link Collection} of {@link PlatformEngine}.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withEngines(@NotNull Collection<? extends PlatformEngine> engines) {
            TEST_KIT.ENGINES.addAll(engines);
            return this;
        }

        /**
         * Add a {@link PlatformEngine} to {@link #TEST_KIT#ENGINES}.
         * @param engine A {@link PlatformEngine} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder addEngine(@NotNull PlatformEngine engine) {
            TEST_KIT.ENGINES.add(engine);
            return this;
        }

        @NotNull
        public TestKit build() {
            return TEST_KIT;
        }
    }
}
