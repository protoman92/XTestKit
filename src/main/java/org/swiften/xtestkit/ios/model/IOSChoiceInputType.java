package org.swiften.xtestkit.ios.model;

/**
 * Created by haipham on 25/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkitcomponents.platform.Platform;
import org.swiften.xtestkitcomponents.xpath.XPath;

/**
 * This interface provides choice-related methods for
 * {@link Platform#IOS}. It improves upon
 * {@link ChoiceInputType} by providing default implementations that can be
 * used in a variety of situations.
 */
public interface IOSChoiceInputType {
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
     * @see IOSView.ViewType#UI_PICKER_WHEEL
     */
    @NotNull
    default XPath iOSScrollViewPickerXP() {
        String cls = IOSView.ViewType.UI_PICKER_WHEEL.className();
        return XPath.builder(Platform.IOS).addClass(cls).build();
    }
}
