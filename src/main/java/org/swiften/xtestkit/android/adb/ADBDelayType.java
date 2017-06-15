package org.swiften.xtestkit.android.adb;

import org.swiften.xtestkit.android.param.StartEmulatorParam;
import org.swiften.xtestkit.android.type.DeviceUIDType;

/**
 * Created by haipham on 4/8/17.
 */

/**
 * This interface provides delay durations for {@link ADBHandler}.
 */
public interface ADBDelayType {
    /**
     * Use this timeout for
     * {@link ADBHandler#rxa_startEmulator(StartEmulatorParam)}.
     * @return {@link Long} value.
     */
    default long emulatorBootTimeout() {
        return 60000;
    }

    /**
     * Use this timeout for
     * {@link ADBHandler#rxa_startEmulator(StartEmulatorParam)}.
     * @return {@link Long} value.
     */
    default long emulatorBootRetryDelay() {
        return 1000;
    }

    /**
     * Use this timeout for operations that change device/emulator settings,
     * such as
     * {@link ADBHandler#rxa_disableAnimations(DeviceUIDType)}.
     * @return {@link Long} value.
     */
    default long emulatorSettingTimeout() {
        return 5000;
    }
}
