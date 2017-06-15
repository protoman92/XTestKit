package org.swiften.xtestkit.ios.element.search;

/**
 * Created by haipham on 1/6/17.
 */

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.search.SearchActionType;
import org.swiften.xtestkit.base.element.locator.type.LocatorType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * This interface provides methods to handle search in
 * {@link Platform#IOS}.
 */
public interface IOSSearchActionType extends
    SearchActionType<IOSDriver<IOSElement>>,
    LocatorType<IOSDriver<IOSElement>>
{
    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see SearchActionType#rxe_textClear()
     * @see #rxe_containsID(String...)
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_textClear() {
        return rxe_containsID("clear text").firstElement().toFlowable();
    }
}
