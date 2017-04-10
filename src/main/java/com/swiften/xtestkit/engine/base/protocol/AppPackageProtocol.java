package com.swiften.xtestkit.engine.base.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/10/17.
 */
public interface AppPackageProtocol {
    /**
     * Get an app package.
     * @return A {@link String} value.
     */
    @NotNull String appPackage();
}
