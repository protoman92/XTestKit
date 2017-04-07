package com.swiften.testapplication.test.login.ui;

import com.swiften.testapplication.test.common.BaseUITest;
import com.swiften.testapplication.runner.TestApplicationRunner;
import com.swiften.testapplication.test.protocol.DelayProtocol;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

/**
 * Created by haipham on 3/24/17.
 */
public final class LoginUITest extends BaseUITest implements DelayProtocol {
    @NotNull private final String USERNAME, PASSWORD;

    {
        USERNAME = "email@example.com";
        PASSWORD = "12345678";
    }

    @Factory(
        dataProviderClass = TestApplicationRunner.class,
        dataProvider = "dataProvider"
    )
    public LoginUITest(int index) {
        super(index);
    }

    @Test(enabled = false)
    @SuppressWarnings("unchecked")
    public void actual_openApp_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

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

    @Test(enabled = false)
    @SuppressWarnings("unchecked")
    public void actual_navigateToLoginScreen_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

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

    @Test(enabled = false)
    @SuppressWarnings("unchecked")
    public void actual_inputCredentials_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

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
        TestSubscriber subscriber = CustomTestSubscriber.create();

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
