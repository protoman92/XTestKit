package org.swiften.xtestkit.android.element.locator;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.android.AndroidView;
import org.swiften.xtestkit.base.element.locator.type.BaseLocatorType;
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 1/6/17.
 */

/**
 * This interface provides methods to locate {@link org.openqa.selenium.WebElement}
 * for {@link Platform#ANDROID}.
 */
public interface AndroidLocatorType extends BaseLocatorType<AndroidDriver<AndroidElement>> {
    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see BaseLocatorType#rxe_statusBar()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_statusBar() {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see BaseLocatorType#rxe_window()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_window() {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see BaseLocatorType#rxe_imageViews()
     * @see AndroidView.ViewType#IMAGE_VIEW
     * @see BaseViewType#className()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_imageViews() {
        return rxe_ofClass(AndroidView.ViewType.IMAGE_VIEW.className());
    }
}
