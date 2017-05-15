package org.swiften.xtestkit.mobile.android.element.action.password.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.action.tap.type.TapType;
import org.swiften.xtestkit.mobile.element.action.password.type.MobilePasswordActionType;

/**
 * Created by haipham on 5/15/17.
 */
public interface AndroidPasswordActionType<D extends AndroidDriver<AndroidElement>> extends
    MobilePasswordActionType<D>
{
    /**
     * Override this method to provide a default
     * {@link TapType}
     * to locate the password mask toggle.
     * @param element A {@link WebElement} instance.
     * @see MobilePasswordActionType#togglePasswordMask(WebElement)
     * @see WebElement#getLocation()
     * @see WebElement#getSize()
     * @see #tap(int, int)
     */
    @Override
    default void togglePasswordMask(@NotNull WebElement element) {
        Point point = element.getLocation();
        Dimension dimension = element.getSize();
        int x = point.getX(), y = point.getY();
        int height = dimension.getHeight(), width = dimension.getWidth();
        int maxX = x + width, maxY = y + height;
        tap(maxX - 5, maxY - height / 2);
    }
}
