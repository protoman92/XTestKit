package org.swiften.xtestkit.base.type;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/10/17.
 */

/**
 * This interface provides an app package name.
 */
@FunctionalInterface
public interface AppPackageType {
    /**
     * Get an app package.
     * @return {@link String} value.
     */
    @NotNull String appPackage();
}
