package org.swiften.xtestkit.mobile.android.capability;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.mobile.capability.MobileCap;
import org.swiften.xtestkit.mobile.ios.capability.IOSCap;

import java.util.*;

/**
 * Created by haipham on 5/7/17.
 */
public class AndroidCap extends MobileCap {
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

        Collections.addAll(parent,
            AndroidMobileCapabilityType.APP_ACTIVITY,
            AndroidMobileCapabilityType.APP_PACKAGE,
            AndroidMobileCapabilityType.AVD);

        return parent;
    }

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
     * Builder class for {@link IOSCap}.
     */
    public static final class Builder extends MobileCap.Builder<AndroidCap> {
        Builder() {
            super(new AndroidCap());
        }
    }
    //endregion
}
