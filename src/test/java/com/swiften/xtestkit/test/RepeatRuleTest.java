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
    public void onNewIteration(int i) {}
    //endregion

    @BeforeClass
    public static void beforeClass() {
        Log.println("beforeClass");
    }

    @AfterClass
    public static void afterClass() {
        Log.println("afterClass");
    }

    @Before
    public void before() {
        Log.println("Before");
    }

    @After
    public void after() {
        Log.println("Tear Down");
    }

    @Test
    public void mock_runTest_shouldRepeat() {
        Log.println(TestUtil.randomBetween(0, 10000));
    }
}
