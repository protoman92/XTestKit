package com.swiften.testapplication.runner;

import com.swiften.testapplication.test.Config;
import com.swiften.testapplication.test.login.ui.LoginUITest;
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
        RUNNER = RepeatRunner.builder()
            .addTestClass(LoginUITest.class)
            .addListener(Config.TEST_KIT)
            .withParameterConsumer(Config.TEST_KIT)
            .withRetryCount(Config.runCount())
            .withPartitionSize(4)
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
