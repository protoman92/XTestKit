package org.swiften.xtestkit.android.element.search;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.search.SearchActionType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 1/6/17.
 */

/**
 * This interface provides methods to handle search for
 * {@link Platform#ANDROID}.
 */
public interface AndroidSearchActionType extends SearchActionType<AndroidDriver<AndroidElement>> {
    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_textClear() {
        throw new RuntimeException(NOT_AVAILABLE);
    }
}
