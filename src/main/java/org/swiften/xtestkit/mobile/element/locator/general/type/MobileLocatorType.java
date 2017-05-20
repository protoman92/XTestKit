package org.swiften.xtestkit.mobile.element.locator.general.type;

import io.appium.java_client.MobileDriver;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.type.PlatformNameContainerType;
import org.swiften.xtestkit.base.type.PlatformType;
import org.swiften.xtestkit.mobile.Platform;

import java.util.Optional;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides general locator capabilities for {@link MobileDriver}.
 * @param <D> Generics parameter that extends {@link MobileDriver}.
 */
public interface MobileLocatorType<D extends MobileDriver> extends
    BaseLocatorType<D>, PlatformNameContainerType
{
    /**
     * @return {@link PlatformType} instance.
     * @see Engine#platform()
     */
    @NotNull
    @Override
    default PlatformType platform() {
        Optional<Platform> platform = Platform.fromValue(platformName());

        if (platform.isPresent()) {
            return platform.get();
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}
