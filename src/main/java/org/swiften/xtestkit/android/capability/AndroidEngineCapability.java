package org.swiften.xtestkit.android.capability;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.capability.MobileEngineCapability;
import org.swiften.xtestkitcomponents.platform.PlatformType;

import java.util.*;

/**
 * Created by haipham on 5/7/17.
 */

/**
 * This class provides capabilities for
 * {@link Platform#ANDROID} and
 * {@link org.swiften.xtestkit.android.AndroidEngine}.
 */
public class AndroidEngineCapability extends MobileEngineCapability {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get {@link Platform#ANDROID}.
     * @return {@link PlatformType} instance.
     * @see Platform#ANDROID
     */
    @NotNull
    @Override
    public PlatformType platform() {
        return Platform.ANDROID;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link List} of {@link String}.
     * @see MobileEngineCapability#requiredCapabilities()
     */
    @NotNull
    @Override
    public List<String> requiredCapabilities() {
        List<String> parent = new ArrayList<>(super.requiredCapabilities());

        Collections.addAll(parent,
            AndroidMobileCapabilityType.APP_ACTIVITY,
            AndroidMobileCapabilityType.APP_PACKAGE,
            AndroidMobileCapabilityType.AVD);

        return parent;
    }

    /**
     * Override this method to provide default implementation.
     * @param capabilities {@link Map} instance.
     * @return {@link Map} instance.
     * @see MobileEngineCapability#distill(Map)
     * @see TestMode#isTestingOnActualEnvironment()
     * @see #testMode()
     */
    @NotNull
    public Map<String,Object> distill(@NotNull Map<String,Object> capabilities) {
        Map<String,Object> result = new HashMap<>(super.distill(capabilities));

        if (testMode().isTestingOnActualEnvironment()) {
            result.remove(AndroidMobileCapabilityType.AVD);
        }

        return result;
    }

    //region Builder.
    /**
     * Builder class for {@link AndroidEngineCapability}.
     */
    public static class Builder extends
        MobileEngineCapability.Builder<AndroidEngineCapability>
    {
        Builder() {
            super(new AndroidEngineCapability());
        }
    }
    //endregion
}
