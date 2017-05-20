package org.swiften.xtestkit.base.capability.type;

/**
 * Created by haipham on 5/7/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.type.BaseErrorType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkit.base.type.PlatformType;

import java.util.Collection;
import java.util.Map;

/**
 * Implement this interface to selectively provide capabilities for
 * {@link Engine}.
 */
public interface CapType extends BaseErrorType {
    /**
     * Get the {@link Platform} for which we are getting capabilities.
     * @return A {@link Platform} instance.
     */
    @NotNull
    PlatformType platform();

    /**
     * Get the {@link TestMode} for which we are getting capabilities.
     * @return A {@link TestMode} instance.
     */
    @NotNull
    TestMode testMode();

    /**
     * Get a {@link Collection} of {@link String} that contains the required
     * capability names.
     * @return A {@link Collection} of {@link String}.
     * @see org.openqa.selenium.remote.CapabilityType
     * @see io.appium.java_client.remote.MobileCapabilityType
     * @see io.appium.java_client.remote.AndroidMobileCapabilityType
     * @see io.appium.java_client.remote.IOSMobileCapabilityType
     */
    @NotNull
    Collection<String> requiredCapabilities();

    /**
     * Check if a {@link Map} of capabilities contains all required
     * capabilities.
     * If this method returns false, we should throw an {@link Exception}.
     * @return A {@link Boolean} value.
     */
    boolean isComplete(@NotNull Map<String,Object> information);

    /**
     * Get all required capabilities from a {@link Map} of capabilities,
     * taking into account the {@link Platform}
     * and other information.
     * @param capabilities A {@link Map} that contains all available
     *                     capabilities.
     * @return A {@link Map} that contains only required capabilities.
     */
    @NotNull
    Map<String,Object> distill(@NotNull Map<String,Object> capabilities);

    /**
     * Builder class for {@link CapType}.
     */
    interface Builder {
        /**
         * Set the {@link #platform} instance.
         * @param platform A {@link Platform} instance.
         * @return The current {@link Builder} instance.
         */
        Builder withPlatform(@NotNull PlatformType platform);

        /**
         * Set the {@link #testMode} instance.
         * @param testMode A {@link TestMode} instance.
         * @return The current {@link Builder} instance.
         */
        Builder withTestMode(@NotNull TestMode testMode);

        /**
         * Return a {@link CapType} instance.
         * @return A {@link CapType} instance.
         */
        CapType build();
    }
}
