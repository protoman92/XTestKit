package org.swiften.xtestkit.android.model;

/**
 * Created by haipham on 25/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.ByXPath;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.xpath.XPath;

/**
 * This interface provides choice-related methods for
 * {@link Platform#ANDROID}. It improves upon
 * {@link ChoiceInputType} by providing default implementations that can be
 * used in a variety of situations.
 */
public interface AndroidChoiceInputType extends ChoiceInputType {
    /**
     * Get the index associated with the {@link org.openqa.selenium.WebElement}
     * with which we are selecting a value for the current input.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     */
    default int androidScrollablePickerIndex(@NotNull InputHelperType helper) {
        return 0;
    };

    /**
     * Get a {@link ByXPath} instance that will be used to query for the
     * choice picker item.
     * @param helper {@link InputHelperType} instance.
     * @param selected The selected {@link String} choice.
     */
    @NotNull XPath androidTargetItemXP(@NotNull InputHelperType helper,
                                       @NotNull String selected);

    /**
     * Get the scroll view picker {@link XPath} for {@link Platform#ANDROID}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link XPath} instance.
     */
    @NotNull XPath androidChoicePickerXP(@NotNull InputHelperType helper);

    /**
     * Get the scroll view picker item {@link XPath} for {@link Platform#ANDROID}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link XPath} instance.
     */
    @NotNull XPath androidChoicePickerItemXP(@NotNull InputHelperType helper);
}
