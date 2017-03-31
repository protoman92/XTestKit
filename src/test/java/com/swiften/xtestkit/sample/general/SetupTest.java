package com.swiften.xtestkit.sample.general;

import com.swiften.xtestkit.sample.Config;
import com.swiften.xtestkit.sample.test.TestApplicationRunner;
import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.kit.TestKit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.Objects;

/**
 * Created by haipham on 4/1/17.
 */
@RunWith(TestApplicationRunner.class)
public class SetupTest {
    @NotNull private static final TestKit TEST_KIT;

    static {
        TEST_KIT = Config.testKit();
    }

    @Nullable private PlatformEngine<?> engine;

    @BeforeClass
    public static void beforeClass() {
        TEST_KIT.incrementCurrent();
        TEST_KIT.beforeClass();
    }

    @AfterClass
    public static void afterClass() {
        TEST_KIT.afterClass();
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
    private PlatformEngine<?> engine() {
        if (Objects.nonNull(engine)) {
            return engine;
        }

        throw new RuntimeException("Engine cannot be null");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_startTestEnvironment_shouldSucceed() {
        // Setup
//        TestSubscriber subscriber = TestSubscriber.create();

        // When

        // Then
    }
}
