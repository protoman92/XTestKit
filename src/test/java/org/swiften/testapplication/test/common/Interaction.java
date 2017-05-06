package org.swiften.testapplication.test.common;

import org.swiften.xtestkit.engine.base.Platform;
import org.swiften.xtestkit.engine.base.PlatformEngine;
import org.swiften.xtestkit.kit.TestKit;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.testapplication.test.DelayProtocol;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.rx.RxUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 3/25/17.
 */

/**
 * Common app interactions that can be reused across test.
 */
public final class Interaction implements DelayProtocol {
    @NotNull private final TestKit TEST_KIT;

    private final int INDEX;

    public Interaction(@NotNull TestKit testKit, int index) {
        TEST_KIT = testKit;
        INDEX = index;
    }

    @NotNull
    private PlatformEngine<?> engine() {
        return TEST_KIT.engine(INDEX);
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

        if (ENGINE.platform().isAndroidPlatform()) {
            return ENGINE.rxElementWithText("app_title").map(a -> true);
        }

        return Flowable.just(true);
    }
    //endregion

    //region Login Screen
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxCheckLoginScreenValidity() {
        final PlatformEngine<?> ENGINE = engine();
        final Platform PLATFORM = ENGINE.platform();

        return Flowable
            .concatArray(
                /* These should be 2 editable elements */
                ENGINE.rxAllEditableElements()
                    .map(List::size)
                    .filter(a -> a == 2)
                    .switchIfEmpty(Flowable.error(new Exception())),

                ENGINE.rxElementWithText("auth_title_signInOrRegister"),

                Flowable
                    .create(obs -> {
                        if (PLATFORM.isAndroidPlatform()) {
                            obs.onNext(true);
                        }

                        obs.onComplete();
                    }, BackpressureStrategy.BUFFER)
                    .flatMap(a -> Flowable.concatArray(
                        ENGINE.rxElementWithText("auth_title_email"),
                        ENGINE.rxElementContainingText("auth_title_password")
                    ))
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
            .flatMap(RxUtil::from)
            .flatMap(a -> ENGINE.rxSendKey(a.OBJECT, INPUTS[a.INDEX]))
            .all(BooleanUtil::isTrue)
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
