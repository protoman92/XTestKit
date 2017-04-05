package com.swiften.testapplication.sample.test;

import com.swiften.testapplication.sample.Config;
import com.swiften.testapplication.sample.login.ui.LoginUITest;
import com.swiften.xtestkit.test.RepeatRunner;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;

/**
 * Created by haipham on 3/26/17.
 */
public final class TestApplicationRunner implements RepeatRunner.TestRunner {
    /**
     * This {@link RepeatRunner} must be static to avoid it being recreated
     * upon every test iteration.
     */
    @NotNull private static final RepeatRunner RUNNER;

    static {
        RUNNER = RepeatRunner.newBuilder()
            .addTestClass(LoginUITest.class)
            .withParameterConsumer(Config.TEST_KIT)
            .withRetryCount(Config.runCount())
            .withPartitionSize(2)
            .withVerboseLevel(0)
            .build();
    }

    @NotNull
    @DataProvider(parallel = true)
    public static Iterator<Object[]> dataProvider() {
        return RUNNER.dataParameters();
    }

    @Test
    @Override
    @RepeatRunner.TestRunnerMethod
    public void runTests() {
        RUNNER.run();
    }
}
