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
public class RepeatRunnerTest {
    private final int INDEX;

    @Factory(
        dataProviderClass = RepeatRuleTestRunner.class,
        dataProvider = "dataProvider"
    )
    public RepeatRunnerTest(int index) {
        Log.printf("Starting test %1$d on thread %2$d", index, currentThread());
        INDEX = index;
    }

    private long currentThread() {
        return Thread.currentThread().getId();
    }

    @BeforeClass
    public void beforeClass() {
        Log.printf(">>>>> BeforeClass %1$d, thread %2$d <<<<<", INDEX, currentThread());
    }

    @AfterClass
    public void afterClass() {
        Log.printf(">>>>> AfterClass %1$d, thread %2$d <<<<<", INDEX, currentThread());
    }

    @BeforeMethod
    public void beforeMethod() {
        Log.printf("<<<<< BeforeMethod %1$d, thread %2$d >>>>>", INDEX, currentThread());
    }

    @AfterMethod
    public void afterMethod() {
        Log.printf("<<<<< AfterMethod %1$d, thread %2$d >>>>>", INDEX, currentThread());
    }

    @Test
    public void mock_runTest1_shouldRepeat() {
        Log.printf("Mock 1, test %1$d, thread %2$d", INDEX, currentThread());
    }

    @Test
    public void mock_runTest2_shouldRepeat() {
        Log.printf("Mock 2, test %1$d, thread %2$d", INDEX, currentThread());
    }
}
