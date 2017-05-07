package org.swiften.xtestkit.engine.mobile.capability;

import io.appium.java_client.remote.MobileCapabilityType;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.string.StringUtil;
import org.swiften.xtestkit.engine.base.TestMode;
import org.swiften.xtestkit.engine.base.capability.BaseCap;

import java.util.*;

/**
 * Created by haipham on 5/7/17.
 */
public class MobileCap extends BaseCap {
    @NotNull
    @Override
    public List<String> requiredCapabilities() {
        List<String> parent = new ArrayList<>(super.requiredCapabilities());

        Collections.addAll(parent,
            MobileCapabilityType.APPIUM_VERSION,
            MobileCapabilityType.AUTOMATION_NAME,
            MobileCapabilityType.DEVICE_NAME,
            MobileCapabilityType.PLATFORM_NAME,
            "autoLaunch");

        if (testMode().isTestingOnSimulatedEnvironment()) {
            parent.add(MobileCapabilityType.PLATFORM_VERSION);
        }

        return parent;
    }

    @NotNull
    public Map<String,Object> distill(@NotNull Map<String,Object> capabilities) {
        Map<String,Object> result = new HashMap<>(super.distill(capabilities));
        result.putAll(capabilities);

        /* Distill based on test mode */
        TestMode testMode = testMode();

        if (testMode.isTestingOnSimulatedEnvironment()) {
            result.remove(MobileCapabilityType.PLATFORM_VERSION);
        }

        /* Take out app path if it's empty, i.e. the app is already installed
         * on an actual device */
        if (!StringUtil.isNotNullOrEmpty(appPath(result))) {
            result.remove(MobileCapabilityType.APP);
        }

        return result;
    }

    /**
     * Get the app name as set in the capabilities {@link Map}.
     * @param capabilities A {@link Map} of capabilities.
     * @return A {@link String} value.
     */
    @NotNull
    public String appPath(@NotNull Map<String,Object> capabilities) {
        Object app = capabilities.get(MobileCapabilityType.APP);
        return app instanceof String ? (String)app : "";
    }

    //region Builder
    /**
     * Builder class for {@link MobileCap}.
     * @param <C> Generics parameter that extends {@link MobileCap}.
     */
    public static class Builder<C extends MobileCap> extends BaseCap.Builder<C> {
        protected Builder(@NotNull C capability) {
            super(capability);
        }
    }
    //endregion
}
