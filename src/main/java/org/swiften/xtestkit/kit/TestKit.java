package org.swiften.xtestkit.kit;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.box.HPBoxes;
import org.swiften.javautilities.localizer.Localizer;
import org.swiften.javautilities.localizer.LocalizerProviderType;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.object.HPObjects;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.HPReactives;
import org.swiften.xtestkit.android.AndroidEngine;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.test.RepeatRunner;
import org.swiften.xtestkit.test.TestListenerType;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.xtestkitcomponents.system.network.NetworkHandler;
import org.swiften.xtestkitcomponents.system.process.ProcessRunner;

import java.util.*;

/**
 * Created by haipham on 3/24/17.
 */
public class TestKit implements
    ErrorProviderType,
    LocalizerProviderType,
    RepeatRunner.IndexConsumer,
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
    @Nullable private LocalizerType localizer;

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
            .fromArray(HPBoxes.box(indexes))
            .map(this::engine)
            .distinct(Engine::comparisonObject)
            .count()
            .toFlowable()
            .subscribe(subscriber);

        return HPReactives.<Long>firstNextEvent(subscriber).intValue();
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
            .fromArray(HPBoxes.box(indexes))
            .filter(a -> a >= 0 && a < SIZE)
            .map(ENGINES::get)
            .filter(HPObjects::nonNull)
            .switchIfEmpty(HPReactives.error(NOT_AVAILABLE));
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_onBatchStarted(@NotNull final int[] INDEXES) {
        return rxe_enginesFromIndexes(INDEXES)
            .concatMap(a -> a.rxa_onBatchStarted(INDEXES))
            .all(HPBooleans::isTrue)
            .toFlowable()
            .defaultIfEmpty(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_onBatchFinished(@NotNull final int[] INDEXES) {
        return rxe_enginesFromIndexes(INDEXES)
            .concatMap(a -> a.rxa_onBatchFinished(INDEXES))
            .all(HPBooleans::isTrue)
            .toFlowable()
            .map(HPBooleans::toTrue)
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
     * @see HPObjects#requireNotNull(Object, String)
     * @see #localizer
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public LocalizerType localizer() {
        HPObjects.requireNotNull(localizer, NOT_AVAILABLE);
        return localizer;
    }
    //endregion

    //region Engine
    /**
     * Get the current active {@link Engine}.
     * @return {@link Engine} instance.
     * @see HPObjects#nonNull(Object)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public Engine<?> engine(int current) {
        List<Engine> engines = engines();

        if (current > -1 && current < engines.size()) {
            Engine engine = engines.get(current);
            HPObjects.requireNotNull(engine, NOT_AVAILABLE);
            return engine;
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
     * Convenience execution for {@link org.testng.annotations.BeforeSuite}.
     * @see #rxa_beforeSuite()
     */
    public void beforeSuite() {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxa_beforeSuite().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
    }

    /**
     * Convenience execution for {@link org.testng.annotations.AfterSuite}.
     * @see #rxa_afterSuite()
     */
    public void afterSuite() {
        TestSubscriber<Boolean> subscriber = CustomTestSubscriber.create();
        rxa_afterSuite().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
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
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withEngines(@NotNull Collection<? extends Engine> engines) {
            TEST_KIT.ENGINES.addAll(engines);
            return this;
        }

        /**
         * Add {@link Engine} to {@link #TEST_KIT#ENGINES}.
         * @param engine {@link Engine} instance.
         * @return {@link Builder} instance.
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
         * @return {@link Builder} instance.
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
            LocalizerType localizer = LOCALIZER_BUILDER.build();

            final TestKit KIT = TEST_KIT;
            List<Engine> engines = KIT.engines();
            engines.forEach(a -> a.setLocalizer(localizer));
            KIT.localizer = localizer;
            return KIT;
        }
    }
    //endregion
}
