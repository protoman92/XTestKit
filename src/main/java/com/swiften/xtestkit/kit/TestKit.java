package com.swiften.xtestkit.kit;

import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.AfterClassParam;
import com.swiften.xtestkit.engine.base.param.AfterParam;
import com.swiften.xtestkit.engine.base.param.BeforeClassParam;
import com.swiften.xtestkit.engine.base.param.BeforeParam;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.kit.protocol.TestKitError;
import com.swiften.xtestkit.localizer.Localizer;
import com.swiften.xtestkit.util.RxUtil;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by haipham on 3/24/17.
 */
public class TestKit implements PlatformEngine.TextDelegate, TestKitError {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull private final List<PlatformEngine> ENGINES;
    @Nullable private Localizer localizer;

    private int current;

    TestKit() {
        ENGINES = new LinkedList<>();
        current = -1;
        RxUtil.overrideErrorHandler();
    }

    //region PlatformEngine.TextDelegate
    @NotNull
    @Override
    public Flowable<String> rxLocalize(@NotNull String text) {
        return localizer().rxLocalize(text);
    }

    @NotNull
    @Override
    public String localize(@NotNull String text) {
        return localizer().localize(text);
    }
    //endregion

    //region Getters
    /**
     * Get an unmodifiable {@link #ENGINES} clone.
     * @return A {@link List} of {@link PlatformEngine}.
     */
    @NotNull
    public List<PlatformEngine> engines() {
        return Collections.unmodifiableList(ENGINES);
    }

    /**
     * Get {@link #localizer}.
     * @return A {@link Localizer} instance.
     */
    @NotNull
    public Localizer localizer() {
        if (Objects.nonNull(localizer)) {
            return localizer;
        }

        throw new RuntimeException(NO_LOCALIZER_FOUND);
    }
    //endregion

    //region Iteration
    /**
     * Increment {@link #current} to access a different {@link PlatformEngine}
     * from {@link #ENGINES}.
     */
    public void incrementCurrent() {
        current += 1;
    }

    /**
     * Same as above, but returns a {@link Flowable} instance for easy
     * {@link Flowable} chaining.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxIncrementCurrent() {
        return Completable
            .fromAction(this::incrementCurrent)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Reset {@link #current} to original value.
     */
    public void resetCounter() {
        current = -1;
    }

    /**
     * Get {@link #current}.
     * @return An {@link Integer} value.
     */
    public int currentIndex() {
        return current;
    }
    //endregion

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

    //region Test Setup
    /**
     * Convenience method for {@link org.junit.BeforeClass}.
     * @param param A {@link BeforeClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeClass(BeforeClassParam)
     */
    @NotNull
    public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
        return currentEngine().rxBeforeClass(param);
    }

    /**
     * Convenience method for {@link org.junit.AfterClass}.
     * @param param A {@link AfterClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterClass(AfterClassParam)
     */
    @NotNull
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        return currentEngine().rxAfterClass(param);
    }

    /**
     * Convenience method for {@link org.junit.Before}.
     * @param param A {@link BeforeParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBefore(BeforeParam)
     */
    @NotNull
    public Flowable<Boolean> rxBefore(@NotNull BeforeParam param) {
        return currentEngine().rxBefore(param);
    }

    /**
     * Convenience method for {@link org.junit.After}.
     * @param param A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfter(AfterParam)
     */
    @NotNull
    public Flowable<Boolean> rxAfter(@NotNull AfterParam param) {
        return currentEngine().rxAfter(param);
    }

    /**
     * @see #rxBeforeClass(BeforeClassParam)
     */
    public void beforeClass() {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxBeforeClass(BeforeClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @see #rxBefore(BeforeParam)
     */
    public void before() {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxBefore(BeforeParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @see #rxAfterClass(AfterClassParam)
     */
    public void afterClass() {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxAfterClass(AfterClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @see #rxAfter(AfterParam)
     */
    public void after() {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxAfter(AfterParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }
    //endregion

    public static final class Builder {
        @NotNull private final TestKit TEST_KIT;
        @NotNull private final Localizer.Builder LOCALIZER_BUILDER;

        Builder() {
            TEST_KIT = new TestKit();
            LOCALIZER_BUILDER = Localizer.newBuilder();
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

        /**
         * Add a new {@link ResourceBundle} to {@link #TEST_KIT#localizer}.
         * @param name The name of the {@link ResourceBundle}.
         * @param locale The {@link Locale} of the {@link ResourceBundle}.
         * @return The current {@link Builder} instance.
         * @see Localizer.Builder#addBundleName(String, Locale)
         */
        public Builder addResourceBundle(@NotNull String name,
                                         @NotNull Locale locale) {
            LOCALIZER_BUILDER.addBundleName(name, locale);
            return this;
        }

        @NotNull
        public TestKit build() {
            final TestKit KIT = TEST_KIT;
            List<PlatformEngine> engines = KIT.engines();
            engines.forEach(a -> a.setTextDelegate(KIT));
            KIT.localizer = LOCALIZER_BUILDER.build();
            return KIT;
        }
    }
}
