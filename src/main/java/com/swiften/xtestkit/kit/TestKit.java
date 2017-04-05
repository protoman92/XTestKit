package com.swiften.xtestkit.kit;

import com.swiften.xtestkit.engine.base.Platform;
import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.AfterClassParam;
import com.swiften.xtestkit.engine.base.param.AfterParam;
import com.swiften.xtestkit.engine.base.param.BeforeClassParam;
import com.swiften.xtestkit.engine.base.param.BeforeParam;
import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.kit.protocol.TestKitError;
import com.swiften.xtestkit.localizer.Localizer;
import com.swiften.xtestkit.test.RepeatRunner;
import com.swiften.xtestkit.util.Log;
import com.swiften.xtestkit.util.RxUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by haipham on 3/24/17.
 */
public class TestKit implements
    RepeatRunner.IndexConsumer,
    PlatformEngine.TextDelegate,
    TestKitError {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull private final List<PlatformEngine> ENGINES;
    @Nullable private Localizer localizer;

    TestKit() {
        ENGINES = new LinkedList<>();
        RxUtil.overrideErrorHandler();
    }

    //region RepeatRunner.ParameterConsumer

    /**
     * If two consecutive {@link PlatformEngine} are of the same {@link Class}
     * (for e.g., two consecutive
     * {@link com.swiften.xtestkit.engine.mobile.android.AndroidEngine}, we
     * return immediately because usually each {@link Platform} CLI tools do
     * not allow two different instances running at the same time.
     * @param indexes An Array of {@link Integer}.
     * @return An {@link Integer} value.
     */
    @Override
    public int consumptionCount(@NotNull int[] indexes) {
        for (int i = 0, length = indexes.length; i < length; i++) {
            if (i < length - 1) {
                PlatformEngine<?> first = engine(i);
                PlatformEngine<?> second = engine(i + 1);

                if (first.getClass().equals(second.getClass())) {
                    return i + 1;
                }
            }
        }

        return RepeatRunner.IndexConsumer.super.consumptionCount(indexes);
    }
    //endregion

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

    //region PlatformEngine
    /**
     * Get the current active {@link PlatformEngine}.
     * @return A {@link PlatformEngine} instance.
     * @throws RuntimeException If no non-null {@link PlatformEngine} found.
     */
    @NotNull
    public PlatformEngine<?> engine(int current) {
        List<PlatformEngine> engines = engines();

        if (current > -1 && current < engines.size()) {
            PlatformEngine engine = engines.get(current);

            if (Objects.nonNull(engine)) {
                return engine;
            }
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
        return engine(param.index()).rxBeforeClass(param);
    }

    /**
     * Convenience method for {@link org.junit.AfterClass}.
     * @param param A {@link AfterClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterClass(AfterClassParam)
     */
    @NotNull
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        return engine(param.index()).rxAfterClass(param);
    }

    /**
     * Convenience method for {@link org.junit.Before}.
     * @param param A {@link BeforeParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeMethod(BeforeParam)
     */
    @NotNull
    public Flowable<Boolean> rxBeforeMethod(@NotNull BeforeParam param) {
        return engine(param.index()).rxBeforeMethod(param);
    }

    /**
     * Convenience method for {@link org.junit.After}.
     * @param param A {@link RetryProtocol} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterMethod(AfterParam)
     */
    @NotNull
    public Flowable<Boolean> rxAfterMethod(@NotNull AfterParam param) {
        return engine(param.index()).rxAfterMethod(param);
    }

    /**
     * @param param A {@link BeforeClassParam} instance.
     * @see #rxBeforeClass(BeforeClassParam)
     */
    public void beforeClass(@NotNull BeforeClassParam param) {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxBeforeClass(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param A {@link BeforeParam} instance.
     * @see #rxBeforeMethod(BeforeParam)
     */
    public void before(@NotNull BeforeParam param) {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxBeforeMethod(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param An {@link AfterClassParam} instance.
     * @see #rxAfterClass(AfterClassParam)
     */
    public void afterClass(@NotNull AfterClassParam param) {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxAfterClass(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param An {@link AfterParam} instance.
     * @see #rxAfterMethod(AfterParam)
     */
    public void after(@NotNull AfterParam param) {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        rxAfterMethod(param).subscribe(subscriber);
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
