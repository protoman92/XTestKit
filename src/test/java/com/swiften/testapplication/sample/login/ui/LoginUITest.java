package com.swiften.testapplication.sample.login.ui;

import com.swiften.testapplication.sample.common.BaseUITest;
import com.swiften.testapplication.sample.test.TestApplicationRunner;
import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.BeforeClassParam;
import com.swiften.xtestkit.kit.TestKit;
import com.swiften.testapplication.sample.protocol.DelayProtocol;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.swiften.testapplication.sample.Config;
import com.swiften.testapplication.sample.common.Interaction;
import org.testng.annotations.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

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

    @Test(enabled = false)
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

    @Test(enabled = false)
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
