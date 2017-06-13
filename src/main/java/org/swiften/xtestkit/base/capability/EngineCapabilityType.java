package org.swiften.xtestkit.base.capability;

/**
 * Created by haipham on 5/7/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkitcomponents.platform.PlatformType;

import java.util.Collection;
import java.util.Map;

/**
 * Implement this interface to selectively provide capabilities for
 * {@link Engine}.
 */
public interface EngineCapabilityType extends BaseErrorType {
    /**
     * Get the {@link Platform} for which we are getting capabilities.
     * @return {@link Platform} instance.
     */
    @NotNull
    PlatformType platform();

    /**
     * Get the {@link TestMode} for which we are getting capabilities.
     * @return {@link TestMode} instance.
     */
    @NotNull
    TestMode testMode();

    /**
     * Get {@link Collection} of {@link String} that contains the required
     * capability names.
     * @return {@link Collection} of {@link String}.
     * @see org.openqa.selenium.remote.CapabilityType
     * @see io.appium.java_client.remote.MobileCapabilityType
     * @see io.appium.java_client.remote.AndroidMobileCapabilityType
     * @see io.appium.java_client.remote.IOSMobileCapabilityType
     */
    @NotNull
    Collection<String> requiredCapabilities();

    /**
     * Check if {@link Map} of capabilities contains all required
     * capabilities.
     * If this method returns false, we should throw {@link Exception}.
     * @return {@link Boolean} value.
     */
    boolean isComplete(@NotNull Map<String,Object> information);

    /**
     * Get all required capabilities from {@link Map} of capabilities,
     * taking into account the {@link Platform}
     * and other information.
     * @param capabilities {@link Map} that contains all available
     *                     capabilities.
     * @return {@link Map} that contains only required capabilities.
     */
    @NotNull
    Map<String,Object> distill(@NotNull Map<String,Object> capabilities);

    /**
     * Builder class for {@link EngineCapabilityType}.
     */
    interface Builder {
        /**
         * Set the {@link #testMode} instance.
         * @param testMode {@link TestMode} instance.
         * @return {@link Builder} instance.
         */
        Builder withTestMode(@NotNull TestMode testMode);

        /**
         * Return {@link EngineCapabilityType} instance.
         * @return {@link EngineCapabilityType} instance.
         */
        EngineCapabilityType build();
    }
}
