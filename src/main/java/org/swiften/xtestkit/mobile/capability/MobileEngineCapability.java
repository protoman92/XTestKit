package org.swiften.xtestkit.mobile.capability;

import io.appium.java_client.remote.MobileCapabilityType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkit.base.capability.EngineCapability;

import java.io.File;
import java.util.*;

/**
 * Created by haipham on 5/7/17.
 */

/**
 * Extend this class to provide specific capabilities for mobile platforms.
 */
public abstract class MobileEngineCapability extends EngineCapability {
    /**
     * @return {@link List} of {@link String}.
     * @see EngineCapability#requiredCapabilities()
     */
    @NotNull
    @Override
    public List<String> requiredCapabilities() {
        List<String> parent = new ArrayList<>(super.requiredCapabilities());

        Collections.addAll(parent,
            MobileCapabilityType.APPIUM_VERSION,
            MobileCapabilityType.AUTOMATION_NAME,
            MobileCapabilityType.DEVICE_NAME,
            MobileCapabilityType.PLATFORM_NAME,
            MobileCapabilityType.PLATFORM_VERSION,
            "autoLaunch");

        return parent;
    }

    /**
     * @param capabilities {@link Map} instance.
     * @return {@link Map} instance.
     * @see EngineCapability#distill(Map)
     * @see TestMode#isTestingOnActualEnvironment()
     * @see #testMode()
     */
    @NotNull
    public Map<String,Object> distill(@NotNull Map<String,Object> capabilities) {
        Map<String,Object> result = new HashMap<>(super.distill(capabilities));

        /* Distill based on test mode */
        TestMode testMode = testMode();

        if (testMode.isTestingOnActualEnvironment()) {
            result.remove(MobileCapabilityType.PLATFORM_VERSION);
        }

        /* Take out app path if it does not exist */
        if (!new File(appPath(result)).exists()) {
            result.remove(MobileCapabilityType.APP);
        }

        return result;
    }

    /**
     * Get the app name as set in the capabilities {@link Map}.
     * @param capabilities {@link Map} of capabilities.
     * @return {@link String} value.
     */
    @NotNull
    public String appPath(@NotNull Map<String,Object> capabilities) {
        Object app = capabilities.get(MobileCapabilityType.APP);
        return app instanceof String ? (String)app : "";
    }

    //region Builder
    /**
     * Builder class for {@link MobileEngineCapability}.
     * @param <C> Generics parameter that extends {@link MobileEngineCapability}.
     */
    public static class Builder<C extends MobileEngineCapability>
        extends EngineCapability.Builder<C>
    {
        protected Builder(@NotNull C capability) {
            super(capability);
        }
    }
    //endregion
}
