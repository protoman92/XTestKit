package org.swiften.xtestkit.base.model;

/**
 * Created by haipham on 18/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.element.choice.AndroidChoiceMultiSwipeType;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.xpath.XPath;

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
     * @param helper {@link InputHelperType} instance.
     * @return {@link Integer} value.
     */
    default int scrollablePickerIndex(@NotNull InputHelperType helper) {
        return 0;
    }

    /**
     * Get the scroll view picker {@link XPath} for
     * {@link Platform#ANDROID}.
     * @param helper {@link PlatformType} instance.
     * @return {@link XPath} value.
     */
    @NotNull XPath choicePickerXP(@NotNull InputHelperType helper);

    /**
     * Get the item {@link XPath} for each item found in the
     * {@link org.openqa.selenium.WebElement} whose {@link XPath} is the
     * {@link #choicePickerXP(InputHelperType)}.
     * @param helper {@link PlatformType} instance.
     * @return {@link XPath} instance.
     * @see #choicePickerXP(InputHelperType)
     */
    @NotNull XPath choicePickerItemXP(@NotNull InputHelperType helper);

    /**
     * Convert {@link String} value into a numeric value. This is done so that
     * we can compare {@link String} values and decide how many initial swipes
     * are needed. Even if the {@link String} values are not directly
     * convertible into a {@link Number}, we need to find a way to do so (e.g.
     * for an {@link Enum}, use the index of each {@link Enum} case).
     * @param helper {@link InputHelperType} instance.
     * @param value {@link String} value.
     * @return {@link Double} value.
     */
    default double numericValue(@NotNull InputHelperType helper,
                                @NotNull String value) {
        return Double.valueOf(value);
    }

    /**
     * Get the numeric value's {@link String} representation.
     * @param helper {@link InputHelperType} instance.
     * @param value {@link Double} value.
     * @return {@link String} value.
     */
    @NotNull
    default String stringValue(@NotNull InputHelperType helper, double value) {
        return String.valueOf(value);
    }

    /**
     * Get the swipe ratio to use with
     * {@link AndroidChoiceMultiSwipeType#rxe_elementSwipeRatio()}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Double} value.
     */
    default double swipeRatio(@NotNull InputHelperType helper) {
        return 1d;
    }

    /**
     * Get the step value which represents the difference between two
     * consecutive {@link #numericValue(InputHelperType, String)}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link Double} value.
     */
    default double numericValueStep(@NotNull InputHelperType helper) {
        return 1;
    }
}
