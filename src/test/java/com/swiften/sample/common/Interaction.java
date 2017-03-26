package com.swiften.sample.common;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.kit.TestKit;
import com.swiften.rx.RxExtension;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import com.swiften.sample.protocol.DelayProtocol;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 3/25/17.
 */

/**
 * Common app interactions that can be reused across tests.
 */
public final class Interaction implements DelayProtocol {
    @NotNull private final TestKit TEST_KIT;

    public Interaction(@NotNull TestKit testKit) {
        TEST_KIT = testKit;
    }

    @NotNull
    private PlatformEngine<?> engine() {
        return TEST_KIT.currentEngine();
    }

    //region Splash Screen
    @NotNull
    public Flowable<Boolean> rx_splash_login_acceptPermission() {
        final PlatformEngine<?> ENGINE = engine();

        return Flowable
            .timer(splashDelay(), TimeUnit.MILLISECONDS)
            /* On Android SDK 23 and above, there will be a permission dialog
             * popup. */
            .flatMap(a -> ENGINE.rxAcceptAlert())
            .onErrorResumeNext(Flowable.just(true))
            .delay(generalDelay(), TimeUnit.MILLISECONDS);
    }

    @NotNull
    public Flowable<Boolean> rxCheckToolbarValidity() {
        final PlatformEngine<?> ENGINE = engine();
        return ENGINE.rxElementWithText("app_title").map(a -> true);
    }
    //endregion

    //region Login Screen
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxCheckLoginScreenValidity() {
        final PlatformEngine<?> ENGINE = engine();

        return Flowable
            .concatArray(
                /* These should be 2 editable elements */
                ENGINE.rxAllEditableElements()
                    .map(List::size)
                    .filter(a -> a == 2)
                    .switchIfEmpty(Flowable.error(new Exception())),

                ENGINE.rxElementWithText("auth_title_signInOrRegister"),
                ENGINE.rxElementWithText("auth_title_email"),
                ENGINE.rxElementContainingText("auth_title_password")
            )
            .toList()
            .toFlowable()
            .map(a -> true);
    }

    @NotNull
    public Flowable<Boolean> rxInputCredentials(@NotNull String username,
                                                @NotNull String password) {
        final PlatformEngine<?> ENGINE = engine();

        final String[] INPUTS = new String[] {
            username,
            password
        };

        return ENGINE.rxAllEditableElements()
            .flatMap(RxExtension::fromCollection)
            .flatMap(a -> ENGINE.rxSendKey(a.OBJECT, INPUTS[a.INDEX]))
            .toList()
            .toFlowable()
            .all(a -> a.stream().allMatch(b -> b))
            .toFlowable();
    }

    @NotNull
    public Flowable<Boolean> rxSubmitCredentials() {
        final PlatformEngine<?> ENGINE = engine();

        return ENGINE
            .rxElementWithText("auth_title_signInOrRegister")
            .flatMap(ENGINE::rxClick);
    }

    @NotNull
    public Flowable<Boolean> rxSignInWithCredentials(@NotNull String username,
                                                     @NotNull String password) {
        return rxInputCredentials(username, password)
            .flatMap(a -> rxSubmitCredentials());
    }
    //endregion
}
