package com.swiften.xtestkit.util;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by haipham on 3/31/17.
 */
public class RxUtil {
    /**
     * Override the default error handling in RxJava 2 to log errors, if
     * {@link Constants#isLoggingEnabled()} is true.
     */
    public static void overrideErrorHandler() {
        final Consumer<? super Throwable> HANDLER = RxJavaPlugins.getErrorHandler();

        RxJavaPlugins.setErrorHandler(t -> {
            LogUtil.println(t);
            HANDLER.accept(t);
        });
    }
}
