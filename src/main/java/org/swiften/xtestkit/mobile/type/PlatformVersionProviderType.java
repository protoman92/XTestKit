package org.swiften.xtestkit.mobile.type;

/**
 * Created by haipham on 6/13/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides platform version.
 */
@FunctionalInterface
public interface PlatformVersionProviderType {
    @NotNull String platformVersion();
}
