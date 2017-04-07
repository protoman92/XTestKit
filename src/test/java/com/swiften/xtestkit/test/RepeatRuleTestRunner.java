package com.swiften.xtestkit.test;

import com.swiften.xtestkit.test.protocol.TestListener;
import io.reactivex.Flowable;
import org.apache.bcel.generic.RET;
import org.jetbrains.annotations.NotNull;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.*;

import java.util.Iterator;
import java.util.Random;

/**
 * Created by haipham on 4/5/17.
 */
public class RepeatRuleTestRunner implements
    RepeatRunner.TestRunner {
    @NotNull private static final RepeatRunner RUNNER;
    @NotNull private static final RepeatRunner.IndexConsumer PC;
    @NotNull private static final TestListener LISTENER;

    private static final int RETRY;
    private static final int PARTITION_SIZE;
    private static final int PARTITION_COUNT;

    static {
        RETRY = 11;
        PARTITION_SIZE = 3;
        PARTITION_COUNT = (int)Math.ceil((double)RETRY / PARTITION_SIZE);

        PC = new RepeatRunner.IndexConsumer() {
            @Override
            public int consumptionCount(@NotNull int[] indexes) {
                int def = indexes.length;
                return def;
            }
        };

        LISTENER = mock(TestListener.class);

        RUNNER = RepeatRunner.builder()
            .addTestClass(RepeatRuleTest.class)
            .withVerboseLevel(0)
            .withRetryCount(RETRY)
            .withPartitionSize(PARTITION_SIZE)
            .withParameterConsumer(PC)
            .addListener(LISTENER)
            .build();
    }

    @NotNull
    @DataProvider(parallel = true)
    public static Iterator<Object[]> dataProvider() {
        return RUNNER.dataParameters();
    }

    @BeforeClass
    public static void beforeClass() {
        doReturn(Flowable.just(true)).when(LISTENER).rxOnFreshStart();
        doReturn(Flowable.just(true)).when(LISTENER).rxOnBatchStarted(any());
        doReturn(Flowable.just(true)).when(LISTENER).rxOnBatchFinished(any());
        doReturn(Flowable.just(true)).when(LISTENER).rxOnAllTestsFinished();
    }

    @AfterClass
    public void afterClass() {
        reset(LISTENER);
    }

    //region RepeatRunner.TestRunner
    @Test
    @Override
    public void runTests() {
        // Setup

        // When
        RUNNER.run();

        // Then
        verify(LISTENER).rxOnFreshStart();
        verify(LISTENER, atLeast(PARTITION_COUNT)).rxOnBatchStarted(any());
        verify(LISTENER, atLeast(PARTITION_COUNT)).rxOnBatchFinished(any());
        verify(LISTENER).rxOnAllTestsFinished();
    }
    //endregion
}
