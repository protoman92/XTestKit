package org.swiften.xtestkit.base.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;

/**
 * Created by haipham on 3/19/17.
 */

/**
 * This interface provides errors for
 * {@link Engine}.
 */
public interface BaseEngineErrorType extends BaseErrorType {
    @NotNull String APPIUM_NOT_INSTALLED = "Appium not installed";
    @NotNull String CAPABILITY_UNAVAILABLE = "Capability unavailable";
    @NotNull String DRIVER_UNAVAILABLE = "Driver unavailable";
    @NotNull String PLATFORM_VIEW_UNAVAILABLE = "Platform view unavailable";
    @NotNull String TEXT_DELEGATE_UNAVAILABLE = "Text delegate unavailable";
    @NotNull String INSUFFICIENT_SETTINGS = "Insufficient settings";
}
