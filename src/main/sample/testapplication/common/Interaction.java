package sample.testapplication.common;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.engine.mobile.Platform;
import com.swiften.kit.TestKit;
import com.swiften.util.Log;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import sample.testapplication.protocol.DelayProtocol;

import java.util.ResourceBundle;
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
    private PlatformEngine engine() {
        return TEST_KIT.currentEngine();
    }

    //region Splash Screen
    @NotNull
    public Flowable<Boolean> rx_splash_login_acceptPermission() {
        PlatformEngine engine = engine();

        return Flowable
            .timer(splashDelay(), TimeUnit.MILLISECONDS)
            /* On Android SDK 23 and above, there will be a permission dialog
             * popup. */
            .flatMap(a -> engine.rxAcceptAlert())
            .onErrorResumeNext(Flowable.just(true))
            .cast(Boolean.class);
    }
    //endregion

    //region Login Screen
    @NotNull
    @SuppressWarnings("unchecked")
    public Flowable<Boolean> rxCheckLoginScreenValidity() {
        PlatformEngine engine = engine();

        return Flowable
            .concatArray(
                engine.rxAllClickableElements()
            )
            .doOnNext(Log::println)
            .toList()
            .toFlowable()
            .map(a -> true);
    }
    //endregion
}
