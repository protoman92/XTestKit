package com.swiften.xtestkit.test;

import com.swiften.xtestkit.util.LogUtil;
import org.testng.annotations.*;

/**
 * Created by haipham on 3/25/17.
 */
public class RepeatRunnerTest {
    private final int INDEX;

    @Factory(
        dataProviderClass = RepeatRuleTestRunner.class,
        dataProvider = "dataProvider"
    )
    public RepeatRunnerTest(int index) {
        LogUtil.printf(
            "Starting test %1$d on thread %2$d",
            index, currentThread());

        INDEX = index;
    }

    private long currentThread() {
        return Thread.currentThread().getId();
    }

    @BeforeClass
    public void beforeClass() {
        LogUtil.printf(
            ">>>>> BeforeClass %1$d, thread %2$d <<<<<",
            INDEX, currentThread());
    }

    @AfterClass
    public void afterClass() {
        LogUtil.printf(
            ">>>>> AfterClass %1$d, thread %2$d <<<<<",
            INDEX, currentThread());
    }

    @BeforeMethod
    public void beforeMethod() {
        LogUtil.printf(
            "<<<<< BeforeMethod %1$d, thread %2$d >>>>>",
            INDEX, currentThread());
    }

    @AfterMethod
    public void afterMethod() {
        LogUtil.printf(
            ">>>>> AfterMethod %1$d, thread %2$d <<<<<",
            INDEX, currentThread());
    }

    @Test
    public void mock_runTest1_shouldRepeat() {
        LogUtil.printf(
            "Mock 1, test %1$d, thread %2$d",
            INDEX, currentThread());
    }

    @Test
    public void mock_runTest2_shouldRepeat() {
        LogUtil.printf(
            "Mock 2, test %1$d, thread %2$d",
            INDEX, currentThread());
    }
}
