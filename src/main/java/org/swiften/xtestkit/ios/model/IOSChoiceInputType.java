package org.swiften.xtestkit.ios.model;

/**
 * Created by haipham on 25/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.xpath.Attribute;
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
     * @see Attribute#forClass(String)
     * @see BaseViewType#className()
     * @see IOSView.ViewType#UI_PICKER_WHEEL
     * @see XPath.Builder#addAttribute(Attribute)
     */
    @NotNull
    default XPath iOSScrollViewPickerXP() {
        String cls = IOSView.ViewType.UI_PICKER_WHEEL.className();
        Attribute attribute = Attribute.forClass(cls);
        return XPath.builder().addAttribute(attribute).build();
    }
}
