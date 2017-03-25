package sample.testapplication.login.ui;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.kit.TestKit;
import com.swiften.test.RepeatRule;
import com.swiften.test.TestKitRule;
import com.swiften.util.Log;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.*;
import sample.testapplication.Config;
import sample.testapplication.common.Interaction;

/**
 * Created by haipham on 3/24/17.
 */
public class LoginUITest {
    @Rule
    @NotNull
    public final RepeatRule REPEAT_RULE;

    @NotNull private final TestKit TEST_KIT;
    @NotNull private final Interaction INTERACTION;
    @Nullable private PlatformEngine engine;

    {
        TEST_KIT = Config.testKit();
        INTERACTION = new Interaction(TEST_KIT);

        /* The TestKitRule class ensures that all PlatformEngines are
         * initiated and performs tests on them sequentially */
        REPEAT_RULE = TestKitRule.newBuilder().withTestKit(TEST_KIT).build();
    }

    @Before
    public void before() {
        engine = TEST_KIT.currentEngine();
        TEST_KIT.before();
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
    public void actual_navigateToLoginScreen_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        INTERACTION
            .rx_splash_login_acceptPermission()
            .flatMap(a -> INTERACTION.rxCheckLoginScreenValidity())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        Log.println(subscriber.getEvents());
    }
}
