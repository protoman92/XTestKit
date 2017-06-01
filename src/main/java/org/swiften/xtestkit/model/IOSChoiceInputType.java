package org.swiften.xtestkit.model;

/**
 * Created by haipham on 25/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkit.mobile.Platform;

/**
 * This interface provides choice-related methods for
 * {@link org.swiften.xtestkit.mobile.Platform#IOS}. It improves upon
 * {@link ChoiceInputType} by providing default implementations that can be
 * used in a variety of situations.
 */
public interface IOSChoiceInputType extends ChoiceInputType {
    /**
     * Get the index associated with the {@link org.openqa.selenium.WebElement}
     * with which we are selecting a value for the current input.
     * @return {@link Integer} value.
     */
    default int iOSScrollablePickerIndex() {
        return 0;
    }

    /**
     * Get the scroll view picker {@link XPath} for {@link Platform#IOS}.
     * @return {@link XPath} instance.
     * @see Platform#IOS
     * @see IOSView.ViewType#UI_PICKERWHEEL
     */
    @NotNull
    default XPath iOSScrollViewPickerXPath() {
        String cls = IOSView.ViewType.UI_PICKERWHEEL.className();
        return XPath.builder(Platform.IOS).setClass(cls).build();
    }
}
