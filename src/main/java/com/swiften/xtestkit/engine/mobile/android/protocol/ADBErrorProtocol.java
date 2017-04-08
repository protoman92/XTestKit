package com.swiften.xtestkit.engine.mobile.android.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/8/17.
 */
public interface ADBErrorProtocol {
    String ANDROID_HOME_NOT_SET = "Android Home is not set";
    String PROCESS_RUNNER_UNAVAILABLE = "Process runner not set";
    String NO_OUTPUT_EXPECTED = "No output expected";

    @NotNull
    default String changeSettingsFailed(@NotNull String setting) {
        return String.format("Unable to change settings %s", setting);
    }
}
