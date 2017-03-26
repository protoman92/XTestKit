package com.swiften.xtestkit.test;

import com.swiften.test.RepeatTestRunner;
import com.swiften.util.Log;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 * Created by haipham on 3/25/17.
 */
@RunWith(RepeatTestRunner.class)
public class RepeatRuleTest {
    @BeforeClass
    public static void beforeClass() {
        Log.println("Before Class");
    }

    @AfterClass
    public static void afterClass() {
        Log.println("After Class");
    }

    @Before
    public void before() {
        Log.println("Before");
    }

    @After
    public void after() {
        Log.println("After");
    }

    @Test
    public void mock_runTest1_shouldRepeat() {}

    @Test
    public void mock_runTest2_shouldRepeat() {}
}
