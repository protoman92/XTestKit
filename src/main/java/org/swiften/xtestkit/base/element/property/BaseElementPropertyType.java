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
import org.swiften.xtestkit.base.type.BaseErrorType;

/**
 * This interface provides interaction capabilities for
 * {@link org.openqa.selenium.WebElement}. Accessor methods are defined here,
 * but individual {@link org.swiften.xtestkit.base.type.PlatformType}
 * subclasses will need its own implementations.
 */
public interface BaseElementPropertyType extends BaseErrorType {
    /**
     * Get text from {@link WebElement}, assuming that this {@link WebElement}
     * is capable of displaying a text.
     * @param element The {@link WebElement} to be inspected.
     * @return {@link String} value.
     */
    @NotNull
    default String getText(@NotNull WebElement element) {
        String text = element.getText();
        LogUtil.printfThread("Text '%s' for element %s", text, element);
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
     * Get the middle coordinate of {@link WebElement} instance.
     * @param element {@link WebElement} instance.
     * @return {@link Point} instance.
     * @see WebElement#getLocation()
     * @see WebElement#getSize()
     * @see Point#getX()
     * @see Point#getY()
     * @see Dimension#getWidth()
     * @see Dimension#getHeight()
     */
    @NotNull
    default Point getMiddleCoordinate(@NotNull WebElement element) {
        Point origin = element.getLocation();
        Dimension size = element.getSize();
        int x = origin.getX() + size.getWidth() / 2;
        int y = origin.getY() + size.getHeight() / 2;
        return new Point(x, y);
    }
}
