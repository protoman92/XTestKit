package org.swiften.xtestkit.ios.element.password;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.password.PasswordActionType;

/**
 * Created by haipham on 17/7/17.
 */
public interface IOSPasswordActionType extends PasswordActionType<IOSDriver<IOSElement>> {
    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     */
    @Override
    default void togglePasswordMask(@NotNull WebElement element) {
        throw new RuntimeException("Not supported on this platform");
    }
}
