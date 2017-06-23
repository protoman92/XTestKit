package org.swiften.xtestkit.ios.element.locator;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.locator.LocatorType;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.javautilities.protocol.ClassNameProviderType;

/**
 * Created by haipham on 1/6/17.
 */

/**
 * This interface provides methods to locate {@link org.openqa.selenium.WebElement}
 * for {@link Platform#IOS}.
 */
public interface IOSLocatorType extends LocatorType<IOSDriver<IOSElement>> {
    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see LocatorType#rxe_statusBar()
     * @see IOSView.Type#UI_STATUS_BAR
     * @see #rxe_ofClass(ClassNameProviderType[])
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_statusBar() {
        return rxe_ofClass(IOSView.Type.UI_STATUS_BAR).firstElement().toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see LocatorType#rxe_window()
     * @see IOSView.Type#UI_WINDOW
     * @see #rxe_ofClass(ClassNameProviderType[])
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_window() {
        return rxe_ofClass(IOSView.Type.UI_WINDOW).firstElement().toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see LocatorType#rxe_imageViews()
     * @see IOSView.Type#UI_IMAGE_VIEW
     * @see #rxe_ofClass(ClassNameProviderType[])
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_imageViews() {
        return rxe_ofClass(IOSView.Type.UI_IMAGE_VIEW);
    }
}
