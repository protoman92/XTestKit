package org.swiften.xtestkit.android.type;

/**
 * Created by haipham on 6/13/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.mobile.type.PlatformVersionProviderType;

/**
 * This interface provides {@link AndroidSDK}.
 */
@FunctionalInterface
public interface AndroidSDKProviderType extends PlatformVersionProviderType {
    /**
     * Get the associated {@link AndroidSDK}.
     * @return {@link AndroidSDK} instance.
     * @see AndroidSDK#from(String)
     * @see #platformVersion()
     */
    @NotNull
    default AndroidSDK androidSDK() {
        String version = platformVersion();
        return AndroidSDK.from(version);
    }
}
