package org.swiften.xtestkit.ios.element.locator;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.locator.type.BaseLocatorType;
import org.swiften.xtestkitcomponents.view.BaseViewType;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 1/6/17.
 */

/**
 * This interface provides methods to locate {@link org.openqa.selenium.WebElement}
 * for {@link Platform#IOS}.
 */
public interface IOSLocatorType extends BaseLocatorType<IOSDriver<IOSElement>> {
    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see BaseLocatorType#rxe_statusBar()
     * @see BaseViewType#className()
     * @see IOSView.ViewType#UI_STATUS_BAR
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_statusBar() {
        return this
            .rxe_ofClass(IOSView.ViewType.UI_STATUS_BAR.className())
            .firstElement().toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see BaseLocatorType#rxe_window()
     * @see BaseViewType#className()
     * @see IOSView.ViewType#UI_WINDOW
     * @see #rxe_ofClass(String...)
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_window() {
        String clsName = IOSView.ViewType.UI_WINDOW.className();
        return this.rxe_ofClass(clsName).firstElement().toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see BaseLocatorType#rxe_imageViews()
     * @see IOSView.ViewType#UI_IMAGE_VIEW
     * @see BaseViewType#className()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_imageViews() {
        return rxe_ofClass(IOSView.ViewType.UI_IMAGE_VIEW.className());
    }
}
