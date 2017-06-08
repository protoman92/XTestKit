package org.swiften.xtestkit.ios.capability;

import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.string.StringUtil;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.capability.MobileEngineCapability;

import java.util.*;

/**
 * Created by haipham on 5/7/17.
 */

/**
 * This class provides capabilities for
 * {@link Platform#IOS} and
 * {@link org.swiften.xtestkit.ios.IOSEngine}.
 */
public class IOSEngineCapability extends MobileEngineCapability {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get {@link Platform#IOS}.
     * @return {@link PlatformType} instance.
     * @see Platform#IOS
     */
    @NotNull
    @Override
    public PlatformType platform() {
        return Platform.IOS;
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

        /* 'app' is required for iOS, but not Android */
        Collections.addAll(parent, MobileCapabilityType.APP);
        return parent;
    }

    /**
     * Override this method to provide default implementation.
     * @param caps {@link Map} instance.
     * @return {@link Map} instance.
     * @see MobileEngineCapability#distill(Map)
     * @see TestMode#isTestingOnSimulatedEnvironment()
     * @see #testMode()
     */
    @NotNull
    @Override
    public Map<String,Object> distill(@NotNull Map<String,Object> caps) {
        Map<String,Object> result = new HashMap<>(super.distill(caps));

        /* If testing on simulator, we do not need UDID */
        if (testMode().isTestingOnSimulatedEnvironment()) {
            result.remove(MobileCapabilityType.UDID);
        }

        return result;
    }

    /**
     * Override this method to provide a additional check for the app's name,
     * since different {@link org.swiften.xtestkit.base.TestMode} requires
     * different app extensions.
     * @param capabilities {@link Map} instance.
     * @return {@link Boolean} value.
     * @see MobileEngineCapability#isComplete(Map)
     * @see StringUtil#isNotNullOrEmpty(String)
     * @see #hasValidAppName(String)
     */
    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isComplete(@NotNull Map<String,Object> capabilities) {
        String appPath = appPath(capabilities);

        if (StringUtil.isNotNullOrEmpty(appPath) && !hasValidAppName(appPath)) {
            return false;
        } else {
            return super.isComplete(capabilities);
        }
    }

    /**
     * Check whether the app file extension matches {@link #testMode()}.
     * @param appName {@link String} value.
     * @return {@link Boolean} value.
     * @see TestMode#ACTUAL
     * @see TestMode#SIMULATED
     * @see #testMode()
     */
    public boolean hasValidAppName(@NotNull String appName) {
        String extension = FilenameUtils.getExtension(appName);

        switch (testMode()) {
            case SIMULATED:
                return extension.equalsIgnoreCase("app");

            case ACTUAL:
                return extension.equalsIgnoreCase("ipa");

            default:
                return false;
        }
    }

    //region Builder.
    /**
     * Builder class for {@link IOSEngineCapability}.
     */
    public static class Builder extends
        MobileEngineCapability.Builder<IOSEngineCapability>
    {
        Builder() {
            super(new IOSEngineCapability());
        }
    }
    //endregion
}
