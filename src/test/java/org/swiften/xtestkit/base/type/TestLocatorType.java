package org.swiften.xtestkit.base.type;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.locator.LocatorType;

/**
 * Created by haipham on 2/6/17.
 */
public interface TestLocatorType extends LocatorType<WebDriver> {
    @NotNull
    @Override
    default Flowable<WebElement> rxe_statusBar() {
        throw new RuntimeException(NO_SUCH_ELEMENT);
    }

    @NotNull
    @Override
    default Flowable<WebElement> rxe_imageViews() {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    @NotNull
    @Override
    default Flowable<WebElement> rxe_window() {
        throw new RuntimeException(NOT_AVAILABLE);
    }
}
