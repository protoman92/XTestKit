package com.swiften.xtestkit.test;

import com.swiften.test.RepeatRule;
import com.swiften.test.TestRunner;
import com.swiften.util.Log;
import com.swiften.xtestkit.util.TestUtil;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 * Created by haipham on 3/25/17.
 */
@RunWith(TestRunner.class)
public class RepeatRuleTest implements RepeatRule.Delegate {
    private int iteration;

    @Rule
    public final RepeatRule REPEAT_RULE = RepeatRule
        .newBuilder()
        .withRetries(3)
        .withDelegate(this)
        .build();

    //region RepeatRule.Delegate
    @Override
    public void onIterationStarted(int i) {
        Log.println("BeforeClass", i);
        iteration = i;
    }

    @Override
    public void onIterationFinished(int i) {
        Log.println("AfterClass", i);
    }
    //endregion

    @Before
    public void before() {
        Log.println("Before", iteration);
    }

    @After
    public void after() {
        Log.println("After", iteration);
    }

    @Test
    public void mock_runTest1_shouldRepeat() {
        Log.println("Running Test 1", iteration);
    }

    @Test
    public void mock_runTest2_shouldRepeat() {
        Log.println("Running Test 2", iteration);
    }
}
