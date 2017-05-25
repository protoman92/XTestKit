package org.swiften.xtestkit.android.adb;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/8/17.
 */
public interface ADBErrorType {
    String ANDROID_HOME_NOT_SET = "Android Home is not set";
    String PROCESS_RUNNER_UNAVAILABLE = "Process runner not set";
    String NO_OUTPUT_EXPECTED = "No output expected";
    String NO_PORT_AVAILABLE = "No port available";

    @NotNull
    default String appNotInstalled(@NotNull String app) {
        return String.format("%s not installed", app);
    }

    @NotNull
    default String changeSettingsFailed(@NotNull String setting) {
        return String.format("Unable to change settings %s", setting);
    }

    @NotNull
    default String unableToClearCache(@NotNull String app) {
        return String.format("Unable to clear cache for %s", app);
    }

    @NotNull
    default String unacceptablePort(int port) {
        return String.format("Unacceptable port: %d", port);
    }
}
