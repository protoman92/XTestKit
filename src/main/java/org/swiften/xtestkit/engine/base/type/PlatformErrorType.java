package org.swiften.xtestkit.engine.base.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.capability.CapErrorType;

/**
 * Created by haipham on 3/19/17.
 */
public interface PlatformErrorType extends CapErrorType {
    String APPIUM_NOT_INSTALLED = "Appium not installed";
    String CAPABILITY_UNAVAILABLE = "Capability unavailable";
    String DRIVER_UNAVAILABLE = "Driver unavailable";
    String PLATFORM_VIEW_UNAVAILABLE = "Platform view unavailable";
    String TEXT_DELEGATE_UNAVAILABLE = "Text delegate unavailable";
    String INSUFFICIENT_SETTINGS = "Insufficient settings";
    String WRONG_DIRECTION = "Wrong direction";
}
