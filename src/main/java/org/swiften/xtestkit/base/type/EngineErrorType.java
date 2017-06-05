package org.swiften.xtestkit.base.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.common.BaseErrorType;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * This interface provides errors for {@link Engine}.
 */
public interface EngineErrorType extends BaseErrorType {
    @NotNull String APPIUM_NOT_INSTALLED = "Appium not installed";
    @NotNull String CAPABILITY_UNAVAILABLE = "Capability unavailable";
    @NotNull String DRIVER_UNAVAILABLE = "Driver unavailable";
    @NotNull String INSUFFICIENT_SETTINGS = "Insufficient settings";
}
