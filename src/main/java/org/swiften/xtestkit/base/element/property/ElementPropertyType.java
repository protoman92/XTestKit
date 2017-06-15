package org.swiften.xtestkit.base.element.property;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkitcomponents.coordinate.RLPositionType;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.common.BaseErrorType;

/**
 * This interface provides interaction capabilities for
 * {@link org.openqa.selenium.WebElement}. Accessor methods are defined here,
 * but individual {@link PlatformType}
 * subclasses will need its own implementations.
 */
public interface ElementPropertyType extends BaseErrorType {
    /**
     * Get text from {@link WebElement}, assuming that this {@link WebElement}
     * is capable of displaying a text.
     * @param element The {@link WebElement} to be inspected.
     * @return {@link String} value.
     */
    @NotNull
    default String getText(@NotNull WebElement element) {
        String text = element.getText();
        LogUtil.printft("Text '%s' for element %s", text, element);
        return text;
    }

    /**
     * Check if a {@link WebElement} is focused.
     * @param element {@link WebElement} instance.
     * @return {@link Boolean} value.
     * @see BooleanUtil#isTrue(Object)
     * @see WebElement#getAttribute(String)
     */
    default boolean isFocused(@NotNull WebElement element) {
        return BooleanUtil.isTrue(element.getAttribute("focused"));
    }

    /**
     * Check if two {@link WebElement} have the same {@link Point} and
     * {@link Dimension}.
     * Since we cannot directly compare two WebElement instances, we can
     * use a proxy method: by comparing their position and dimension.
     * @param element1 {@link WebElement} instance.
     * @param element2 {@link WebElement} instance.
     * @return {@link Boolean} value.
     * @see WebElement#getLocation()
     * @see WebElement#getSize()
     */
    default boolean sameOriginAndSize(@NotNull WebElement element1,
                                      @NotNull WebElement element2) {
        Point l1 = element1.getLocation(), l2 = element2.getLocation();
        Dimension d1 = element1.getSize(), d2 = element2.getSize();
        return l1.equals(l2) && d1.equals(d2);
    }

    /**
     * Get {@link Point} coordinate relative to {@link WebElement}.
     * @param element {@link WebElement} instance.
     * @param horizontalRatio {@link RLPositionType} instance.
     * @param verticalRatio {@link RLPositionType} instance.
     * @return {@link Point} instance.
     * @see Dimension#getHeight()
     * @see Dimension#getWidth()
     * @see RLPositionType#dimensionRatio()
     * @see WebElement#getLocation()
     * @see WebElement#getSize()
     */
    @NotNull
    default Point coordinate(@NotNull WebElement element,
                             @NotNull RLPositionType horizontalRatio,
                             @NotNull RLPositionType verticalRatio) {
        Point point = element.getLocation();
        Dimension size = element.getSize();
        int x = point.getX(), y = point.getY();
        int width = size.getWidth(), height = size.getHeight();
        double hRatio = horizontalRatio.dimensionRatio();
        double vRatio = verticalRatio.dimensionRatio();
        int offsetWidth = (int)(width * hRatio);
        int offsetHeight = (int)(height * vRatio);
        int newX = x + offsetWidth, newY = y + offsetHeight;
        return new Point(newX, newY);
    }
}
