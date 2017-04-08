package com.swiften.xtestkit.test;

import com.swiften.xtestkit.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by haipham on 3/25/17.
 */
public class RepeatRuleTest {
    private final int INDEX;

    @Factory(
        dataProviderClass = RepeatRuleTestRunner.class,
        dataProvider = "dataProvider"
    )
    public RepeatRuleTest(int index) {
        Log.printf("Starting test %1$d on thread %2$d", index, Thread.currentThread().getId());
        INDEX = index;
    }

    @BeforeClass
    public void beforeClass() {}

    @AfterClass
    public void afterClass() {}

    @BeforeMethod
    public void beforeMethod() {}

    @AfterMethod
    public void afterMethod() {}

    @Test
    public void mock_runTest1_shouldRepeat() {
        Log.printf("Mock 1, test %1$d, thread %2$d", INDEX, Thread.currentThread().getId());
    }

    @Test
    public void mock_runTest2_shouldRepeat() {
        Log.printf("Mock 2, test %1$d, thread %2$d", INDEX, Thread.currentThread().getId());
    }
}
