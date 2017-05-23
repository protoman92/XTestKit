package org.swiften.xtestkit.base.element.action.input.type;

/**
 * Created by haipham on 18/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.choice.type.ChoiceSelectorSwipeType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.PlatformType;

/**
 * This interface provides methods to help interact with choice-based input
 * {@link org.openqa.selenium.WebElement}.
 */
public interface ChoiceInputType {
    /**
     * Get the scroll view picker {@link XPath} for
     * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
     * @param platform {@link PlatformType} instance.
     * @return {@link XPath} value.
     */
    @NotNull
    XPath choicePickerScrollViewXPath(@NotNull PlatformType platform);

    /**
     * Get the item {@link XPath} for each item found in the
     * {@link org.openqa.selenium.WebElement} whose {@link XPath} is the
     * {@link #choicePickerScrollViewXPath(PlatformType)}.
     * @param platform {@link PlatformType} instance.
     * @return {@link XPath} instance.
     * @see #choicePickerScrollViewXPath(PlatformType)
     */
    @NotNull
    XPath choicePickerScrollViewItemXPath(@NotNull PlatformType platform);

    /**
     * Get the target choice item {@link XPath} instance. This item should be
     * displaying the input text we are interested in.
     * @param platform {@link PlatformType} instance.
     * @param selected {@link String} value of the selected choice.
     * @return {@link XPath} instance.
     */
    @NotNull
    XPath targetChoiceItemXPath(@NotNull PlatformType platform,
                                @NotNull String selected);

    /**
     * Convert {@link String} value into a numeric value. This is done so that
     * we can compare {@link String} values and decide how many initial swipes
     * are needed. Even if the {@link String} values are not directly
     * convertible into a {@link Number}, we need to find a way to do so (e.g.
     * for an {@link Enum}, use the index of each {@link Enum} case).
     * @param value {@link String} value.
     * @return {@link Double} value.
     */
    default double numericValue(@NotNull String value) {
        return Double.valueOf(value);
    }

    /**
     * Get the numeric value's {@link String} representation.
     * @param value {@link Double} value.
     * @return {@link String} value.
     */
    @NotNull
    default String stringValue(double value) {
        return String.valueOf(value);
    }

    /**
     * Get the swipe ratio to use with
     * {@link ChoiceSelectorSwipeType#rx_elementSwipeRatio()}.
     * @param platform {@link PlatformType} instance.
     * @return {@link Double} value.
     */
    default double swipeRatio(@NotNull PlatformType platform) {
        return 0.7d;
    }
}
