package org.swiften.xtestkit.test;

import org.swiften.javautilities.log.LogUtil;
import org.testng.annotations.*;

/**
 * Created by haipham on 3/25/17.
 */
public final class RepeatRunnerTest {
    private final int INDEX;

    @Factory(
        dataProviderClass = RepeatRuleTestRunner.class,
        dataProvider = "dataProvider"
    )
    public RepeatRunnerTest(int index) {
        LogUtil.printf("Starting test %1$d on thread %2$d", index, currentThread());
        INDEX = index;
    }

    private long currentThread() {
        return Thread.currentThread().getId();
    }

    @BeforeTest
    @BeforeSuite
    public void beforeSuite() {}

    @BeforeClass
    public void beforeClass() {}

    @AfterClass
    public void afterClass() {}

    @BeforeMethod
    public void beforeMethod() {}

    @AfterMethod
    public void afterMethod() {}

    @Test
    public void test_runTest1_shouldRepeat() {}

    @Test
    public void test_runTest2_shouldRepeat() {}
}
