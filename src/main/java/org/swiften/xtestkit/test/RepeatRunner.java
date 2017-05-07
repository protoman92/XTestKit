package org.swiften.xtestkit.test;

import org.swiften.xtestkit.engine.base.RetriableType;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.kit.TestKit;
import org.testng.*;
import org.testng.annotations.*;
import org.testng.xml.XmlSuite;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by haipham on 3/26/17.
 */

/**
 * We need to use this class to ensure {@link TestKit}
 * based test are repeated as long as there are more than 1
 * {@link PlatformEngine} registered.
 * To use this class, we should ideally create a class that implements
 * {@link TestRunner}, with a static {@link RepeatRunner} instance so that
 * it is not recreated every time a new iteration is run (if it is not static,
 * {@link RepeatRunner#PAGINATION} will be recreated as well, leading to wrong
 * pagination.
 */
public class RepeatRunner implements
    IAnnotationTransformer2,
    ITestListener,
    ITestNGListener,
    RepeatRunnerErrorType,
    TestListenerType {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final Pagination PAGINATION;
    @NotNull final List<Class<?>> TEST_CLASSES;
    @NotNull final Collection<TestListenerType> LISTENERS;

    int verbosity;

    RepeatRunner() {
        TEST_CLASSES = new LinkedList<>();
        PAGINATION = new Pagination();
        LISTENERS = new HashSet<>();
    }

    //region IAnnotationTransformer2
    @Override
    public void transform(@NotNull ITestAnnotation annotation,
                          @Nullable Class testClass,
                          @Nullable Constructor testConstructor,
                          @Nullable Method testMethod) {
        if (testMethod != null) {
            Annotation skipMT = testMethod.getAnnotation(TestRunnerMethod.class);

            if (Objects.nonNull(skipMT)) {
                annotation.setEnabled(false);
            }
        }
    }

    @Override
    public void transform(@NotNull IConfigurationAnnotation annotation,
                          @Nullable Class testClass,
                          @Nullable Constructor testConstructor,
                          @Nullable Method testMethod) {}

    @Override
    public void transform(@NotNull IDataProviderAnnotation annotation,
                          @Nullable Method method) {}

    @Override
    public void transform(@NotNull IFactoryAnnotation annotation,
                          @Nullable Method method) {}
    //endregion

    //region ITestListener
    @Override
    public void onTestStart(@NotNull ITestResult result) {}

    @Override
    public void onTestSuccess(@NotNull ITestResult result) {}

    @Override
    public void onTestFailure(@NotNull ITestResult result) {
        LogUtil.println(result.getName());
        LogUtil.println(result.getThrowable());
    }

    @Override
    public void onTestSkipped(@NotNull ITestResult result) {}

    @Override
    public void onTestFailedButWithinSuccessPercentage(@NotNull ITestResult result) {}

    @Override
    public void onStart(@NotNull ITestContext context) {}

    @Override
    public void onFinish(@NotNull ITestContext context) {}
    //endregion

    //region Getters
    /**
     * Return {@link #TEST_CLASSES}.
     * @return A {@link List} of {@link Class}.
     */
    @NotNull
    public List<Class<?>> testClasses() {
        return TEST_CLASSES;
    }

    /**
     * Return {@link #testClasses()} as an Array.
     * @return An Array of {@link Class}.
     */
    @NotNull
    public Class[] testClassesArray() {
        List<Class<?>> testClasses = testClasses();
        return testClasses.toArray(new Class[testClasses.size()]);
    }

    /**
     * Create a fresh {@link TestNG} instance to avoid internal cache. If
     * we use only one instance of {@link TestNG}, when we batch test some
     * will be repeated (probably due to each {@link TestNG} instance keeping
     * a record of test and replaying them every iteration.
     * @return A {@link TestNG} instance.
     */
    @NotNull
    public TestNG createRunner() {
        TestNG testRunner = new TestNG();
        testRunner.setTestClasses(testClassesArray());
        testRunner.setVerbose(verbosity);
        testRunner.setAnnotationTransformer(this);
        testRunner.addListener(this);
        testRunner.setParallel(XmlSuite.ParallelMode.INSTANCES);
        return testRunner;
    }

    /**
     * Return {@link #PAGINATION#indexParameters()}.
     * @return An {@link Integer} Array.
     */
    @NotNull
    public int[] indexParameters() {
        return PAGINATION.indexParameters();
    }

    /**
     * Return {@link #PAGINATION#dataParameters()}.
     * @return An {@link Iterator} of {@link Object} Array.
     */
    @NotNull
    public Iterator<Object[]> dataParameters() {
        return PAGINATION.dataParameters();
    }

    /**
     * Return {@link #LISTENERS}.
     * @return A {@link Collection} of {@link TestListenerType}.
     * @throws RuntimeException If {@link #LISTENERS} is empty.
     */
    @NotNull
    public Collection<TestListenerType> testListeners() {
        if (LISTENERS.isEmpty()) {
            throw new RuntimeException(LISTENERS_EMPTY);
        }

        return LISTENERS;
    }
    //endregion

    //region TestListenerType
    @NotNull
    @Override
    public Flowable<Boolean> rxOnFreshStart() {
        return Flowable
            .fromIterable(testListeners())
            .flatMap(TestListenerType::rxOnFreshStart)
            .all(BooleanUtil::isTrue)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxOnBatchStarted(@NotNull final int[] INDEXES) {
        return Flowable
            .fromIterable(testListeners())
            .flatMap(a -> a.rxOnBatchStarted(INDEXES))
            .all(BooleanUtil::isTrue)
            .<Boolean>toFlowable()
            .map(a -> true)
            .defaultIfEmpty(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxOnBatchFinished(@NotNull final int[] INDEXES) {
        return Flowable
            .fromIterable(testListeners())
            .flatMap(a -> a.rxOnBatchFinished(INDEXES))
            .all(BooleanUtil::isTrue)
            .<Boolean>toFlowable()
            .map(a -> true)
            .defaultIfEmpty(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxOnAllTestsFinished() {
        return Flowable
            .fromIterable(testListeners())
            .flatMap(TestListenerType::rxOnAllTestsFinished)
            .all(BooleanUtil::isTrue)
            .<Boolean>toFlowable()
            .map(a -> true)
            .defaultIfEmpty(true);
    }
    //endregion

    //region Test Run
    @SuppressWarnings("unchecked")
    public void run() {
        TestSubscriber subscriber = CustomTestSubscriber.create();

        @SuppressWarnings("unchecked")
        final Pagination PG = this.PAGINATION;

        class Run {
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Completable run() {
                if (PG.isAvailable()) {
                    /* Refer to explanation for createRunner() as to why
                     * we need to create a new TestNg instance for every
                     * iteration */
                    final TestNG RUNNER = createRunner();
                    final int[] INDEXES = PG.indexParameters();
                    final int CONSUMED = INDEXES.length;

                    LogUtil.printf(Arrays.toString(INDEXES));

                    return rxOnBatchStarted(INDEXES)
                        .flatMapCompletable(a -> Completable.fromAction(RUNNER::run))
                        .toFlowable()
                        .defaultIfEmpty(true)
                        .flatMapCompletable(a -> PG.rxAppendConsumed(CONSUMED))
                        .toFlowable()
                        .defaultIfEmpty(true)
                        .flatMap(a -> rxOnBatchFinished(INDEXES))
                        .flatMapCompletable(a -> new Run().run());
                }

                return PG.rxResetIndex();
            }
        }

        rxOnFreshStart()
            .flatMapCompletable(a -> new Run().run())
            .toFlowable()
            .defaultIfEmpty(true)
            .flatMap(a -> rxOnAllTestsFinished())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
    }
    //endregion

    //region Builder
    public static final class Builder {
        @NotNull private final RepeatRunner RUNNER;

        Builder() {
            RUNNER = new RepeatRunner();
        }

        /**
         * Add a test {@link Class} to be passed to {@link #RUNNER#TEST_RUNNER}.
         * @param cls A {@link Class} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder addTestClass(@NotNull Class<?> cls) {
            RUNNER.TEST_CLASSES.add(cls);
            return this;
        }

        /**
         * Set {@link TestNG#m_verbose} level to control the level of logging.
         * @param level An {@link Integer} value.
         * @return THe current {@link Builder} instance.
         */
        @NotNull
        public Builder withVerboseLevel(int level) {
            RUNNER.verbosity = level;
            return this;
        }

        /**
         * Set the {@link Pagination#retries} value.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetryCount(int retries) {
            RUNNER.PAGINATION.retries = retries;
            return this;
        }

        /**
         * Set the {@link Pagination#partitionSize} value. This batches the test
         * runners into sets of test.
         * @param partition An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPartitionSize(int partition) {
            RUNNER.PAGINATION.partitionSize = partition;
            return this;
        }

        /**
         * Set the {@link #RUNNER#indexConsumer} instance.
         * @param consumer A {@link IndexConsumer} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withParameterConsumer(@NotNull IndexConsumer consumer) {
            RUNNER.PAGINATION.indexConsumer = consumer;
            return this;
        }

        /**
         * Add a {@link TestListenerType} instance to {@link #RUNNER#LISTENERS}.
         * @param listener A {@link TestListenerType} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder addListener(@NotNull TestListenerType listener) {
            RUNNER.LISTENERS.add(listener);
            return this;
        }

        @NotNull
        public RepeatRunner build() {
            return RUNNER;
        }
    }
    //endregion

    //region Pagination
    public static final class Pagination implements RetriableType {
        @Nullable IndexConsumer indexConsumer;
        int retries;
        int partitionSize;
        int consumed;

        Pagination() {}

        /**
         * Return {@link #indexConsumer}.
         * @return A {@link IndexConsumer} instance.
         */
        @NotNull
        public IndexConsumer indexConsumer() {
            if (Objects.nonNull(indexConsumer)) {
                return indexConsumer;
            }

            throw new RuntimeException(PARAMETER_CONSUMER_UNAVAILABLE);
        }

        //region RetriableType
        /**
         * Override this method to provide custom retry count.
         * @return An {@link Integer} value.
         */
        @Override
        public int retries() {
            return retries;
        }
        //endregion

        /**
         * Partition the retries to run test parallel test.
         * @return An {@link Integer} value.
         */
        public int partitionSize() {
            return partitionSize;
        }

        /**
         * Set the {@link #consumed} value in order to provide the next
         * set of parameters when another round of test commences.
         * @return A {@link Completable} instance.
         */
        @NotNull
        public Completable rxAppendConsumed(final int ADDITION) {
            return Completable.fromAction((() -> consumed += ADDITION));
        }

        /**
         * Get the appropriate index parameters after each batch run.
         * @return An {@link Integer} array.
         */
        @NotNull
        public int[] indexParameters() {
            IndexConsumer indexConsumer = indexConsumer();
            final int CONSUMED = consumed;
            int size = partitionSize();
            double tries = retries();
            int end = (int)Math.min(tries - CONSUMED, size);

            int[] newIndexes = IntStream
                .range(0, end)
                .map(a -> CONSUMED + a)
                .toArray();

            int toBeConsumed = indexConsumer.consumptionCount(newIndexes);
            return IntStream.of(newIndexes).limit(toBeConsumed).toArray();
        }

        /**
         * To be used with {@link DataProvider}.
         * @return An {@link Iterator} of {@link Object} Array.
         */
        @NotNull
        public Iterator<Object[]> dataParameters() {
            List<Object[]> params = new LinkedList<>();
            int[] indexes = indexParameters();
            IntStream.of(indexes).forEach(a -> params.add(new Object[] { a }));
            return params.iterator();
        }

        /**
         * Reset {@link #consumed} to original value.
         * @return A {@link Completable} instance.
         */
        @NotNull
        public Completable rxResetIndex() {
            return Completable.fromAction(() -> consumed = 0);
        }

        /**
         * Check whether pagination has ended.
         * @return A {@link Boolean} value.
         */
        public boolean isAvailable() {
            return consumed < retries();
        }
    }
    //endregion

    @FunctionalInterface
    public interface TestRunner {
        /**
         * Call this method to run all test registered in a
         * {@link RepeatRunner} instance.
         */
        @Test
        void runTests();
    }

    public interface IndexConsumer {
        /**
         * Get the number of consumptions, based on
         * {@link Pagination#indexParameters()}. For example, there might
         * be clashes that prevent two consumers from consuming their
         * respective indexes.
         * @param indexes An Array of {@link Integer}.
         * @return An {@link Integer} value.
         */
        default int consumptionCount(@NotNull int[] indexes) {
            return indexes.length;
        }
    }

    /**
     * Mark test methods with this {@link java.lang.annotation.Annotation} in
     * order to detect which {@link org.testng.annotations.Test} method is used
     * to run {@link RepeatRunner#run()}. That method will be skipped when
     * {@link RepeatRunner} processes
     * {@link java.lang.annotation.Annotation} in order to prevent infinitely
     * recursive test.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestRunnerMethod {}
}
