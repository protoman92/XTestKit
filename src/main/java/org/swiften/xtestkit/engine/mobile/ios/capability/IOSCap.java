package org.swiften.xtestkit.engine.mobile.ios.capability;

import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.mobile.capability.MobileCap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by haipham on 5/7/17.
 */
public class IOSCap extends MobileCap {
    /**
     * Get a {@link Builder} instance.
     * @return A {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    @Override
    public List<String> requiredCapabilities() {
        List<String> parent = new ArrayList<>(super.requiredCapabilities());
        Collections.addAll(parent, MobileCapabilityType.UDID);
        return parent;
    }

    @Override
    public boolean isComplete(@NotNull Map<String,Object> capabilities) {
        String appPath = appPath(capabilities);
        return hasCorrectAppName(appPath) && super.isComplete(capabilities);
    }

    /**
     * Check whether the app file extension matches {@link #testMode()}.
     * @param appName A {@link String} value.
     * @return A {@link Boolean} value.
     * @see #testMode()
     */
    public boolean hasCorrectAppName(@NotNull String appName) {
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
     * Builder class for {@link IOSCap}.
     */
    public static final class Builder extends MobileCap.Builder<IOSCap> {
        Builder() {
            super(new IOSCap());
        }
    }
    //endregion
}
