package org.swiften.xtestkit.ios.model;

/**
 * Created by haipham on 25/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.javautilities.protocol.ClassNameProviderType;
import org.swiften.xtestkitcomponents.xpath.AttributeType;
import org.swiften.xtestkitcomponents.xpath.CompoundAttribute;
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
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     */
    default int iOSScrollablePickerIndex(@NotNull InputHelperType helper) {
        return 0;
    }

    /**
     * Get the scroll view picker {@link XPath} for {@link Platform#IOS}.
     * @param helper {@link InputHelperType} instance.*
     * @return {@link XPath} instance.
     * @see CompoundAttribute#forClass(ClassNameProviderType)
     * @see IOSView.Type#UI_PICKER_WHEEL
     * @see XPath.Builder#addAttribute(AttributeType)
     */
    @NotNull
    default XPath iOSScrollViewPickerXP(@NotNull InputHelperType helper) {
        ClassNameProviderType param = IOSView.Type.UI_PICKER_WHEEL;
        CompoundAttribute attribute = CompoundAttribute.forClass(param);
        return XPath.builder().addAttribute(attribute).build();
    }
}
