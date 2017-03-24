package sample.com.swiften.xtestkit.testapplication.login.ui;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.kit.TestKit;
import com.swiften.test.RepeatRule;
import com.swiften.test.TestKitRepeat;
import com.swiften.util.Log;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.*;
import sample.com.swiften.xtestkit.testapplication.Config;

/**
 * Created by haipham on 3/24/17.
 */
public class LoginUITest {
    @Rule
    @NotNull
    public final RepeatRule REPEAT_RULE;

    @NotNull private final TestKit TEST_KIT;
    @Nullable private PlatformEngine engine;

    {
        TEST_KIT = Config.testKit();

        REPEAT_RULE = TestKitRepeat.newBuilder()
            .withTestKit(TEST_KIT)
            .withRetries(TEST_KIT.engines().size())
            .build();
    }

    @Before
    public void before() {
        TEST_KIT.before();
        engine = TEST_KIT.currentEngine();
    }

    @After
    public void after() {
        TEST_KIT.after();
    }

    @NotNull
    private PlatformEngine engine() {
        if (engine != null) {
            return engine;
        }

        throw new RuntimeException("Engine cannot be null");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_test() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When

        // Then
        Log.println(engine);
    }
}
