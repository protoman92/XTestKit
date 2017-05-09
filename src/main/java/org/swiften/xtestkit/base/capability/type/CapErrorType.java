package org.swiften.xtestkit.base.capability.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.PlatformErrorType;

/**
 * Created by haipham on 5/7/17.
 */
public interface CapErrorType extends PlatformErrorType {
    @NotNull String TEST_MODE_UNAVAILABLE = "Test mode unavailable";
}
