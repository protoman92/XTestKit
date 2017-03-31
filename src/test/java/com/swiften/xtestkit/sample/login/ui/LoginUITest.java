package com.swiften.xtestkit.sample.login.ui;

import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.kit.TestKit;
import com.swiften.xtestkit.sample.protocol.DelayProtocol;
import com.swiften.xtestkit.sample.test.TestApplicationRunner;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.*;
import com.swiften.xtestkit.sample.Config;
import com.swiften.xtestkit.sample.common.Interaction;
import org.junit.runner.RunWith;

/**
 * Created by haipham on 3/24/17.
 */
@RunWith(TestApplicationRunner.class)
public final class LoginUITest implements DelayProtocol {
    /**
     * This needs to be static so that {@link BeforeClass} and
     * {@link AfterClass} can access its methods. Each test suite will have
     * one statis {@link TestKit} to avoid synchronization problem with
     * {@link TestKit#current} value.
     */
    @NotNull private static final TestKit TEST_KIT;

    static {
        TEST_KIT = Config.testKit();
    }

    @NotNull private final Interaction INTERACTION;
    @Nullable private PlatformEngine engine;

    @NotNull private final String USERNAME, PASSWORD;

    {
        INTERACTION = new Interaction(TEST_KIT);
        USERNAME = "email@example.com";
        PASSWORD = "12345678";
    }

    @BeforeClass
    public static void beforeClass() {
        /* Calling beforeClass() here ensures that each PlatformEngine will
         * only start the test environment once */
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
    private PlatformEngine engine() {
        if (engine != null) {
            return engine;
        }

        throw new RuntimeException("Engine cannot be null");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_openApp_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        INTERACTION
            .rx_splash_login_acceptPermission()
            .flatMap(a -> INTERACTION.rxCheckToolbarValidity())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
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
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_inputCredentials_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        INTERACTION
            .rx_splash_login_acceptPermission()
            .flatMap(a -> INTERACTION.rxInputCredentials(USERNAME, PASSWORD))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_submitCredentials_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        INTERACTION
            .rx_splash_login_acceptPermission()
            .flatMap(a -> INTERACTION.rxSignInWithCredentials(USERNAME, PASSWORD))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }
}
