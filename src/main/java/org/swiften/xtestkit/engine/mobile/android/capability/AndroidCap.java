package org.swiften.xtestkit.engine.mobile.android.capability;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.mobile.capability.MobileCap;
import org.swiften.xtestkit.engine.mobile.ios.capability.IOSCap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            AndroidMobileCapabilityType.APP_PACKAGE);

        if (testMode().isTestingOnSimulatedEnvironment()) {
            parent.add(AndroidMobileCapabilityType.AVD);
        }

        return parent;
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
