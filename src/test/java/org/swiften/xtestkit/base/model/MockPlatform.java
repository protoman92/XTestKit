package org.swiften.xtestkit.base.model;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkitcomponents.platform.PlatformType;

/**
 * Created by haipham on 5/8/17.
 */
public class MockPlatform implements PlatformType {
    @NotNull
    @Override
    public String value() {
        return "";
    }
}
