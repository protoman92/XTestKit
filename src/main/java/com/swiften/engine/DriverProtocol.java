package com.swiften.engine;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/19/17.
 */
public interface DriverProtocol {
    /**
     * Start an Appium driver instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxStartDriver();

    /**
     * Stop the active Appium driver instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxStopDriver();
}
