package org.swiften.xtestkit.android.element.locator;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.android.AndroidView;
import org.swiften.xtestkit.base.element.locator.LocatorType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.view.BaseViewType;
import org.swiften.xtestkitcomponents.xpath.CompoundAttribute;
import org.swiften.xtestkitcomponents.xpath.XPath;

/**
 * Created by haipham on 1/6/17.
 */

/**
 * This interface provides methods to locate {@link org.openqa.selenium.WebElement}
 * for {@link Platform#ANDROID}.
 */
public interface AndroidLocatorType extends LocatorType<AndroidDriver<AndroidElement>> {
    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see LocatorType#rxe_statusBar()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_statusBar() {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see LocatorType#rxe_window()
     * @see BaseViewType#className()
     * @see CompoundAttribute#empty()
     * @see CompoundAttribute#withClass(String)
     * @see CompoundAttribute#withIndex(Integer)
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     * @see AndroidView.Type#FRAME_LAYOUT
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_window() {
        CompoundAttribute cAttr = CompoundAttribute.empty()
            .withClass(AndroidView.Type.FRAME_LAYOUT.className())
            .withIndex(1);

        XPath xpath = XPath.builder().addAttribute(cAttr).build();
        return rxe_withXPath(xpath).firstElement().toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see LocatorType#rxe_imageViews()
     * @see AndroidView.Type#IMAGE_VIEW
     * @see BaseViewType#className()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_imageViews() {
        return rxe_ofClass(AndroidView.Type.IMAGE_VIEW.className());
    }
}
