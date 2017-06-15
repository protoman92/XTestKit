package org.swiften.xtestkit.android.element.password;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.swiften.xtestkit.base.element.tap.TapParamType;
import org.swiften.xtestkit.mobile.element.action.password.MobilePasswordActionType;

/**
 * Created by haipham on 5/15/17.
 */
public interface AndroidPasswordActionType extends
    MobilePasswordActionType<AndroidDriver<AndroidElement>>
{
    /**
     * Override this method to provide a default
     * {@link TapParamType}
     * to locate the password mask toggle.
     * @param element {@link WebElement} instance.
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
