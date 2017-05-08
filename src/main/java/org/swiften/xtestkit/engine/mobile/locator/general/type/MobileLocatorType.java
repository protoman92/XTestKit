package org.swiften.xtestkit.engine.mobile.locator.general.type;

import io.appium.java_client.MobileDriver;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.BaseEngine;
import org.swiften.xtestkit.engine.base.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.engine.base.type.PlatformErrorType;
import org.swiften.xtestkit.engine.base.type.PlatformNameContainerType;
import org.swiften.xtestkit.engine.base.type.PlatformType;
import org.swiften.xtestkit.engine.mobile.Platform;

import java.util.Optional;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides general locator capabilities for {@link MobileDriver}.
 * @param <D> Generics parameter that extends {@link MobileDriver}.
 */
public interface MobileLocatorType<D extends MobileDriver> extends
    BaseLocatorType<D>,
    PlatformNameContainerType,
    PlatformErrorType
{
    /**
     * @return A {@link PlatformType} instance.
     * @see BaseEngine#platform()
     */
    @NotNull
    @Override
    default PlatformType platform() {
        Optional<Platform> platform = Platform.fromValue(platformName());

        if (platform.isPresent()) {
            return platform.get();
        } else {
            throw new RuntimeException(PLATFORM_UNAVAILABLE);
        }
    }
}
