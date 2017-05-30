package org.swiften.xtestkit.kit;

import org.swiften.javautilities.localizer.LCFormat;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.type.BaseErrorType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.system.network.NetworkHandler;
import org.swiften.xtestkit.system.process.ProcessRunner;
import org.swiften.xtestkit.test.RepeatRunner;
import org.swiften.xtestkit.test.TestListenerType;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.box.BoxUtil;
import org.swiften.javautilities.localizer.Localizer;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.android.AndroidEngine;

import java.util.*;

/**
 * Created by haipham on 3/24/17.
 */
public class TestKit implements
    BaseErrorType,
    RepeatRunner.IndexConsumer,
    LocalizerType,
    TestListenerType
{
    /**
     * Get a {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final List<Engine> ENGINES;
    @Nullable private Localizer localizer;

    TestKit() {
        PROCESS_RUNNER = new ProcessRunner();
        NETWORK_HANDLER = new NetworkHandler();
        ENGINES = new LinkedList<>();
    }

    //region RepeatRunner.ParameterConsumer
    /**
     * If two consecutive {@link Engine} are of the same {@link Class}
     * (for e.g., two consecutive
     * {@link AndroidEngine}, we
     * return immediately because usually each {@link Platform} CLI tools do
     * not allow two different instances running at the same time.
     * @param indexes An Array of {@link Integer}.
     * @return {@link Integer} value.
     */
    @Override
    @SuppressWarnings("unchecked")
    public int consumptionCount(@NotNull int[] indexes) {
        TestSubscriber subscriber = CustomTestSubscriber.create();

        Flowable
            .fromArray(BoxUtil.box(indexes))
            .map(this::engine)
            .distinct(Engine::comparisonObject)
            .count()
            .toFlowable()
            .subscribe(subscriber);

        return RxTestUtil.<Long>firstNextEvent(subscriber).intValue();
    }
    //endregion

    //region TestListenerType
    /**
     * Return a distinct stream of {@link Engine} based on each of
     * the engine's {@link Class}. This is useful for one-time setup, such
     * as {@link #rxa_onFreshStart()} and {@link #rxa_onAllTestsFinished()}.
     * @return {@link Flowable} instance.
     * @see Engine#getClass()
     * @see #engines()
     */
    @NotNull
    public Flowable<Engine> rxe_distinctEngines() {
        return Flowable.fromIterable(engines()).distinct(Engine::getClass);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxa_onFreshStart() {
        final TestKit THIS = this;

        return rxa_killAllAppiumInstances()
            .concatMap(a -> THIS.rxe_distinctEngines())
            .concatMap(Engine::rxa_onFreshStart);
    }

    /**
     * Convenient method to get {@link Engine} from {@link #ENGINES}
     * based on an Array of {@link Integer} indexes.
     * @param indexes An Array of {@link Integer}.
     * @return {@link Flowable} instance.
     * @see #engines()
     * @see #rxa_onBatchStarted(int[])
     * @see #rxa_onBatchFinished(int[])
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public Flowable<Engine> rxe_enginesFromIndexes(@NotNull int[] indexes) {
        final List<Engine> ENGINES = engines();
        final int SIZE = ENGINES.size();

        return Flowable
            .fromArray(BoxUtil.box(indexes))
            .filter(a -> a >= 0 && a < SIZE)
            .map(ENGINES::get)
            .filter(Objects::nonNull)
            .switchIfEmpty(RxUtil.error(NOT_AVAILABLE));
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_onBatchStarted(@NotNull final int[] INDEXES) {
        return rxe_enginesFromIndexes(INDEXES)
            .concatMap(a -> a.rxa_onBatchStarted(INDEXES))
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .defaultIfEmpty(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_onBatchFinished(@NotNull final int[] INDEXES) {
        return rxe_enginesFromIndexes(INDEXES)
            .concatMap(a -> a.rxa_onBatchFinished(INDEXES))
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .map(BooleanUtil::toTrue)
            .defaultIfEmpty(true);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxa_onAllTestsFinished() {
        final TestKit THIS = this;

        return rxa_killAllAppiumInstances()
            .concatMap(a -> THIS.rxe_distinctEngines())
            .concatMap(Engine::rxa_onAllTestsFinished);
    }
    //endregion

    //region Appium Setup
    /**
     * Kill all active Appium instances.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxa_killAllAppiumInstances() {
        NetworkHandler networkHandler = networkHandler();
        String command = cmKillAllAppiumInstances();

        return networkHandler
            .rxa_killAll(command)
            .onErrorResumeNext(Flowable.just(true));
    }
    //endregion

    //region LocalizerType
    /**
     * @param text {@link String} value.
     * @param locale {@link Locale} instance.
     * @return {@link Flowable} instance.
     * @see LocalizerType#rxa_localize(LCFormat, Locale)
     * @see Localizer#rxa_localize(LCFormat, Locale)
     */
    @NotNull
    @Override
    public Flowable<String> rxa_localize(@NotNull String text, @Nullable Locale locale) {
        return localizer().rxa_localize(text, locale);
    }

    /**
     * @param text {@link String} value.
     * @return {@link Flowable} instance.
     * @see LocalizerType#rxa_localize(LCFormat)
     * @see Localizer#rxa_localize(LCFormat)
     */
    @Override
    public Flowable<String> rxa_localize(@NotNull String text) {
        return localizer().rxa_localize(text);
    }

    /**
     * @param text {@link String} value.
     * @param locale {@link Locale} instance.
     * @return {@link Flowable} instance.
     * @see LocalizerType#localize(LCFormat, Locale)
     * @see Localizer#localize(LCFormat, Locale)
     */
    @NotNull
    @Override
    public String localize(@NotNull String text, @Nullable Locale locale) {
        return localizer().localize(text, locale);
    }

    /**
     * @param text {@link String} value.
     * @return {@link Flowable} instance.
     * @see LocalizerType#localize(LCFormat, Locale)
     * @see Localizer#localize(LCFormat, Locale)
     */
    @Override
    public String localize(@NotNull String text) {
        return localizer().localize(text);
    }

    /**
     * @param format {@link LCFormat} instance.
     * @param locale {@link Locale} instance.
     * @return {@link Flowable} instance.
     * @see LocalizerType#rxa_localize(LCFormat, Locale)
     * @see Localizer#rxa_localize(LCFormat, Locale)
     */
    @NotNull
    @Override
    public Flowable<String> rxa_localize(@NotNull LCFormat format,
                                         @Nullable Locale locale) {
        return localizer().rxa_localize(format, locale);
    }

    /**
     * @param format {@link LCFormat} instance.
     * @return {@link Flowable} instance.
     * @see LocalizerType#rxa_localize(LCFormat)
     * @see Localizer#rxa_localize(LCFormat)
     */
    @NotNull
    @Override
    public Flowable<String> rxa_localize(@NotNull LCFormat format) {
        return localizer().rxa_localize(format);
    }

    /**
     * @param format {@link LCFormat} instance.
     * @param locale {@link Locale} instance.
     * @return {@link Flowable} instance.
     * @see LocalizerType#localize(LCFormat, Locale)
     * @see Localizer#localize(LCFormat, Locale)
     */
    @NotNull
    @Override
    public String localize(@NotNull LCFormat format,
                           @Nullable Locale locale) {
        return localizer().localize(format, locale);
    }

    /**
     * @param format {@link LCFormat} instance.
     * @return {@link Flowable} instance.
     * @see LocalizerType#localize(LCFormat, Locale)
     * @see Localizer#localize(LCFormat, Locale)
     */
    @NotNull
    @Override
    public String localize(@NotNull LCFormat format) {
        return localizer().localize(format);
    }
    //endregion

    //region Getters
    /**
     * Return {@link #PROCESS_RUNNER}.
     * @return {@link ProcessRunner} instance.
     * @see #PROCESS_RUNNER
     */
    @NotNull
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    /**
     * Return {@link #NETWORK_HANDLER}.
     * @return {@link NetworkHandler} instance.
     * @see #NETWORK_HANDLER
     */
    @NotNull
    public NetworkHandler networkHandler() {
        return NETWORK_HANDLER;
    }

    /**
     * Get an unmodifiable {@link #ENGINES} clone.
     * @return {@link List} of {@link Engine}.
     * @see #ENGINES
     */
    @NotNull
    public List<Engine> engines() {
        return Collections.unmodifiableList(ENGINES);
    }

    /**
     * Get {@link #localizer}.
     * @return {@link Localizer} instance.
     * @see #localizer
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public Localizer localizer() {
        if (Objects.nonNull(localizer)) {
            return localizer;
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }
    //endregion

    //region Engine
    /**
     * Get the current active {@link Engine}.
     * @return {@link Engine} instance.
     * @throws RuntimeException If no non-null {@link Engine} found.
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public Engine<?> engine(int current) {
        List<Engine> engines = engines();

        if (current > -1 && current < engines.size()) {
            Engine engine = engines.get(current);

            if (Objects.nonNull(engine)) {
                return engine;
            }
        }

        throw new RuntimeException(NOT_AVAILABLE);
    }
    //endregion

    //region Test Setup

    /**
     * Convenience method for {@link org.testng.annotations.BeforeSuite}.
     * @return {@link Flowable} instance.
     * @see #rxa_onFreshStart()
     */
    @NotNull
    public Flowable<Boolean> rxa_beforeSuite() {
        return rxa_onFreshStart();
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterSuite}.
     * @return {@link Flowable} instance.
     * @see #rxa_onAllTestsFinished()
     */
    @NotNull
    public Flowable<Boolean> rxa_afterSuite() {
        return rxa_onAllTestsFinished();
    }

    /**
     * Convenience method for {@link org.testng.annotations.BeforeClass}.
     * @param param {@link BeforeClassParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_beforeClass(BeforeClassParam)
     */
    @NotNull
    public Flowable<Boolean> rxa_beforeClass(@NotNull BeforeClassParam param) {
        return engine(param.index()).rxa_beforeClass(param);
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterClass}.
     * @param param {@link AfterClassParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_afterClass(AfterClassParam)
     */
    @NotNull
    public Flowable<Boolean> rxa_afterClass(@NotNull AfterClassParam param) {
        return engine(param.index()).rxa_afterClass(param);
    }

    /**
     * Convenience method for {@link org.testng.annotations.BeforeMethod}.
     * @param param {@link BeforeParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_beforeMethod(BeforeParam)
     */
    @NotNull
    public Flowable<Boolean> rxa_beforeMethod(@NotNull BeforeParam param) {
        return engine(param.index()).rxa_beforeMethod(param);
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterMethod}.
     * @param param {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rxa_afterMethod(AfterParam)
     */
    @NotNull
    public Flowable<Boolean> rxAfterMethod(@NotNull AfterParam param) {
        return engine(param.index()).rxa_afterMethod(param);
    }

    /**
     * @see #rxa_beforeSuite()
     */
    public void beforeSuite() {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxa_beforeSuite().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @see #rxa_afterSuite()
     */
    public void afterSuite() {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxa_afterSuite().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param {@link BeforeClassParam} instance.
     * @see #rxa_beforeClass(BeforeClassParam)
     */
    public void beforeClass(@NotNull BeforeClassParam param) {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxa_beforeClass(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param {@link BeforeParam} instance.
     * @see #rxa_beforeMethod(BeforeParam)
     */
    public void beforeMethod(@NotNull BeforeParam param) {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxa_beforeMethod(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param {@link AfterClassParam} instance.
     * @see #rxa_afterClass(AfterClassParam)
     */
    public void afterClass(@NotNull AfterClassParam param) {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxa_afterClass(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param {@link AfterParam} instance.
     * @see #rxAfterMethod(AfterParam)
     */
    public void afterMethod(@NotNull AfterParam param) {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxAfterMethod(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }
    //endregion

    //region CLI
    /**
     * Command to kill all existing Appium instances.
     * @return {@link String} value.
     */
    @NotNull
    public String cmKillAllAppiumInstances() {
        return "node appium";
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link TestKit}.
     */
    public static final class Builder {
        @NotNull private final TestKit TEST_KIT;
        @NotNull private final Localizer.Builder LOCALIZER_BUILDER;

        Builder() {
            TEST_KIT = new TestKit();
            LOCALIZER_BUILDER = Localizer.builder();
        }

        /**
         * Set the {@link #TEST_KIT#ENGINES} {@link Engine}.
         * @param engines {@link Collection} of {@link Engine}.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withEngines(@NotNull Collection<? extends Engine> engines) {
            TEST_KIT.ENGINES.addAll(engines);
            return this;
        }

        /**
         * Add {@link Engine} to {@link #TEST_KIT#ENGINES}.
         * @param engine {@link Engine} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder addEngine(@NotNull Engine engine) {
            TEST_KIT.ENGINES.add(engine);
            return this;
        }

        /**
         * Add a new {@link ResourceBundle} to {@link #TEST_KIT#localizer}.
         * @param name The name of the {@link ResourceBundle}.
         * @param locale The {@link Locale} of the {@link ResourceBundle}.
         * @return The current {@link Builder} instance.
         * @see Localizer.Builder#addBundle(String, Locale)
         */
        public Builder addResourceBundle(@NotNull String name, @NotNull Locale locale) {
            LOCALIZER_BUILDER.addBundle(name, locale);
            return this;
        }

        @NotNull
        public TestKit build() {
            /* Add default resource bundles */
            addResourceBundle("TestKit", Locale.US);

            final TestKit KIT = TEST_KIT;
            List<Engine> engines = KIT.engines();
            engines.forEach(a -> a.setLocalizer(KIT));
            KIT.localizer = LOCALIZER_BUILDER.build();
            return KIT;
        }
    }
    //endregion
}
