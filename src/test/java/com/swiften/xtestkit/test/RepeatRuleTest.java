package com.swiften.xtestkit.test;

import com.swiften.test.RepeatRule;
import com.swiften.util.Log;
import com.swiften.xtestkit.util.TestUtil;
import org.junit.*;

/**
 * Created by haipham on 3/25/17.
 */
public class RepeatRuleTest implements RepeatRule.Delegate {
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
    }

    @Override
    public void onIterationFinished(int i) {
        Log.println("AfterClass, i");
    }
    //endregion

    @Before
    public void before() {
        Log.println("Before");
    }

    @After
    public void after() {
        Log.println("After");
    }

    @Test
    public void mock_runTest_shouldRepeat() {
        Log.println("Running Test", TestUtil.randomBetween(0, 10000));
    }
}
