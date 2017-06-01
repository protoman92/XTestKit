package org.swiften.xtestkit.ios.element.locator;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.locator.type.BaseLocatorType;
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkit.ios.IOSView;

/**
 * Created by haipham on 1/6/17.
 */

/**
 * This interface provides methods to locate {@link org.openqa.selenium.WebElement}
 * for {@link org.swiften.xtestkit.mobile.Platform#IOS}.
 */
public interface IOSLocatorType extends BaseLocatorType<IOSDriver<IOSElement>> {
    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see BaseLocatorType#rxe_statusBar()
     * @see BaseViewType#className()
     * @see IOSView.ViewType#UI_STATUSBAR
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_statusBar() {
        return this
            .rxe_ofClass(IOSView.ViewType.UI_STATUSBAR.className())
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
        return this
            .rxe_ofClass(IOSView.ViewType.UI_WINDOW.className())
            .firstElement().toFlowable();
    }
}
