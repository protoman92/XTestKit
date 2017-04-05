package com.swiften.xtestkit.test;

import com.swiften.xtestkit.util.Log;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by haipham on 4/5/17.
 */
public class RepeatRuleTestRunner implements RepeatRunner.TestRunner {
    @NotNull
    private static final RepeatRunner RUNNER;

    static {
        RUNNER = RepeatRunner.newBuilder()
            .addTestClass(RepeatRuleTest.class)
            .withVerboseLevel(0)
            .withRetryCount(11)
            .withPartitionSize(3)
            .build();
    }

    @NotNull
    @DataProvider(parallel = true)
    public static Iterator<Object[]> dataProvider() {
        return RUNNER.dataParameters();
    }

    //region RepeatRunner.TestRunner
    @Test
    @Override
    public void runTests() {
        RUNNER.run();
    }
    //endregion
}
