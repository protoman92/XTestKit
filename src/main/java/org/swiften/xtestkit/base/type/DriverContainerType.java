package org.swiften.xtestkit.base.type;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.swiften.javautilities.rx.RxUtil;

import java.util.concurrent.Callable;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides {@link D} driver instance.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
@FunctionalInterface
public interface DriverContainerType<D extends WebDriver> {
    /**
     * Get the associated {@link D} instance.
     * @return {@link D} instance.
     */
    @NotNull D driver();

    /**
     * Instead of calling {@link RxUtil#error(String)} directly, call this
     * method so that we can get the {@link WebDriver#getPageSource()} as well.
     * @param error {@link String} value.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see Flowable#error(Callable)
     * @see WebDriver#getPageSource()
     * @see #driver()
     */
    @NotNull
    default  <T> Flowable<T> rxv_errorWithPageSource(@NotNull String error) {
        final DriverContainerType<?> THIS = this;

        return Flowable.error(() -> {
//            LogUtil.println(THIS.driver().getPageSource());
            return new Exception(error);
        });
    }

    /**
     * Same as above, but uses an empty {@link String} error.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #rxv_errorWithPageSource(String)
     */
    @NotNull
    default <T> Flowable<T> rxv_errorWithPageSource() {
        return rxv_errorWithPageSource("");
    }
}
