package org.swiften.xtestkit.kit;

import org.swiften.javautilities.localizer.LCFormat;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.base.type.RetryType;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.kit.type.TestKitErrorType;
import org.swiften.xtestkit.system.network.NetworkHandler;
import org.swiften.xtestkit.system.process.ProcessRunner;
import org.swiften.xtestkit.test.RepeatRunner;
import org.swiften.xtestkit.test.type.TestListenerType;
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
import org.swiften.xtestkit.mobile.android.AndroidEngine;

import java.util.*;

/**
 * Created by haipham on 3/24/17.
 */
public class TestKit implements
    RepeatRunner.IndexConsumer,
    LocalizerType,
    TestKitErrorType,
    TestListenerType
{
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final List<Engine> ENGINES;
    @Nullable private Localizer localizer;

    TestKit() {
        PROCESS_RUNNER = ProcessRunner.builder().build();
        NETWORK_HANDLER = NetworkHandler.builder().build();
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
     * as {@link #rx_onFreshStart()} and {@link #rx_onAllTestsFinished()}.
     * @return {@link Flowable} instance.
     * @see #engines()
     * @see Engine#getClass()
     */
    @NotNull
    public Flowable<Engine> rxDistinctEngines() {
        return Flowable.fromIterable(engines()).distinct(Engine::getClass);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rx_onFreshStart() {
        final TestKit THIS = this;

        return rxKillAllAppiumInstances()
            .flatMap(a -> THIS.rxDistinctEngines())
            .concatMap(Engine::rx_onFreshStart);
    }

    /**
     * Convenient method to get {@link Engine} from {@link #ENGINES}
     * based on an Array of {@link Integer} indexes.
     * @param indexes An Array of {@link Integer}.
     * @return {@link Flowable} instance.
     * @see #rx_onBatchStarted(int[])
     * @see #rx_onBatchFinished(int[])
     */
    @NotNull
    public Flowable<Engine> rxEnginesFromIndexes(@NotNull int[] indexes) {
        final List<Engine> ENGINES = engines();
        final int SIZE = ENGINES.size();

        return Flowable
            .fromArray(BoxUtil.box(indexes))
            .filter(a -> a >= 0 && a < SIZE)
            .map(ENGINES::get)
            .filter(Objects::nonNull)
            .switchIfEmpty(RxUtil.error(PLATFORM_ENGINE_UNAVAILABLE));
    }

    @NotNull
    @Override
    public Flowable<Boolean> rx_onBatchStarted(@NotNull final int[] INDEXES) {
        return rxEnginesFromIndexes(INDEXES)
            .flatMap(a -> a.rx_onBatchStarted(INDEXES))
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .defaultIfEmpty(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rx_onBatchFinished(@NotNull final int[] INDEXES) {
        return rxEnginesFromIndexes(INDEXES)
            .flatMap(a -> a.rx_onBatchFinished(INDEXES))
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .map(BooleanUtil::toTrue)
            .defaultIfEmpty(true);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rx_onAllTestsFinished() {
        final TestKit THIS = this;

        return rxKillAllAppiumInstances()
            .flatMap(a -> THIS.rxDistinctEngines())
            .concatMap(Engine::rx_onAllTestsFinished);
    }
    //endregion

    //region Appium Setup
    /**
     * Kill all active Appium instances.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxKillAllAppiumInstances() {
        NetworkHandler networkHandler = networkHandler();
        String command = cmKillAllAppiumInstances();

        return networkHandler
            .rxKillAll(command)
            .onErrorResumeNext(Flowable.just(true));
    }
    //endregion

    //region LocalizerType
    /**
     * @param text {@link String} value.
     * @param locale {@link Locale} instance.
     * @return {@link Flowable} instance.
     * @see LocalizerType#rxLocalize(LCFormat, Locale)
     * @see Localizer#rxLocalize(LCFormat, Locale)
     */
    @NotNull
    @Override
    public Flowable<String> rxLocalize(@NotNull String text, @Nullable Locale locale) {
        return localizer().rxLocalize(text, locale);
    }

    /**
     * @param text {@link String} value.
     * @return {@link Flowable} instance.
     * @see LocalizerType#rxLocalize(LCFormat)
     * @see Localizer#rxLocalize(LCFormat)
     */
    @Override
    public Flowable<String> rxLocalize(@NotNull String text) {
        return localizer().rxLocalize(text);
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
     * @see LocalizerType#rxLocalize(LCFormat, Locale)
     * @see Localizer#rxLocalize(LCFormat, Locale)
     */
    @NotNull
    @Override
    public Flowable<String> rxLocalize(@NotNull LCFormat format,
                                       @Nullable Locale locale) {
        return localizer().rxLocalize(format, locale);
    }

    /**
     * @param format {@link LCFormat} instance.
     * @return {@link Flowable} instance.
     * @see LocalizerType#rxLocalize(LCFormat)
     * @see Localizer#rxLocalize(LCFormat)
     */
    @NotNull
    @Override
    public Flowable<String> rxLocalize(@NotNull LCFormat format) {
        return localizer().rxLocalize(format);
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
     */
    @NotNull
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    /**
     * Return {@link #NETWORK_HANDLER}.
     * @return {@link NetworkHandler} instance.
     */
    @NotNull
    public NetworkHandler networkHandler() {
        return NETWORK_HANDLER;
    }

    /**
     * Get an unmodifiable {@link #ENGINES} clone.
     * @return {@link List} of {@link Engine}.
     */
    @NotNull
    public List<Engine> engines() {
        return Collections.unmodifiableList(ENGINES);
    }

    /**
     * Get {@link #localizer}.
     * @return {@link Localizer} instance.
     */
    @NotNull
    public Localizer localizer() {
        if (Objects.nonNull(localizer)) {
            return localizer;
        }

        throw new RuntimeException(NO_LOCALIZER_FOUND);
    }
    //endregion

    //region Engine
    /**
     * Get the current active {@link Engine}.
     * @return {@link Engine} instance.
     * @throws RuntimeException If no non-null {@link Engine} found.
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

        throw new RuntimeException(NO_TEST_ENGINE_FOUND);
    }
    //endregion

    //region Test Setup

    /**
     * Convenience method for {@link org.testng.annotations.BeforeSuite}.
     * @return {@link Flowable} instance.
     * @see #rx_onFreshStart()
     */
    @NotNull
    public Flowable<Boolean> rxBeforeSuite() {
        return rx_onFreshStart();
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterSuite}.
     * @return {@link Flowable} instance.
     * @see #rx_onAllTestsFinished()
     */
    @NotNull
    public Flowable<Boolean> rxAfterSuite() {
        return rx_onAllTestsFinished();
    }

    /**
     * Convenience method for {@link org.testng.annotations.BeforeClass}.
     * @param param {@link BeforeClassParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_beforeClass(BeforeClassParam)
     */
    @NotNull
    public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
        return engine(param.index())
            .rx_beforeClass(param)
            .compose(RxUtil.withCommonSchedulers());
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterClass}.
     * @param param {@link AfterClassParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_afterClass(AfterClassParam)
     */
    @NotNull
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        return engine(param.index())
            .rx_afterClass(param)
            .compose(RxUtil.withCommonSchedulers());
    }

    /**
     * Convenience method for {@link org.testng.annotations.BeforeMethod}.
     * @param param {@link BeforeParam} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_beforeMethod(BeforeParam)
     */
    @NotNull
    public Flowable<Boolean> rxBeforeMethod(@NotNull BeforeParam param) {
        return engine(param.index())
            .rx_beforeMethod(param)
            .compose(RxUtil.withCommonSchedulers());
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterMethod}.
     * @param param {@link RetryType} instance.
     * @return {@link Flowable} instance.
     * @see Engine#rx_afterMethod(AfterParam)
     */
    @NotNull
    public Flowable<Boolean> rxAfterMethod(@NotNull AfterParam param) {
        return engine(param.index())
            .rx_afterMethod(param)
            .compose(RxUtil.withCommonSchedulers());
    }

    /**
     * @see #rxBeforeSuite()
     */
    public void beforeSuite() {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxBeforeSuite().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @see #rxAfterSuite()
     */
    public void afterSuite() {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxAfterSuite().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param {@link BeforeClassParam} instance.
     * @see #rxBeforeClass(BeforeClassParam)
     */
    public void beforeClass(@NotNull BeforeClassParam param) {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxBeforeClass(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param {@link BeforeParam} instance.
     * @see #rxBeforeMethod(BeforeParam)
     */
    public void before(@NotNull BeforeParam param) {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxBeforeMethod(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param {@link AfterClassParam} instance.
     * @see #rxAfterClass(AfterClassParam)
     */
    public void afterClass(@NotNull AfterClassParam param) {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxAfterClass(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    /**
     * @param param {@link AfterParam} instance.
     * @see #rxAfterMethod(AfterParam)
     */
    public void after(@NotNull AfterParam param) {
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
            final TestKit KIT = TEST_KIT;
            List<Engine> engines = KIT.engines();
            engines.forEach(a -> a.setLocalizer(KIT));
            KIT.localizer = LOCALIZER_BUILDER.build();
            return KIT;
        }
    }
    //endregion
}
