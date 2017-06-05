package org.swiften.xtestkit.android.capability;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.platform.Platform;
import org.swiften.xtestkit.mobile.capability.MobileCapability;
import org.swiften.xtestkit.ios.capability.IOSCapability;

import java.util.*;

/**
 * Created by haipham on 5/7/17.
 */

/**
 * This class provides capabilities for
 * {@link Platform#ANDROID} and
 * {@link org.swiften.xtestkit.android.AndroidEngine}.
 */
public class AndroidCapability extends MobileCapability {
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
     * @return {@link List} of {@link String}.
     * @see MobileCapability#requiredCapabilities()
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
     * @param capabilities {@link Map} instance.
     * @return {@link Map} instance.
     * @see MobileCapability#distill(Map)
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
     * Builder class for {@link IOSCapability}.
     */
    public static class Builder extends MobileCapability.Builder<AndroidCapability> {
        Builder() {
            super(new AndroidCapability());
        }
    }
    //endregion
}
