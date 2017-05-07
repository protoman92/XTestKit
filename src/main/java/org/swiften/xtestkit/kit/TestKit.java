package org.swiften.xtestkit.kit;

import org.swiften.xtestkit.engine.base.Platform;
import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.engine.base.type.RetryType;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.kit.param.BeforeParam;
import org.swiften.xtestkit.system.NetworkHandler;
import org.swiften.xtestkit.system.ProcessRunner;
import org.swiften.xtestkit.system.ProcessRunnableType;
import org.swiften.xtestkit.test.RepeatRunner;
import org.swiften.xtestkit.test.TestListenerType;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.box.BoxUtil;
import org.swiften.javautilities.localizer.Localizer;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.engine.mobile.android.AndroidEngine;

import java.io.IOException;
import java.util.*;

/**
 * Created by haipham on 3/24/17.
 */
public class TestKit implements
    RepeatRunner.IndexConsumer,
    PlatformEngine.TextDelegate,
    ProcessRunnableType,
    TestKitErrorType,
    TestListenerType {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final List<PlatformEngine> ENGINES;
    @Nullable private Localizer localizer;

    TestKit() {
        PROCESS_RUNNER = ProcessRunner.builder().build();
        NETWORK_HANDLER = NetworkHandler.builder().build();
        ENGINES = new LinkedList<>();
    }

    //region ProcessRunnableType
    @NotNull
    @Override
    public String execute(@NotNull String args) throws IOException {
        return processRunner().execute(args);
    }

    @NotNull
    @Override
    public Flowable<String> rxExecute(@NotNull String args) {
        return processRunner().rxExecute(args);
    }
    //endregion

    //region RepeatRunner.ParameterConsumer
    /**
     * If two consecutive {@link PlatformEngine} are of the same {@link Class}
     * (for e.g., two consecutive
     * {@link AndroidEngine}, we
     * return immediately because usually each {@link Platform} CLI tools do
     * not allow two different instances running at the same time.
     * @param indexes An Array of {@link Integer}.
     * @return An {@link Integer} value.
     */
    @Override
    @SuppressWarnings("unchecked")
    public int consumptionCount(@NotNull int[] indexes) {
        TestSubscriber subscriber = CustomTestSubscriber.create();

        Flowable
            .fromArray(BoxUtil.box(indexes))
            .map(this::engine)
            .distinct(PlatformEngine::getComparisonObject)
            .count()
            .toFlowable()
            .subscribe(subscriber);

        return RxTestUtil.<Long>getFirstNextEvent(subscriber).intValue();
    }
    //endregion

    //region TestListenerType
    /**
     * Return a distinct stream of {@link PlatformEngine} based on each of
     * the engine's {@link Class}. This is useful for one-time setup, such
     * as {@link #rxOnFreshStart()} and {@link #rxOnAllTestsFinished()}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<PlatformEngine> rxDistinctEngines() {
        return Flowable
            .fromIterable(engines())
            .distinct(PlatformEngine::getClass);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxOnFreshStart() {
        LogUtil.println("Fresh start for all test");

        return Flowable
            .concatArray(
                rxKillAllAppiumInstances(),
                rxDistinctEngines().flatMap(PlatformEngine::rxOnFreshStart)
            )
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Convenient method to get {@link PlatformEngine} from {@link #ENGINES}
     * based on an Array of {@link Integer} indexes.
     * @param indexes An Array of {@link Integer}.
     * @return A {@link Flowable} instance.
     * @see #rxOnBatchStarted(int[])
     * @see #rxOnBatchFinished(int[])
     */
    @NotNull
    public Flowable<PlatformEngine> rxEnginesFromIndexes(@NotNull int[] indexes) {
        final List<PlatformEngine> ENGINES = engines();
        final int SIZE = ENGINES.size();

        return Flowable
            .fromArray(BoxUtil.box(indexes))
            .filter(a -> a >= 0 && a < SIZE)
            .map(ENGINES::get)
            .filter(Objects::nonNull)
            .switchIfEmpty(Flowable.error(new Exception(PLATFORM_ENGINE_UNAVAILABLE)));
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxOnBatchStarted(@NotNull final int[] INDEXES) {
        LogUtil.printf("Starting batch with indexes %s", Arrays.toString(INDEXES));

        return rxEnginesFromIndexes(INDEXES)
            .flatMap(a -> a.rxOnBatchStarted(INDEXES))
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .defaultIfEmpty(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxOnBatchFinished(@NotNull final int[] INDEXES) {
        LogUtil.printf("Finishing batch with indexes %s", Arrays.toString(INDEXES));

        return rxEnginesFromIndexes(INDEXES)
            .flatMap(a -> a.rxOnBatchFinished(INDEXES))
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .map(a -> true)
            .defaultIfEmpty(true);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxOnAllTestsFinished() {
        return Flowable
            .concatArray(
                rxKillAllAppiumInstances(),
                rxDistinctEngines().flatMap(PlatformEngine::rxOnAllTestsFinished)
            )
            .all(BooleanUtil::isTrue)
            .toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Appium Setup
    /**
     * Kill all active Appium instances.
     * @return A {@link Flowable} instance.
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
     * Return {@link #PROCESS_RUNNER}.
     * @return A {@link ProcessRunner} instance.
     */
    @NotNull
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    /**
     * Return {@link #NETWORK_HANDLER}.
     * @return A {@link NetworkHandler} instance.
     */
    @NotNull
    public NetworkHandler networkHandler() {
        return NETWORK_HANDLER;
    }

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
     * Convenience method for {@link org.testng.annotations.BeforeSuite}.
     * @return A {@link Flowable} instance.
     * @see #rxOnFreshStart()
     */
    @NotNull
    public Flowable<Boolean> rxBeforeSuite() {
        return rxOnFreshStart();
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterSuite}.
     * @return A {@link Flowable} instance.
     * @see #rxOnAllTestsFinished()
     */
    @NotNull
    public Flowable<Boolean> rxAfterSuite() {
        return rxOnAllTestsFinished();
    }

    /**
     * Convenience method for {@link org.testng.annotations.BeforeClass}.
     * @param param A {@link BeforeClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeClass(BeforeClassParam)
     */
    @NotNull
    public Flowable<Boolean> rxBeforeClass(@NotNull BeforeClassParam param) {
        return engine(param.index())
            .rxBeforeClass(param)
            .compose(RxUtil.withCommonSchedulers());
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterClass}.
     * @param param A {@link AfterClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterClass(AfterClassParam)
     */
    @NotNull
    public Flowable<Boolean> rxAfterClass(@NotNull AfterClassParam param) {
        return engine(param.index())
            .rxAfterClass(param)
            .compose(RxUtil.withCommonSchedulers());
    }

    /**
     * Convenience method for {@link org.testng.annotations.BeforeMethod}.
     * @param param A {@link BeforeParam} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxBeforeMethod(BeforeParam)
     */
    @NotNull
    public Flowable<Boolean> rxBeforeMethod(@NotNull BeforeParam param) {
        return engine(param.index())
            .rxBeforeMethod(param)
            .compose(RxUtil.withCommonSchedulers());
    }

    /**
     * Convenience method for {@link org.testng.annotations.AfterMethod}.
     * @param param A {@link RetryType} instance.
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxAfterMethod(AfterParam)
     */
    @NotNull
    public Flowable<Boolean> rxAfterMethod(@NotNull AfterParam param) {
        return engine(param.index())
            .rxAfterMethod(param)
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
     * @param param A {@link BeforeClassParam} instance.
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
     * @param param A {@link BeforeParam} instance.
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
     * @param param An {@link AfterClassParam} instance.
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
     * @param param An {@link AfterParam} instance.
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
     * @return A {@link String} value.
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
         * @see Localizer.Builder#addBundle(String, Locale)
         */
        public Builder addResourceBundle(@NotNull String name, @NotNull Locale locale) {
            LOCALIZER_BUILDER.addBundle(name, locale);
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
    //endregion
}
