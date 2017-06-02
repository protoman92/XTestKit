package org.swiften.xtestkit.base.model;

/**
 * Created by haipham on 18/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.element.choice.AndroidChoiceSwipeSelectorType;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.base.type.PlatformType;

/**
 * This interface provides methods to help interact with choice-based input
 * {@link org.openqa.selenium.WebElement}.
 */
public interface ChoiceInputType {
    /**
     * Get the index of the choice picker, depending on the {@link PlatformType}
     * being tested. If there is only one choice picker present on the screen,
     * return 0.
     * This is useful when there are multiple choice pickers side-by-side
     * with the same id/accessibility.
     * @param platform {@link PlatformType} instance.
     * @return {@link Integer} value.
     */
    default int scrollablePickerIndex(@NotNull PlatformType platform) {
        return 0;
    }

    /**
     * Get the scroll view picker {@link XPath} for
     * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
     * @param platform {@link PlatformType} instance.
     * @return {@link XPath} value.
     */
    @NotNull
    XPath choicePickerXPath(@NotNull PlatformType platform);

    /**
     * Get the item {@link XPath} for each item found in the
     * {@link org.openqa.selenium.WebElement} whose {@link XPath} is the
     * {@link #choicePickerXPath(PlatformType)}.
     * @param platform {@link PlatformType} instance.
     * @return {@link XPath} instance.
     * @see #choicePickerXPath(PlatformType)
     */
    @NotNull XPath choicePickerItemXPath(@NotNull PlatformType platform);

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
     * {@link AndroidChoiceSwipeSelectorType#rxe_elementSwipeRatio()}.
     * @param platform {@link PlatformType} instance.
     * @return {@link Double} value.
     */
    default double swipeRatio(@NotNull PlatformType platform) {
        return 0.7d;
    }
}
